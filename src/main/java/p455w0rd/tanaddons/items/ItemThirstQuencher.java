package p455w0rd.tanaddons.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import p455w0rd.tanaddons.compat.CompatManager;
import p455w0rd.tanaddons.compat.curios.CuriosCompat;
import p455w0rd.tanaddons.init.ModConfig;
import p455w0rd.tanaddons.util.ReadableNumberConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemThirstQuencher extends ItemForgeEnergy {

    public static final String TAG_FLUID_STORED = "FluidStored";
    public static final String TAG_TIME = "TimeStart";
    public static final String TAG_ACTIVE = "Active";

    public ItemThirstQuencher() {
        super(ModConfig.COMMON.thirstQuencherRFCapacity.get(),
              ModConfig.COMMON.thirstQuencherRFCapacity.get(),
              ModConfig.COMMON.thirstQuencherRFPerTick.get() * 2);
    }

    @Override
    public int getEnergyCapacity() {
        return ModConfig.COMMON.thirstQuencherRFCapacity.get();
    }

    @Override
    public int getMaxReceive() {
        return ModConfig.COMMON.thirstQuencherRFCapacity.get();
    }

    @Override
    public int getMaxExtract() {
        return ModConfig.COMMON.thirstQuencherRFPerTick.get() * 2;
    }

    public int getFluidCapacity() {
        return ModConfig.COMMON.thirstQuencherWaterCapacity.get();
    }

    public int getFluidStored(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_FLUID_STORED) : 0;
    }

    public void setFluidStored(ItemStack stack, int amount) {
        int clamped = Math.max(0, Math.min(getFluidCapacity(), amount));
        stack.getOrCreateTag().putInt(TAG_FLUID_STORED, clamped);
    }

    public void drainFluid(ItemStack stack, int amount) {
        setFluidStored(stack, getFluidStored(stack) - amount);
    }

    public void doTick(Player player, ItemStack stack) {
        if (player.level().isClientSide) {
            return;
        }

        boolean requireEnergy = ModConfig.COMMON.requireEnergy.get();
        int energyPerTick = ModConfig.COMMON.thirstQuencherRFPerTick.get();

        if ((requireEnergy && getEnergyStored(stack) < energyPerTick) || getFluidStored(stack) < 100) {
            setActive(stack, false);
            return;
        }

        if (CompatManager.isPlayerThirsty(player)) {
            setActive(stack, true);
            int currentTime = getTime(stack);
            if (currentTime <= 0) {
                CompatManager.quenchThirst(player);
                drainFluid(stack, 100);
                setTime(stack, 20);
            }
            else {
                setTime(stack, currentTime - 1);
            }

            if (requireEnergy) {
                setEnergyStored(stack, getEnergyStored(stack) - energyPerTick);
            }
        }
        else {
            setActive(stack, false);
            if (getTime(stack) != 20) {
                setTime(stack, 20);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            doTick(player, stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int currentFluid = getFluidStored(stack);
        int maxFluid = getFluidCapacity();

        if (currentFluid >= maxFluid) {
            return InteractionResultHolder.pass(stack);
        }

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = level.getBlockState(pos);

            if (level.getFluidState(pos).is(Fluids.WATER) && level.getFluidState(pos).isSource()) {
                if (!level.isClientSide) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                    setFluidStored(stack, Math.min(currentFluid + 1000, maxFluid));
                }
                level.playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                LazyOptional<IFluidHandler> cap = be.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection());
                if (cap.isPresent()) {
                    IFluidHandler handler = cap.orElse(null);
                    if (handler != null) {
                        FluidStack drained = handler.drain(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.SIMULATE);
                        if (!drained.isEmpty() && drained.getAmount() > 0) {
                            if (!level.isClientSide) {
                                handler.drain(drained, IFluidHandler.FluidAction.EXECUTE);
                                setFluidStored(stack, Math.min(currentFluid + drained.getAmount(), maxFluid));
                            }
                            level.playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
                        }
                    }
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    private int getTime(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_TIME) ? tag.getInt(TAG_TIME) : 20;
    }

    private void setTime(ItemStack stack, int time) {
        stack.getOrCreateTag().putInt(TAG_TIME, time);
    }

    private void setActive(ItemStack stack, boolean active) {
        stack.getOrCreateTag().putBoolean(TAG_ACTIVE, active);
    }

    public boolean isActive(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_ACTIVE);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack) && (!ModConfig.COMMON.requireEnergy.get() || getEnergyStored(stack) > ModConfig.COMMON.thirstQuencherRFPerTick.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (ModConfig.COMMON.requireEnergy.get()) {
            String energyStr = ReadableNumberConverter.INSTANCE.toWideReadableForm(getEnergyStored(stack))
                    + "/" + ReadableNumberConverter.INSTANCE.toWideReadableForm(getEnergyCapacity()) + " FE";
            tooltip.add(Component.literal(energyStr).withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
        }

        String fluidStr = "Stored Water: " + getFluidStored(stack) + "/" + getFluidCapacity() + " mB";
        tooltip.add(Component.literal(fluidStr).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());

        tooltip.add(Component.translatable("tooltip.tanaddons.thirstquencher.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.tanaddons.thirstquencher.desc2").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.tanaddons.thirstquencher.desc3").withStyle(ChatFormatting.DARK_GRAY));

        if (CompatManager.isCuriosLoaded()) {
            tooltip.add(Component.translatable("tooltip.tanaddons.baublesitem", "Curios").withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ICapabilityProvider energyProvider = super.initCapabilities(stack, nbt);
        LazyOptional<IFluidHandlerItem> fluidHolder = LazyOptional.of(() -> new IFluidHandlerItem() {
            @Nonnull
            @Override
            public ItemStack getContainer() {
                return stack;
            }

            @Override
            public int getTanks() {
                return 1;
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return new FluidStack(Fluids.WATER, getFluidStored(stack));
            }

            @Override
            public int getTankCapacity(int tank) {
                return getFluidCapacity();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack resource) {
                return !resource.isEmpty() && resource.getFluid() == Fluids.WATER;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (resource.isEmpty() || resource.getFluid() != Fluids.WATER) {
                    return 0;
                }
                int stored = getFluidStored(stack);
                int accepted = Math.min(getFluidCapacity() - stored, resource.getAmount());
                if (action.execute() && accepted > 0) {
                    setFluidStored(stack, stored + accepted);
                }
                return accepted;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (resource.isEmpty() || resource.getFluid() != Fluids.WATER) {
                    return FluidStack.EMPTY;
                }
                return drain(resource.getAmount(), action);
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                int stored = getFluidStored(stack);
                int drained = Math.min(stored, maxDrain);
                if (action.execute() && drained > 0) {
                    setFluidStored(stack, stored - drained);
                }
                return drained > 0 ? new FluidStack(Fluids.WATER, drained) : FluidStack.EMPTY;
            }
        });

        ICapabilityProvider curioProvider = CompatManager.isCuriosLoaded()
                ? CuriosCompat.initCuriosProvider(stack, player -> doTick(player, stack))
                : null;

        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ENERGY) {
                    return energyProvider.getCapability(cap, side);
                }
                if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                    return fluidHolder.cast();
                }
                if (curioProvider != null) {
                    LazyOptional<T> curioCap = curioProvider.getCapability(cap, side);
                    if (curioCap.isPresent()) {
                        return curioCap;
                    }
                }
                return LazyOptional.empty();
            }
        };
    }
}
