package dev.ilyapirogov.tigerkus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.minecraft.command.Commands.literal;

@Mod("tigerkus")
public class TigerKus {
    public static final Logger LOGGER = LogManager.getLogger();
    public static HashSet<UUID> tigerUuids = new HashSet<>();

    public TigerKus() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.spec);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.addListener(TigerKus::startup);
    }

    public static int addPlayer(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
            tigerUuids.add(player.getUniqueID());
            CommonConfig.GENERAL.tigerUuids.set(
                    tigerUuids.stream().map(UUID::toString).collect(Collectors.toList())
            );
            CommonConfig.GENERAL.tigerUuids.save();

            ITextComponent msg = new StringTextComponent("Player ").append(player.getName()).appendString("has become a tiger!");
            context.getSource().sendFeedback(msg, true);

            return Command.SINGLE_SUCCESS;
        } catch (CommandSyntaxException e) {
            context.getSource().sendErrorMessage(new StringTextComponent("Something wrong :("));
            e.printStackTrace();
        }
        return 0;
    }

    public static int removePlayer(CommandContext<CommandSource> context) {
        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
            tigerUuids.remove(player.getUniqueID());
            CommonConfig.GENERAL.tigerUuids.set(
                    tigerUuids.stream().map(UUID::toString).collect(Collectors.toList())
            );
            CommonConfig.GENERAL.tigerUuids.save();

            ITextComponent msg = new StringTextComponent("Player ").append(player.getName()).appendString("is not a tiger anymore");
            context.getSource().sendFeedback(msg, true);

            return Command.SINGLE_SUCCESS;
        } catch (CommandSyntaxException e) {
            context.getSource().sendErrorMessage(new StringTextComponent("Something wrong :("));
            e.printStackTrace();
        }
        return 0;
    }

    public void setup(final FMLCommonSetupEvent event) {
        KusHandler events = new KusHandler();
        MinecraftForge.EVENT_BUS.register(events);
    }

    public static void startup(final FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();

        LiteralArgumentBuilder<CommandSource> builder = literal("tigerkus")
                .requires(src -> src.hasPermissionLevel(4))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(TigerKus::addPlayer)))
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(TigerKus::removePlayer)));

        dispatcher.register(builder);

        for (String uuid : CommonConfig.GENERAL.tigerUuids.get()) {
            tigerUuids.add(UUID.fromString(uuid));
        }
    }
}
