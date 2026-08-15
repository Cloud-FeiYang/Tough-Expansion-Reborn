package p455w0rd.tanaddons.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import p455w0rd.tanaddons.compat.CompatManager;
import p455w0rd.tanaddons.compat.curios.CuriosCompat;
import p455w0rd.tanaddons.init.ModConfig;
import p455w0rd.tanaddons.util.ReadableNumberConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTempRegulator extends ItemForgeEnergy {

    public static final String TAG_TIME = "TimeStart";
    public static final String TAG_ACTIVE = "Active";

    public ItemTempRegulator() {
        super(ModConfig.COMMON.portableTempRegulatorRFCapacity.get(),
              ModConfig.COMMON.portableTempRegulatorRFCapacity.get(),
              ModConfig.COMMON.portableTempRegulatorRFPerTick.get() * 2);
    }

    @Override
    public int getEnergyCapacity() {
        return ModConfig.COMMON.portableTempRegulatorRFCapacity.get();
    }

    @Override
    public int getMaxReceive() {
        return ModConfig.COMMON.portableTempRegulatorRFCapacity.get();
    }

    @Override
    public int getMaxExtract() {
        return ModConfig.COMMON.portableTempRegulatorRFPerTick.get() * 2;
    }

    public void doTick(Player player, ItemStack stack) {
        if (player.level().isClientSide) {
            return;
        }

        boolean requireEnergy = ModConfig.COMMON.requireEnergy.get();
        int energyPerTick = ModConfig.COMMON.portableTempRegulatorRFPerTick.get();

        if (requireEnergy && getEnergyStored(stack) < energyPerTick) {
            setActive(stack, false);
            return;
        }

        if (CompatManager.isPlayerTemperatureAbnormal(player)) {
            setActive(stack, true);
            int currentTime = getTime(stack);
            if (currentTime <= 0) {
                CompatManager.regulateTemperature(player);
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
        return isActive(stack) && (!ModConfig.COMMON.requireEnergy.get() || getEnergyStored(stack) > ModConfig.COMMON.portableTempRegulatorRFPerTick.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (ModConfig.COMMON.requireEnergy.get()) {
            String energyStr = ReadableNumberConverter.INSTANCE.toWideReadableForm(getEnergyStored(stack))
                    + "/" + ReadableNumberConverter.INSTANCE.toWideReadableForm(getEnergyCapacity()) + " FE";
            tooltip.add(Component.literal(energyStr).withStyle(ChatFormatting.ITALIC, ChatFormatting.GOLD));
            tooltip.add(Component.empty());
        }

        tooltip.add(Component.translatable("tooltip.tanaddons.ptempregulator.desc").withStyle(ChatFormatting.GRAY));
        if (ModConfig.COMMON.requireEnergy.get()) {
            tooltip.add(Component.translatable("tooltip.tanaddons.ptempregulator.desc2").withStyle(ChatFormatting.DARK_GRAY));
        }

        if (CompatManager.isCuriosLoaded()) {
            tooltip.add(Component.translatable("tooltip.tanaddons.baublesitem", "Curios").withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ICapabilityProvider energyProvider = super.initCapabilities(stack, nbt);
        ICapabilityProvider curioProvider = CompatManager.isCuriosLoaded()
                ? CuriosCompat.initCuriosProvider(stack, player -> doTick(player, stack))
                : null;

        if (curioProvider == null) {
            return energyProvider;
        }

        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ENERGY) {
                    return energyProvider.getCapability(cap, side);
                }
                LazyOptional<T> curioCap = curioProvider.getCapability(cap, side);
                if (curioCap.isPresent()) {
                    return curioCap;
                }
                return LazyOptional.empty();
            }
        };
    }
}
