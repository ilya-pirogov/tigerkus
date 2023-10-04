package dev.pirogov.tigerkus.fabric;

import dev.pirogov.tigerkus.TigerKus;
import net.fabricmc.api.ModInitializer;

public class TigerkusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TigerKus.init();
    }
}