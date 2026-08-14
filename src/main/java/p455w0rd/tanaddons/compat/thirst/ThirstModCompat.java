package p455w0rd.tanaddons.compat.thirst;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

public class ThirstModCompat {

    private static Boolean initialized = null;
    private static Class<?> playerThirstClass = null;
    private static Method drinkMethod = null;
    private static Method getThirstMethod = null;

    private static void init() {
        if (initialized != null) {
            return;
        }
        initialized = true;

        String[] classNames = new String[] {
                "dev.ghen.thirst.foundation.common.capability.PlayerThirst",
                "cn.mlus.thirst.foundation.common.capability.PlayerThirst",
                "dev.ghen.thirst.content.PlayerThirst",
                "cn.mlus.thirst.content.PlayerThirst",
                "dev.ghen.thirst.api.ThirstHelper",
                "cn.mlus.thirst.api.ThirstHelper"
        };

        for (String name : classNames) {
            try {
                Class<?> clazz = Class.forName(name);
                playerThirstClass = clazz;
                for (Method m : clazz.getMethods()) {
                    if (m.getName().equals("drink") && m.getParameterCount() >= 2) {
                        drinkMethod = m;
                    }
                    if (m.getName().equals("getThirst") && m.getParameterCount() >= 1) {
                        getThirstMethod = m;
                    }
                }
                if (playerThirstClass != null) {
                    break;
                }
            }
            catch (ClassNotFoundException ignored) {}
        }
    }

    public static boolean isThirsty(Player player) {
        init();
        if (playerThirstClass != null) {
            try {
                if (getThirstMethod != null) {
                    Object val = getThirstMethod.invoke(null, player);
                    if (val instanceof Number num) {
                        return num.intValue() < 20;
                    }
                }
                return true;
            }
            catch (Throwable ignored) {}
        }
        return false;
    }

    public static boolean quenchThirst(Player player) {
        init();
        if (playerThirstClass != null) {
            try {
                if (drinkMethod != null) {
                    Class<?>[] paramTypes = drinkMethod.getParameterTypes();
                    if (paramTypes.length == 2 && paramTypes[0] == Player.class) {
                        drinkMethod.invoke(null, player, 4);
                        return true;
                    }
                    else if (paramTypes.length == 3 && paramTypes[0] == Player.class) {
                        drinkMethod.invoke(null, player, 4, 2.0f);
                        return true;
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return false;
    }
}
