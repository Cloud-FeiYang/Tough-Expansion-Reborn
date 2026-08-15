package p455w0rd.tanaddons.init;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

    public static class Common {
        public final ForgeConfigSpec.BooleanValue requireEnergy;
        public final ForgeConfigSpec.IntValue tempRegulatorRFCapacity;
        public final ForgeConfigSpec.IntValue tempRegulatorRFPerTick;
        public final ForgeConfigSpec.IntValue tempRegulatorRadius;

        public final ForgeConfigSpec.IntValue portableTempRegulatorRFCapacity;
        public final ForgeConfigSpec.IntValue portableTempRegulatorRFPerTick;

        public final ForgeConfigSpec.IntValue thirstQuencherRFCapacity;
        public final ForgeConfigSpec.IntValue thirstQuencherRFPerTick;
        public final ForgeConfigSpec.IntValue thirstQuencherWaterCapacity;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            requireEnergy = builder
                    .comment("Whether temperature regulation and thirst quenching require Forge Energy")
                    .define("requireEnergy", true);

            builder.pop();

            builder.push("temp_regulator_block");

            tempRegulatorRFCapacity = builder
                    .comment("FE storage capacity of the Temperature Regulator block")
                    .defineInRange("rfCapacity", 1000000, 1000, 100000000);

            tempRegulatorRFPerTick = builder
                    .comment("FE consumed per tick per player being regulated by the block")
                    .defineInRange("rfPerTick", 40, 0, 100000);

            tempRegulatorRadius = builder
                    .comment("Block radius within which players are regulated")
                    .defineInRange("radius", 7, 1, 64);

            builder.pop();

            builder.push("portable_temp_regulator");

            portableTempRegulatorRFCapacity = builder
                    .comment("FE storage capacity of the Portable Temperature Regulator")
                    .defineInRange("rfCapacity", 500000, 1000, 100000000);

            portableTempRegulatorRFPerTick = builder
                    .comment("FE consumed per tick while regulating player temperature")
                    .defineInRange("rfPerTick", 20, 0, 100000);

            builder.pop();

            builder.push("thirst_quencher");

            thirstQuencherRFCapacity = builder
                    .comment("FE storage capacity of the Thirst Quencher")
                    .defineInRange("rfCapacity", 500000, 1000, 100000000);

            thirstQuencherRFPerTick = builder
                    .comment("FE consumed per tick while quenching player thirst")
                    .defineInRange("rfPerTick", 20, 0, 100000);

            thirstQuencherWaterCapacity = builder
                    .comment("Internal water storage capacity in mB (default 5000 = 5 buckets)")
                    .defineInRange("waterCapacity", 5000, 1000, 64000);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }
}
