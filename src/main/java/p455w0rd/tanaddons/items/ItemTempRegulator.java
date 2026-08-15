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

    public static final String TAG_ACTIVE = "Active";
    public static final int COOLDOWN_TICKS = 60;
    // Energy cost per full cycle (60 ticks × rfPerTick), deducted once per cycle
    private static final int ENERGY_PER_CYCLE = 60;

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
        if (player.level().isClientSide) return;

        // Optimization 1: Fast-fail — check self conditions first (zero-cost)
        boolean requireEnergy = ModConfig.COMMON.requireEnergy.get();
        int energyCostPerCycle = ModConfig.COMMON.portableTempRegulatorRFPerTick.get() * ENERGY_PER_CYCLE;
        if (requireEnergy && getEnergyStored(stack) < energyCostPerCycle) {
            setActiveDirty(stack, false);
            return;
        }

        // Optimization 2: GameTime modulo — skip 59 out of 60 ticks with zero computation.
        // Using stack identity hash to spread multiple items across different ticks (load smoothing).
        long gameTime = player.level().getGameTime();
        int offset = System.identityHashCode(stack) & 0x3F; // 0..63
        if ((gameTime + offset) % COOLDOWN_TICKS != 0) {
            return; // cold-path: no NBT, no reflection, no capability call
        }

        // Optimization 3: Perform the expensive cross-mod query only at the trigger tick
        if (CompatManager.isPlayerTemperatureAbnormal(player)) {
            setActiveDirty(stack, true);
            CompatManager.regulateTemperature(player);

            // Optimization 4: Batch deduct the full cycle's energy in one NBT write
            if (requireEnergy) {
                setEnergyStored(stack, getEnergyStored(stack) - energyCostPerCycle);
            }
        } else {
            setActiveDirty(stack, false);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            doTick(player, stack);
        }
    }

    /**
     * Optimization 5: Dirty-check — only write to NBT when the active state actually changes,
     * preventing unnecessary item slot sync packets.
     */
    private void setActiveDirty(ItemStack stack, boolean active) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_ACTIVE) != active) {
            tag.putBoolean(TAG_ACTIVE, active);
        }
    }

    public boolean isActive(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_ACTIVE);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack) && (!ModConfig.COMMON.requireEnergy.get() || getEnergyStored(stack) > 0);
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
                if (curioCap.isPresent()) return curioCap;
                return LazyOptional.empty();
            }
        };
    }
}
