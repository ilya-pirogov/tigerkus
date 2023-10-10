package dev.pirogov.tigerkus.config;


import dev.pirogov.tigerkus.TigerKus;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.*;

@Config(name = TigerKus.MOD_ID)
public class ClothConfig implements ConfigData {
    @ConfigEntry.Category("avoiding")
    @ConfigEntry.Gui.TransitiveObject
    public Avoiding avoiding = new Avoiding();

    @ConfigEntry.Category("repulsion")
    @ConfigEntry.Gui.TransitiveObject
    public Repulsion repulsion = new Repulsion();

    @Comment("UUIDs of Tigers who can bite!")
    public List<String> uuids = new ArrayList<>();
    @Override
    public void validatePostLoad() throws ValidationException {
        uuids = uuids.stream().filter(s -> {
           try {
               //noinspection ResultOfMethodCallIgnored
               UUID.fromString(s);
               return true;
           } catch (IllegalArgumentException ex) {
               TigerKus.LOGGER.error("Invalid UUID: %s; %s".formatted(s, ex.getMessage()));
               return false;
           }
        }).toList();
    }

    @Config(name = "repulsion")
    public static class Repulsion {
        @Comment("If true, player will repel any living entities that are trying to hurt Tiger (e.g. Slimes).")
        public boolean enabled = true;

        @Comment("Repulsion strength.")
        public double strength = 2.0;
    }

    @Config(name = "avoiding")
    public static class Avoiding {
        @Comment("If true, mobs will avoid players at all costs. Like creepers do with cats and ocelots.")
        public boolean enabled = true;

        @Comment("Maximum distance at which mobs will avoid players.")
        public float maxDist = 8.0f;

        @Comment("Walk speed modifier for far away mobs.")
        public double walkSpeedModifier = 1.2;

        @Comment("Sprint speed modifier for close mobs.")
        public double sprintSpeedModifier = 1.4;
    }
}