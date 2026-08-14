package p455w0rd.tanaddons.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import p455w0rd.tanaddons.items.ItemTempRegulator;
import p455w0rd.tanaddons.items.ItemThirstQuencher;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModGlobals.MODID);

    public static final RegistryObject<BlockItem> TEMP_REGULATOR = ITEMS.register("temp_regulator",
            () -> new BlockItem(ModBlocks.TEMP_REGULATOR.get(), new Item.Properties()));

    public static final RegistryObject<ItemTempRegulator> PORTABLE_TEMP_REGULATOR = ITEMS.register("portable_temp_regulator",
            ItemTempRegulator::new);

    public static final RegistryObject<ItemThirstQuencher> THIRST_QUENCHER = ITEMS.register("thirst_quencher",
            ItemThirstQuencher::new);

}
