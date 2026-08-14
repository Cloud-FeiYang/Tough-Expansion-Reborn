package p455w0rd.tanaddons.init;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import p455w0rd.tanaddons.blocks.BlockTempRegulator;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModGlobals.MODID);

    public static final RegistryObject<BlockTempRegulator> TEMP_REGULATOR = BLOCKS.register("temp_regulator", BlockTempRegulator::new);

}
