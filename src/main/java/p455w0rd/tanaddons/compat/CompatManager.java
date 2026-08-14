package p455w0rd.tanaddons.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import p455w0rd.tanaddons.compat.coldsweat.ColdSweatCompat;
import p455w0rd.tanaddons.compat.tan.TANCompat;
import p455w0rd.tanaddons.compat.thirst.ThirstModCompat;
import p455w0rd.tanaddons.init.ModGlobals;

public class CompatManager {

    public static boolean isColdSweatLoaded() {
        return ModList.get().isLoaded(ModGlobals.MODID_COLDSWEAT);
    }

    public static boolean isThirstModLoaded() {
        return ModList.get().isLoaded(ModGlobals.MODID_THIRST);
    }

    public static boolean isTanLoaded() {
        return ModList.get().isLoaded(ModGlobals.MODID_TAN);
    }

    public static boolean isCuriosLoaded() {
        return ModList.get().isLoaded(ModGlobals.MODID_CURIOS);
    }

    /**
     * Checks whether the player's temperature is currently outside the comfortable range.
     */
    public static boolean isPlayerTemperatureAbnormal(Player player) {
        if (isColdSweatLoaded()) {
            try {
                return ColdSweatCompat.isTemperatureAbnormal(player);
            }
            catch (Throwable ignored) {}
        }
        if (isTanLoaded()) {
            try {
                return TANCompat.isTemperatureAbnormal(player);
            }
            catch (Throwable ignored) {}
        }
        return false;
    }

    /**
     * Gradually normalizes the player's temperature towards the comfortable target.
     * Note: Does NOT force-remove debuffs, allowing the game mechanics to naturally stabilize.
     *
     * @return true if temperature was actively adjusted
     */
    public static boolean regulateTemperature(Player player) {
        boolean adjusted = false;
        if (isColdSweatLoaded()) {
            try {
                adjusted = ColdSweatCompat.regulateTemperature(player) || adjusted;
            }
            catch (Throwable ignored) {}
        }
        if (isTanLoaded()) {
            try {
                adjusted = TANCompat.regulateTemperature(player) || adjusted;
            }
            catch (Throwable ignored) {}
        }
        return adjusted;
    }

    /**
     * Checks whether the player is currently thirsty and needs hydration.
     */
    public static boolean isPlayerThirsty(Player player) {
        if (isThirstModLoaded()) {
            try {
                return ThirstModCompat.isThirsty(player);
            }
            catch (Throwable ignored) {}
        }
        if (isTanLoaded()) {
            try {
                return TANCompat.isThirsty(player);
            }
            catch (Throwable ignored) {}
        }
        return false;
    }

    /**
     * Quenches player thirst by adding thirst points and hydration.
     *
     * @return true if thirst was quenched
     */
    public static boolean quenchThirst(Player player) {
        boolean quenched = false;
        if (isThirstModLoaded()) {
            try {
                quenched = ThirstModCompat.quenchThirst(player) || quenched;
            }
            catch (Throwable ignored) {}
        }
        if (isTanLoaded()) {
            try {
                quenched = TANCompat.quenchThirst(player) || quenched;
            }
            catch (Throwable ignored) {}
        }
        return quenched;
    }
}
