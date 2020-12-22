package dev.ilyapirogov.tigerkus;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> tigerUuids;
        public final ForgeConfigSpec.ConfigValue<Boolean> fearEveryone;
        public final ForgeConfigSpec.ConfigValue<Double> avoidDistance;
        public final ForgeConfigSpec.ConfigValue<Double> farSpeedIn;
        public final ForgeConfigSpec.ConfigValue<Double> nearSpeedIn;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            tigerUuids = builder
                    .comment("The list of Tigers")
                    .defineList("tigerUuids", new ArrayList<>(), o -> UUID.fromString((String)o).hashCode() != 0);

            fearEveryone = builder
                    .comment("When it's true all mobs will run away from the Tiger")
                    .define("fearEveryone",true);

            avoidDistance = builder
                    .comment("The max distance between player and mobs")
                    .defineInRange("avoidDistance", 8D, 0D,32D);

            nearSpeedIn = builder
                    .comment("The initial mob speed")
                    .defineInRange("nearSpeedIn", 1.2D, 0D, 5D);

            farSpeedIn = builder
                    .comment("The mob speed when it's a far away")
                    .defineInRange("farSpeedIn", 1D, 0D, 5D);

            builder.pop();
        }
    }

}
