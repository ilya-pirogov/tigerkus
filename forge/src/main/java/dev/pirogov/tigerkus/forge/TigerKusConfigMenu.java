package dev.pirogov.tigerkus.forge;

import dev.pirogov.tigerkus.config.ClothConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.client.ConfigScreenHandler;

@OnlyIn(Dist.CLIENT)
public class TigerKusConfigMenu {
    public static void init() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> AutoConfig.getConfigScreen(ClothConfig.class, parent).get())
        );
    }
}
