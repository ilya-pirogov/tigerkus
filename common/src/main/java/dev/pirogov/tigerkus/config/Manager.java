package dev.pirogov.tigerkus.config;

import me.shedaniel.autoconfig.ConfigHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Manager {
    private final ConfigHolder<ClothConfig> holder;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @NotNull
    public ClothConfig current;

    @NotNull
    private Set<UUID> tigers = new HashSet<>();

    public Manager(ConfigHolder<ClothConfig> holder) {
        this.holder = holder;
        load();
    }

    private void load() {
        current = holder.getConfig();
        tigers = new HashSet<>(current.uuids.stream().map(UUID::fromString).toList());
    }

    private void save() {
        current.uuids = tigers.stream().map(UUID::toString).toList();
        holder.save();
    }

    public boolean isTiger(UUID uuid) {
        return tigers.contains(uuid);
    }

    public void addTiger(UUID uuid) {
        tigers.add(uuid);
        save();
    }

    public void removeTiger(UUID uuid) {
        tigers.remove(uuid);
        save();
    }

    public void reload() {
        load();
    }
}
