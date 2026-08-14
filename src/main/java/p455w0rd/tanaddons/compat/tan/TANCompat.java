package p455w0rd.tanaddons.compat.tan;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

public class TANCompat {

    private static Boolean initialized = null;
    private static Method getThirstMethod = null;
    private static Method getThirstDataMethod = null;
    private static Method getTemperatureDataMethod = null;

    private static void init() {
        if (initialized != null) {
            return;
        }
        initialized = true;

        try {
            Class<?> thirstHelper = Class.forName("toughasnails.api.thirst.ThirstHelper");
            for (Method m : thirstHelper.getMethods()) {
                if (m.getName().equals("getThirst") || m.getName().equals("getThirstData")) {
                    getThirstMethod = m;
                    getThirstDataMethod = m;
                    break;
                }
            }
        }
        catch (Throwable ignored) {}

        try {
            Class<?> tempHelper = Class.forName("toughasnails.api.temperature.TemperatureHelper");
            for (Method m : tempHelper.getMethods()) {
                if (m.getName().equals("getTemperature") || m.getName().equals("getTemperatureData")) {
                    getTemperatureDataMethod = m;
                    break;
                }
            }
        }
        catch (Throwable ignored) {}
    }

    public static boolean isTemperatureAbnormal(Player player) {
        init();
        if (getTemperatureDataMethod != null) {
            try {
                Object data = getTemperatureDataMethod.invoke(null, player);
                if (data != null) {
                    Method getTemp = data.getClass().getMethod("getTemperature");
                    Object temp = getTemp.invoke(data);
                    if (temp != null) {
                        Method getRaw = temp.getClass().getMethod("getRawValue");
                        int raw = (int) getRaw.invoke(temp);
                        return raw != 14;
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return false;
    }

    public static boolean regulateTemperature(Player player) {
        init();
        if (getTemperatureDataMethod != null) {
            try {
                Object data = getTemperatureDataMethod.invoke(null, player);
                if (data != null) {
                    Method getTemp = data.getClass().getMethod("getTemperature");
                    Object temp = getTemp.invoke(data);
                    if (temp != null) {
                        Method getRaw = temp.getClass().getMethod("getRawValue");
                        int raw = (int) getRaw.invoke(temp);
                        if (raw != 14) {
                            int next = raw < 14 ? raw + 1 : raw - 1;
                            Class<?> tempClass = temp.getClass();
                            Object nextTemp = tempClass.getConstructor(int.class).newInstance(next);
                            Method setTemp = data.getClass().getMethod("setTemperature", tempClass);
                            setTemp.invoke(data, nextTemp);
                            return true;
                        }
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return false;
    }

    public static boolean isThirsty(Player player) {
        init();
        if (getThirstDataMethod != null) {
            try {
                Object data = getThirstDataMethod.invoke(null, player);
                if (data != null) {
                    Method getThirst = data.getClass().getMethod("getThirst");
                    int thirst = (int) getThirst.invoke(data);
                    return thirst < 20;
                }
            }
            catch (Throwable ignored) {}
        }
        return false;
    }

    public static boolean quenchThirst(Player player) {
        init();
        if (getThirstDataMethod != null) {
            try {
                Object data = getThirstDataMethod.invoke(null, player);
                if (data != null) {
                    Method getThirst = data.getClass().getMethod("getThirst");
                    int thirst = (int) getThirst.invoke(data);
                    if (thirst < 20) {
                        Method setThirst = data.getClass().getMethod("setThirst", int.class);
                        setThirst.invoke(data, Math.min(20, thirst + 2));
                        try {
                            Method setHydration = data.getClass().getMethod("setHydration", float.class);
                            setHydration.invoke(data, 5.0f);
                        }
                        catch (Throwable ignored) {}
                        return true;
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return false;
    }
}
