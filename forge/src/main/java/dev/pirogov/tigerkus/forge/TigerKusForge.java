package dev.pirogov.tigerkus.forge;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.Env;
import dev.pirogov.tigerkus.TigerKus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TigerKus.MOD_ID)
public class TigerKusForge  {
    public TigerKusForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(TigerKus.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        TigerKus.init();

        if (Platform.getEnvironment() == Env.CLIENT) {
            TigerKusConfigMenu.init();
        }
    }
}