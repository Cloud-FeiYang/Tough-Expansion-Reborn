package p455w0rd.tanaddons.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModGlobals.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tanaddons_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tanaddons"))
                    .icon(() -> new ItemStack(ModItems.PORTABLE_TEMP_REGULATOR.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TEMP_REGULATOR.get());
                        output.accept(ModItems.PORTABLE_TEMP_REGULATOR.get());
                        output.accept(ModItems.THIRST_QUENCHER.get());
                    })
                    .build()
    );

}
