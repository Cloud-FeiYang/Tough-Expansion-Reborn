package p455w0rd.tanaddons.compat.coldsweat;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

/**
 * Soft compatibility with Cold Sweat (modid: cold_sweat).
 * Uses the Temperature API directly:
 *   Temperature.get(LivingEntity, Trait)  -> double
 *   Temperature.set(LivingEntity, Trait, double)
 *   Temperature.add(LivingEntity, Trait, double)
 *
 * CORE temperature represents the player's internal body temperature.
 * Values far from 0.0 indicate discomfort (hot or cold).
 */
public class ColdSweatCompat {

    private static Boolean initialized = null;
    private static Object traitCORE = null;
    private static Method getMethod = null;  // Temperature.get(LivingEntity, Trait) -> double
    private static Method setMethod = null;  // Temperature.set(LivingEntity, Trait, double)

    private static void init() {
        if (initialized != null) return;
        initialized = true;
        try {
            Class<?> tempClass = Class.forName("com.momosoftworks.coldsweat.api.util.Temperature");
            Class<?> traitClass = Class.forName("com.momosoftworks.coldsweat.api.util.Temperature$Trait");

            // Locate CORE enum constant
            for (Object constant : traitClass.getEnumConstants()) {
                if ("CORE".equals(constant.toString())) {
                    traitCORE = constant;
                    break;
                }
            }

            // Find Temperature.get(LivingEntity, Trait) -> double
            // Find Temperature.set(LivingEntity, Trait, double)
            for (Method m : tempClass.getMethods()) {
                Class<?>[] params = m.getParameterTypes();
                if ("get".equals(m.getName()) && params.length == 2
                        && params[1] == traitClass && m.getReturnType() == double.class) {
                    getMethod = m;
                }
                else if ("set".equals(m.getName()) && params.length == 3
                        && params[1] == traitClass && params[2] == double.class) {
                    setMethod = m;
                }
            }
        }
        catch (Throwable ignored) {}
    }

    /**
     * Returns true if the player's CORE temperature deviates significantly from 0 (neutral).
     * Cold Sweat CORE ranges roughly from -150 (extreme cold) to +150 (extreme heat).
     * Comfortable zone is typically around -10 to +10 depending on config.
     */
    public static boolean isTemperatureAbnormal(Player player) {
        init();
        if (getMethod == null || traitCORE == null) return false;
        try {
            Object val = getMethod.invoke(null, player, traitCORE);
            if (val instanceof Number num) {
                // Cold Sweat core temp: 0 = neutral, abs value > 10 is uncomfortable
                return Math.abs(num.doubleValue()) > 10.0;
            }
        }
        catch (Throwable ignored) {}
        return false;
    }

    /**
     * Gently nudges the player's CORE temperature towards 0 (neutral).
     * Uses Temperature.set() to directly apply a scaled-down value toward 0.
     */
    public static boolean regulateTemperature(Player player) {
        init();
        if (getMethod == null || setMethod == null || traitCORE == null) return false;
        try {
            Object val = getMethod.invoke(null, player, traitCORE);
            if (val instanceof Number num) {
                double core = num.doubleValue();
                if (Math.abs(core) > 10.0) {
                    // Move 10% of the way toward 0 each cycle (gentle regulation)
                    double regulated = core * 0.85;
                    setMethod.invoke(null, player, traitCORE, regulated);
                    return true;
                }
            }
        }
        catch (Throwable ignored) {}
        return false;
    }
}
