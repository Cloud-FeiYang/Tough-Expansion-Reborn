package p455w0rd.tanaddons.compat.coldsweat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

public class ColdSweatCompat {

    private static Boolean initialized = null;
    private static Class<?> tempClass = null;
    private static Object traitCore = null;
    private static Object traitBody = null;
    private static Method getMethod = null;
    private static Method addMethod = null;

    private static void init() {
        if (initialized != null) {
            return;
        }
        initialized = true;

        try {
            tempClass = Class.forName("com.momosoftworks.coldsweat.api.util.Temperature");
            Class<?> traitClass = null;
            for (Class<?> declared : tempClass.getDeclaredClasses()) {
                if (declared.getSimpleName().equals("Trait")) {
                    traitClass = declared;
                    break;
                }
            }
            if (traitClass == null) {
                traitClass = Class.forName("com.momosoftworks.coldsweat.api.util.Temperature$Trait");
            }

            for (Object constant : traitClass.getEnumConstants()) {
                String name = constant.toString();
                if ("CORE".equalsIgnoreCase(name)) {
                    traitCore = constant;
                }
                else if ("BODY".equalsIgnoreCase(name)) {
                    traitBody = constant;
                }
            }

            for (Method m : tempClass.getMethods()) {
                if ("get".equals(m.getName()) && m.getParameterCount() == 2) {
                    getMethod = m;
                }
                else if ("add".equals(m.getName()) && m.getParameterCount() == 3) {
                    addMethod = m;
                }
            }
        }
        catch (Throwable ignored) {}
    }

    public static boolean isTemperatureAbnormal(Player player) {
        init();
        if (getMethod != null && traitCore != null) {
            try {
                Object coreVal = getMethod.invoke(null, player, traitCore);
                if (coreVal instanceof Number num) {
                    double core = num.doubleValue();
                    if (Math.abs(core) > 1.0) {
                        return true;
                    }
                }
                if (traitBody != null) {
                    Object bodyVal = getMethod.invoke(null, player, traitBody);
                    if (bodyVal instanceof Number num) {
                        double body = num.doubleValue();
                        return Math.abs(body) > 1.0;
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return false;
    }

    public static boolean regulateTemperature(Player player) {
        init();
        if (getMethod != null && addMethod != null && traitCore != null) {
            try {
                Object coreVal = getMethod.invoke(null, player, traitCore);
                if (coreVal instanceof Number num) {
                    double core = num.doubleValue();
                    if (Math.abs(core) > 0.5) {
                        double step = Math.min(Math.abs(core), 2.0);
                        if (core > 0) {
                            addMethod.invoke(null, player, traitCore, -step);
                        }
                        else {
                            addMethod.invoke(null, player, traitCore, step);
                        }
                        return true;
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return false;
    }
}
