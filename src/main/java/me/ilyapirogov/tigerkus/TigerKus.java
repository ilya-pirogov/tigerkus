package me.ilyapirogov.tigerkus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.UUID;

@Mod(modid = TigerKus.MODID, name = TigerKus.NAME, version = TigerKus.VERSION)
public class TigerKus {
    public static final String MODID = "tigerkus";
    public static final String NAME = "Tiger Kus'";
    public static final String VERSION = "1.3";

    public static Logger logger;
    private Configuration config;

    public static HashSet<UUID> tigerUuids = new HashSet<>();
    public static boolean fearEveryone = true;
    public static float avoidDistance = 8F;
    public static double farSpeedIn = 1D;
    public static double nearSpeedIn = 1.2D;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());
        String[] defaultUuid = new String[]{"424303b4-09a6-4297-a45e-88d1c3903a60"};

        try {

            String[] uuids = config.get("general", "tigerUuids", defaultUuid).getStringList();
            for (String uuid : uuids) {
                tigerUuids.add(UUID.fromString(uuid));
            }

            fearEveryone = config.get("general", "fearEveryone", fearEveryone).getBoolean();
            avoidDistance = (float) config.get("general", "avoidDistance", avoidDistance).getDouble();
            farSpeedIn = config.get("general", "farSpeedIn", farSpeedIn).getDouble();
            nearSpeedIn = config.get("general", "nearSpeedIn", nearSpeedIn).getDouble();

        } catch (Exception ex) {
            logger.error("Unable to load a configuration file");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        KusHandler events = new KusHandler();
        MinecraftForge.EVENT_BUS.register(events);
    }
}
