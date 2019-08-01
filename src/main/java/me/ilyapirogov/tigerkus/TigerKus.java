package me.ilyapirogov.tigerkus;

import me.ilyapirogov.tigerkus.commands.AddPlayerCommand;
import me.ilyapirogov.tigerkus.commands.RemovePlayerCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@Mod(modid = TigerKus.MODID, name = TigerKus.NAME, version = TigerKus.VERSION)
public class TigerKus {
    public static final String MODID = "tigerkus";
    public static final String NAME = "Tiger Kus'";
    public static final String VERSION = "1.5";
    public static final String PERM_ADD = "tigerkus.list.add";
    public static final String PERM_REMOVE = "tigerkus.list.remove";
    public static final String CONFIG_GENERAL = "general";

    public static Logger logger;
    private static Configuration config;

    public static HashSet<UUID> tigerUuids = new HashSet<>();
    public static boolean fearEveryone = true;
    public static float avoidDistance = 8F;
    public static double farSpeedIn = 1D;
    public static double nearSpeedIn = 1.2D;

    public static void addPlayer(UUID playerUuid) {
        tigerUuids.add(playerUuid);
        updateConfig();
    }

    public static void removePlayer(UUID playerUuid) {
        tigerUuids.remove(playerUuid);
        updateConfig();
    }

    private static void updateConfig() {
        ArrayList<String> newList = new ArrayList<>(tigerUuids.size());
        for (UUID tigerUuid : tigerUuids) {
            newList.add(tigerUuid.toString());
        }

        config.getCategory(CONFIG_GENERAL).get("tigerUuids").set(newList.toArray(new String[0]));
        if (config.hasChanged()) {
            config.save();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());
        String[] defaultUuid = new String[]{};

        try {

            String[] uuids = config.get(CONFIG_GENERAL, "tigerUuids", defaultUuid).getStringList();
            for (String uuid : uuids) {
                tigerUuids.add(UUID.fromString(uuid));
            }

            fearEveryone = config.get(CONFIG_GENERAL, "fearEveryone", fearEveryone).getBoolean();
            avoidDistance = (float) config.get("general", "avoidDistance", avoidDistance).getDouble();
            farSpeedIn = config.get(CONFIG_GENERAL, "farSpeedIn", farSpeedIn).getDouble();
            nearSpeedIn = config.get(CONFIG_GENERAL, "nearSpeedIn", nearSpeedIn).getDouble();

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
        PermissionAPI.registerNode(PERM_ADD, DefaultPermissionLevel.OP,
                "Ability to add player to Tiger list");
        PermissionAPI.registerNode(PERM_REMOVE, DefaultPermissionLevel.OP,
                "Ability to remove player from Tiger list");
    }


    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new AddPlayerCommand());
        event.registerServerCommand(new RemovePlayerCommand());
    }
}
