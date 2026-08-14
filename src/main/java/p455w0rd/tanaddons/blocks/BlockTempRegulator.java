package p455w0rd.tanaddons.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import p455w0rd.tanaddons.init.ModBlockEntities;
import p455w0rd.tanaddons.tiles.TileTempRegulator;
import p455w0rd.tanaddons.util.ReadableNumberConverter;

public class BlockTempRegulator extends BaseEntityBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public BlockTempRegulator() {
        super(BlockBehaviour.Properties.of()
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileTempRegulator(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.TEMP_REGULATOR.get(), TileTempRegulator::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TileTempRegulator tile) {
            if (player.isShiftKeyDown()) {
                if (!level.isClientSide) {
                    String msg = "Energy: " + ReadableNumberConverter.INSTANCE.toWideReadableForm(tile.getEnergyStored())
                            + "/" + ReadableNumberConverter.INSTANCE.toWideReadableForm(tile.getMaxEnergyStored())
                            + " FE (" + tile.getEnergyUse() + " FE/t)";
                    player.displayClientMessage(Component.literal(msg).withStyle(ChatFormatting.GOLD), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else {
                if (!level.isClientSide) {
                    tile.nextMode();
                    String key = switch (tile.getMode()) {
                        case 0 -> "message.tanaddons.redstonerequired";
                        case 1 -> "message.tanaddons.noredstonerequired";
                        default -> "message.tanaddons.redstoneignored";
                    };
                    player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.GREEN), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }
}
