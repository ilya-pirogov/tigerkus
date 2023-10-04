package dev.pirogov.tigerkus.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.pirogov.tigerkus.TigerKus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TigerKus.MOD_ID)
public class TigerkusForge {
    public TigerkusForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(TigerKus.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        TigerKus.init();
    }
}