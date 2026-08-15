package p455w0rd.tanaddons.compat.thirst;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Soft compatibility with "Thirst Was Taken" (modid: thirst).
 * The mod exposes player thirst via a Forge capability (ModCapabilities.PLAYER_THIRST)
 * returning an IThirst instance. IThirst has:
 *   int  getThirst()                             - current thirst (max 20)
 *   void drink(Player player, int thirst, int quenched)  - replenish thirst
 */
public class ThirstModCompat {

    private static Boolean initialized = null;
    private static Capability<?> PLAYER_THIRST_CAP = null;
    private static Method drinkMethod = null;   // IThirst.drink(Player, int, int)
    private static Method getThirstMethod = null; // IThirst.getThirst()

    private static void init() {
        if (initialized != null) return;
        initialized = true;
        try {
            Class<?> capabilitiesClass = Class.forName("dev.ghen.thirst.foundation.common.capability.ModCapabilities");
            Field capField = capabilitiesClass.getField("PLAYER_THIRST");
            PLAYER_THIRST_CAP = (Capability<?>) capField.get(null);

            Class<?> iThirstClass = Class.forName("dev.ghen.thirst.foundation.common.capability.IThirst");
            for (Method m : iThirstClass.getMethods()) {
                if ("drink".equals(m.getName()) && m.getParameterCount() == 3) {
                    drinkMethod = m;
                }
                if ("getThirst".equals(m.getName()) && m.getParameterCount() == 0) {
                    getThirstMethod = m;
                }
            }
        }
        catch (Throwable ignored) {}
    }

    /**
     * Returns the IThirst capability instance for the player, or null if unavailable.
     */
    private static Object getThirstCap(Player player) {
        if (PLAYER_THIRST_CAP == null) return null;
        try {
            var lazyOpt = player.getCapability((Capability<Object>) PLAYER_THIRST_CAP);
            return lazyOpt.isPresent() ? lazyOpt.orElse(null) : null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean isThirsty(Player player) {
        init();
        try {
            Object cap = getThirstCap(player);
            if (cap != null && getThirstMethod != null) {
                Object val = getThirstMethod.invoke(cap);
                if (val instanceof Number num) {
                    return num.intValue() < 20;
                }
            }
        }
        catch (Throwable ignored) {}
        return false;
    }

    /**
     * Restores 4 thirst points and 2 quenched points.
     */
    public static boolean quenchThirst(Player player) {
        init();
        try {
            Object cap = getThirstCap(player);
            if (cap != null && drinkMethod != null) {
                drinkMethod.invoke(cap, player, 4, 2);
                return true;
            }
        }
        catch (Throwable ignored) {}
        return false;
    }
}
