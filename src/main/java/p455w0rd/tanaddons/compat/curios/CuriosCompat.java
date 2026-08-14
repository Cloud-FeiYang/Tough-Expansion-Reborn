package p455w0rd.tanaddons.compat.curios;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class CuriosCompat {

    private static Capability<?> CURIOS_CAP = null;
    private static Class<?> ICurioClass = null;
    private static Method getEntityMethod = null;
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;
        initialized = true;
        try {
            ICurioClass = Class.forName("top.theillusivec4.curios.api.type.capability.ICurio");
            Class<?> slotContextClass = Class.forName("top.theillusivec4.curios.api.SlotContext");
            getEntityMethod = slotContextClass.getMethod("entity");
            Class<?> curiosCapClass = Class.forName("top.theillusivec4.curios.api.CuriosCapability");
            CURIOS_CAP = (Capability<?>) curiosCapClass.getField("ITEM").get(null);
        }
        catch (Throwable ignored) {}
    }

    @Nullable
    public static ICapabilityProvider initCuriosProvider(ItemStack stack, Consumer<Player> tickConsumer) {
        init();
        if (CURIOS_CAP == null || ICurioClass == null) {
            return null;
        }

        Object curioInstance = Proxy.newProxyInstance(
                ICurioClass.getClassLoader(),
                new Class<?>[]{ICurioClass},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (name.equals("getStack")) {
                        return stack;
                    }
                    if (name.equals("curioTick") && args != null && args.length > 0 && args[0] != null) {
                        try {
                            if (getEntityMethod != null) {
                                Object entity = getEntityMethod.invoke(args[0]);
                                if (entity instanceof Player player && !player.level().isClientSide) {
                                    tickConsumer.accept(player);
                                }
                            }
                        }
                        catch (Throwable ignored) {}
                        return null;
                    }
                    if (name.equals("toString")) {
                        return "CurioItemProxy[" + stack + "]";
                    }
                    if (name.equals("hashCode")) {
                        return stack.hashCode();
                    }
                    if (name.equals("equals")) {
                        return proxy == (args != null && args.length > 0 ? args[0] : null);
                    }

                    if (method.isDefault()) {
                        try {
                            return InvocationHandler.invokeDefault(proxy, method, args);
                        }
                        catch (Throwable ignored) {}
                    }

                    Class<?> returnType = method.getReturnType();
                    if (List.class.isAssignableFrom(returnType)) {
                        if (args != null) {
                            for (Object arg : args) {
                                if (arg instanceof List<?> l) {
                                    return l;
                                }
                            }
                        }
                        return Collections.emptyList();
                    }
                    if (Set.class.isAssignableFrom(returnType)) {
                        return Collections.emptySet();
                    }
                    if (Map.class.isAssignableFrom(returnType)) {
                        return Collections.emptyMap();
                    }
                    if (returnType == boolean.class || returnType == Boolean.class) {
                        return true;
                    }
                    if (returnType == int.class || returnType == Integer.class) {
                        return 0;
                    }
                    if (ItemStack.class.isAssignableFrom(returnType)) {
                        return stack;
                    }
                    return null;
                }
        );

        LazyOptional<?> lazy = LazyOptional.of(() -> curioInstance);

        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == CURIOS_CAP) {
                    return lazy.cast();
                }
                return LazyOptional.empty();
            }
        };
    }
}
