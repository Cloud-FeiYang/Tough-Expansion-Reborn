package p455w0rd.tanaddons.compat.tan;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

/**
 * Soft compatibility with Tough As Nails (modid: toughasnails) 9.2.x
 *
 * Temperature API:
 *   TemperatureHelper.getTemperatureData(Player)      -> ITemperature
 *   ITemperature.getLevel()                           -> TemperatureLevel (enum: ICY, COLD, NEUTRAL, WARM, HOT)
 *   ITemperature.setLevel(TemperatureLevel)
 *   ITemperature.setTargetLevel(TemperatureLevel)
 *   TemperatureHelper.isTemperatureEnabled()          -> boolean
 *
 * Thirst API:
 *   ThirstHelper.getThirst(Player)                    -> IThirst
 *   IThirst.isThirsty()                               -> boolean (thirst < 20)
 *   IThirst.drink(int thirst, float hydration)
 *   ThirstHelper.isThirstEnabled()                    -> boolean
 */
public class TANCompat {

    private static Boolean initialized = null;

    // ---- Temperature ----
    private static Method getTemperatureDataMethod = null; // TemperatureHelper.getTemperatureData(Player) -> ITemperature
    private static Method isTemperatureEnabledMethod = null; // TemperatureHelper.isTemperatureEnabled() -> boolean
    private static Method getLevelMethod = null;           // ITemperature.getLevel() -> TemperatureLevel
    private static Method setLevelMethod = null;           // ITemperature.setLevel(TemperatureLevel)
    private static Method setTargetLevelMethod = null;     // ITemperature.setTargetLevel(TemperatureLevel)
    private static Object NEUTRAL_LEVEL = null;            // TemperatureLevel.NEUTRAL

    // ---- Thirst ----
    private static Method getThirstMethod = null;          // ThirstHelper.getThirst(Player) -> IThirst
    private static Method isThirstEnabledMethod = null;    // ThirstHelper.isThirstEnabled() -> boolean
    private static Method isThirstyMethod = null;          // IThirst.isThirsty() -> boolean
    private static Method drinkMethod = null;              // IThirst.drink(int, float)

    private static void init() {
        if (initialized != null) return;
        initialized = true;

        // --- Temperature ---
        try {
            Class<?> tempHelperClass = Class.forName("toughasnails.api.temperature.TemperatureHelper");
            Class<?> iTemperatureClass = Class.forName("toughasnails.api.temperature.ITemperature");
            Class<?> tempLevelClass = Class.forName("toughasnails.api.temperature.TemperatureLevel");

            getTemperatureDataMethod = tempHelperClass.getMethod("getTemperatureData", Player.class);
            isTemperatureEnabledMethod = tempHelperClass.getMethod("isTemperatureEnabled");
            getLevelMethod = iTemperatureClass.getMethod("getLevel");
            setLevelMethod = iTemperatureClass.getMethod("setLevel", tempLevelClass);
            setTargetLevelMethod = iTemperatureClass.getMethod("setTargetLevel", tempLevelClass);

            // Get TemperatureLevel.NEUTRAL enum constant
            for (Object constant : tempLevelClass.getEnumConstants()) {
                if ("NEUTRAL".equals(constant.toString())) {
                    NEUTRAL_LEVEL = constant;
                    break;
                }
            }
        } catch (Throwable ignored) {}

        // --- Thirst ---
        try {
            Class<?> thirstHelperClass = Class.forName("toughasnails.api.thirst.ThirstHelper");
            Class<?> iThirstClass = Class.forName("toughasnails.api.thirst.IThirst");

            getThirstMethod = thirstHelperClass.getMethod("getThirst", Player.class);
            isThirstEnabledMethod = thirstHelperClass.getMethod("isThirstEnabled");
            isThirstyMethod = iThirstClass.getMethod("isThirsty");
            drinkMethod = iThirstClass.getMethod("drink", int.class, float.class);
        } catch (Throwable ignored) {}
    }

    // ====================== Temperature ======================

    /**
     * Returns true when the player's temperature level is not NEUTRAL.
     */
    public static boolean isTemperatureAbnormal(Player player) {
        init();
        if (getTemperatureDataMethod == null || getLevelMethod == null || NEUTRAL_LEVEL == null) return false;
        try {
            // Respect TAN config: if temperature system is disabled, skip
            if (isTemperatureEnabledMethod != null && !(boolean) isTemperatureEnabledMethod.invoke(null)) return false;

            Object tempData = getTemperatureDataMethod.invoke(null, player);
            if (tempData == null) return false;
            Object level = getLevelMethod.invoke(tempData);
            return !NEUTRAL_LEVEL.equals(level);
        } catch (Throwable ignored) {}
        return false;
    }

    /**
     * Directly sets both current and target temperature to NEUTRAL.
     * TAN handles its own gradual transition logic; setting both prevents
     * the system from fighting against us.
     */
    public static boolean regulateTemperature(Player player) {
        init();
        if (getTemperatureDataMethod == null || setLevelMethod == null || NEUTRAL_LEVEL == null) return false;
        try {
            if (isTemperatureEnabledMethod != null && !(boolean) isTemperatureEnabledMethod.invoke(null)) return false;

            Object tempData = getTemperatureDataMethod.invoke(null, player);
            if (tempData == null) return false;
            Object currentLevel = getLevelMethod.invoke(tempData);
            if (NEUTRAL_LEVEL.equals(currentLevel)) return false;

            // Increment/decrement one step toward NEUTRAL using the enum's own methods
            // TemperatureLevel has ordinal(): ICY=0, COLD=1, NEUTRAL=2, WARM=3, HOT=4
            int ordinal = ((Enum<?>) currentLevel).ordinal();
            int neutralOrdinal = ((Enum<?>) NEUTRAL_LEVEL).ordinal();
            Object[] constants = currentLevel.getClass().getEnumConstants();
            int nextOrdinal = ordinal < neutralOrdinal ? ordinal + 1 : ordinal - 1;
            Object nextLevel = constants[nextOrdinal];

            setLevelMethod.invoke(tempData, nextLevel);
            if (setTargetLevelMethod != null) {
                setTargetLevelMethod.invoke(tempData, NEUTRAL_LEVEL);
            }
            return true;
        } catch (Throwable ignored) {}
        return false;
    }

    // ====================== Thirst ======================

    /**
     * Returns true when the player's thirst level is below maximum (< 20).
     */
    public static boolean isThirsty(Player player) {
        init();
        if (getThirstMethod == null || isThirstyMethod == null) return false;
        try {
            if (isThirstEnabledMethod != null && !(boolean) isThirstEnabledMethod.invoke(null)) return false;

            Object thirstData = getThirstMethod.invoke(null, player);
            if (thirstData == null) return false;
            return (boolean) isThirstyMethod.invoke(thirstData);
        } catch (Throwable ignored) {}
        return false;
    }

    /**
     * Restores 4 thirst points and 2.0 hydration using the official IThirst.drink() API.
     */
    public static boolean quenchThirst(Player player) {
        init();
        if (getThirstMethod == null || drinkMethod == null) return false;
        try {
            if (isThirstEnabledMethod != null && !(boolean) isThirstEnabledMethod.invoke(null)) return false;

            Object thirstData = getThirstMethod.invoke(null, player);
            if (thirstData == null) return false;
            // Only quench if actually thirsty
            if (isThirstyMethod != null && !(boolean) isThirstyMethod.invoke(thirstData)) return false;

            drinkMethod.invoke(thirstData, 4, 2.0f);
            return true;
        } catch (Throwable ignored) {}
        return false;
    }
}
