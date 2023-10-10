package dev.pirogov.tigerkus.fabric;

import dev.pirogov.tigerkus.TigerKus;
import net.fabricmc.api.ModInitializer;

public class TigerKusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TigerKus.init();
    }
}