package p455w0rd.tanaddons.items;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import p455w0rd.tanaddons.init.ModConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemForgeEnergy extends Item {

    public static final String TAG_ENERGY = "Energy";
    protected final int capacity;
    protected final int maxReceive;
    protected final int maxExtract;

    public ItemForgeEnergy(int capacity, int maxReceive, int maxExtract) {
        super(new Item.Properties().stacksTo(1));
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public int getEnergyCapacity() {
        return capacity;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public int getEnergyStored(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_ENERGY) : 0;
    }

    public void setEnergyStored(ItemStack stack, int energy) {
        int clamped = Math.max(0, Math.min(getEnergyCapacity(), energy));
        stack.getOrCreateTag().putInt(TAG_ENERGY, clamped);
    }

    public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        int stored = getEnergyStored(stack);
        int cap = getEnergyCapacity();
        int energyReceived = Math.min(cap - stored, Math.min(getMaxReceive(), maxReceive));
        if (!simulate && energyReceived > 0) {
            setEnergyStored(stack, stored + energyReceived);
        }
        return energyReceived;
    }

    public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        int stored = getEnergyStored(stack);
        int energyExtracted = Math.min(stored, Math.min(getMaxExtract(), maxExtract));
        if (!simulate && energyExtracted > 0) {
            setEnergyStored(stack, stored - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return ModConfig.COMMON.requireEnergy.get();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int cap = getEnergyCapacity();
        return cap > 0 ? Math.round(13.0F * (float) getEnergyStored(stack) / (float) cap) : 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int cap = getEnergyCapacity();
        float f = cap > 0 ? Math.max(0.0F, (float) getEnergyStored(stack) / (float) cap) : 0.0F;
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new EnergyCapabilityProvider(stack, this);
    }

    public static class EnergyCapabilityProvider implements ICapabilityProvider {
        private final ItemStack stack;
        private final ItemForgeEnergy item;
        private final LazyOptional<IEnergyStorage> holder;

        public EnergyCapabilityProvider(ItemStack stack, ItemForgeEnergy item) {
            this.stack = stack;
            this.item = item;
            this.holder = LazyOptional.of(() -> new IEnergyStorage() {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return item.receiveEnergy(stack, maxReceive, simulate);
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return item.extractEnergy(stack, maxExtract, simulate);
                }

                @Override
                public int getEnergyStored() {
                    return item.getEnergyStored(stack);
                }

                @Override
                public int getMaxEnergyStored() {
                    return item.getEnergyCapacity();
                }

                @Override
                public boolean canExtract() {
                    return true;
                }

                @Override
                public boolean canReceive() {
                    return true;
                }
            });
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ENERGY) {
                return holder.cast();
            }
            return LazyOptional.empty();
        }
    }
}
