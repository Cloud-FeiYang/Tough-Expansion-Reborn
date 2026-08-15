package p455w0rd.tanaddons.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import p455w0rd.tanaddons.blocks.BlockTempRegulator;
import p455w0rd.tanaddons.compat.CompatManager;
import p455w0rd.tanaddons.init.ModBlockEntities;
import p455w0rd.tanaddons.init.ModConfig;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TileTempRegulator extends BlockEntity {

    public static final String TAG_ENERGY = "Energy";
    public static final String TAG_MODE = "RSMode";
    public static final int COOLDOWN_TICKS = 60;

    private int energy = 0;
    private int mode = 0; // 0 = requires signal, 1 = requires lack of signal, 2 = ignored

    private final Map<UUID, Integer> playerTimers = new HashMap<>();
    private final LazyOptional<IEnergyStorage> energyHolder = LazyOptional.of(this::createEnergyStorage);

    public TileTempRegulator(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TEMP_REGULATOR.get(), pos, state);
    }

    private IEnergyStorage createEnergyStorage() {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int capacity = getMaxEnergyStored();
                int energyReceived = Math.min(capacity - energy, Math.min(10000, maxReceive));
                if (!simulate && energyReceived > 0) {
                    energy += energyReceived;
                    setChanged();
                }
                return energyReceived;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return energy;
            }

            @Override
            public int getMaxEnergyStored() {
                return ModConfig.COMMON.tempRegulatorRFCapacity.get();
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    public int getMode() {
        return mode;
    }

    public void nextMode() {
        mode = (mode + 1) % 3;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int getEnergyStored() {
        return energy;
    }

    public int getMaxEnergyStored() {
        return ModConfig.COMMON.tempRegulatorRFCapacity.get();
    }

    public int getEnergyUse() {
        return ModConfig.COMMON.tempRegulatorRFPerTick.get();
    }

    public boolean isRunning() {
        if (level == null) {
            return false;
        }
        boolean hasSignal = level.hasNeighborSignal(worldPosition);
        boolean redstoneSatisfied = switch (mode) {
            case 0 -> hasSignal;
            case 1 -> !hasSignal;
            default -> true;
        };
        boolean hasEnergy = !ModConfig.COMMON.requireEnergy.get() || energy >= getEnergyUse();
        return redstoneSatisfied && hasEnergy;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileTempRegulator tile) {
        if (level.isClientSide) {
            return;
        }

        boolean running = tile.isRunning();
        if (state.getValue(BlockTempRegulator.ACTIVE) != running) {
            level.setBlock(pos, state.setValue(BlockTempRegulator.ACTIVE, running), 3);
        }

        if (!running) {
            return;
        }

        int radius = ModConfig.COMMON.tempRegulatorRadius.get();
        AABB box = new AABB(pos).inflate(radius);
        List<Player> players = level.getEntitiesOfClass(Player.class, box);

        for (Player player : players) {
            if (CompatManager.isPlayerTemperatureAbnormal(player)) {
                int timer = tile.getPlayerTimer(player);
                if (timer <= 0) {
                    CompatManager.regulateTemperature(player);
                    tile.setPlayerTimer(player, COOLDOWN_TICKS);
                }
                else {
                    tile.setPlayerTimer(player, timer - 1);
                }

                if (ModConfig.COMMON.requireEnergy.get()) {
                    tile.energy = Math.max(0, tile.energy - tile.getEnergyUse());
                    tile.setChanged();
                }
            }
            else {
                tile.removePlayerTimer(player);
            }
        }
    }

    public int getPlayerTimer(Player player) {
        return playerTimers.getOrDefault(player.getUUID(), 0);
    }

    public void setPlayerTimer(Player player, int time) {
        playerTimers.put(player.getUUID(), time);
    }

    public void removePlayerTimer(Player player) {
        playerTimers.remove(player.getUUID());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_ENERGY)) {
            energy = tag.getInt(TAG_ENERGY);
        }
        if (tag.contains(TAG_MODE)) {
            mode = tag.getInt(TAG_MODE);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(TAG_ENERGY, energy);
        tag.putInt(TAG_MODE, mode);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && ModConfig.COMMON.requireEnergy.get()) {
            return energyHolder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHolder.invalidate();
    }
}
