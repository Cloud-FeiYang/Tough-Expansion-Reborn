package p455w0rd.tanaddons.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import p455w0rd.tanaddons.init.ModGlobals;
import p455w0rd.tanaddons.init.ModItems;
import p455w0rd.tanaddons.items.ItemThirstQuencher;

@Mod.EventBusSubscriber(modid = ModGlobals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.THIRST_QUENCHER.get(),
                    new ResourceLocation(ModGlobals.MODID, "filllevel"),
                    (stack, level, entity, seed) -> {
                        if (stack.getItem() instanceof ItemThirstQuencher tq) {
                            int stored = tq.getFluidStored(stack);
                            int cap = tq.getFluidCapacity();
                            if (stored <= 0) return 0.0F;
                            float fraction = (float) stored / (float) cap;
                            if (fraction <= 0.25F) return 1.0F;
                            if (fraction <= 0.50F) return 2.0F;
                            if (fraction <= 0.75F) return 3.0F;
                            return 4.0F;
                        }
                        return 0.0F;
                    });
        });
    }
}
