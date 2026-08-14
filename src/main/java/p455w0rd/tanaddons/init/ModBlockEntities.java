package p455w0rd.tanaddons.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import p455w0rd.tanaddons.tiles.TileTempRegulator;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModGlobals.MODID);

    public static final RegistryObject<BlockEntityType<TileTempRegulator>> TEMP_REGULATOR = BLOCK_ENTITIES.register("temp_regulator",
            () -> BlockEntityType.Builder.of(TileTempRegulator::new, ModBlocks.TEMP_REGULATOR.get()).build(null));

}
