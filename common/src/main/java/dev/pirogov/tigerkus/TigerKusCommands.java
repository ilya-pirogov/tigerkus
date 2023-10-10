package dev.pirogov.tigerkus;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TigerKusCommands {
    TigerKusCommands(MinecraftServer instance) {
        CommandDispatcher<CommandSourceStack> dispatcher = instance.getCommands().getDispatcher();
        dispatcher.register(this.addCommand());
        dispatcher.register(this.removeCommand());
        dispatcher.register(this.reloadCommand());
    }

    public LiteralArgumentBuilder<CommandSourceStack> tigerkusCategory() {
        return literal("tigerkus").requires(cs -> cs.hasPermission(2));
    }

    public RequiredArgumentBuilder<CommandSourceStack, GameProfileArgument.Result> playerArgument() {
        return argument("player", GameProfileArgument.gameProfile());
    }

    public LiteralArgumentBuilder<CommandSourceStack> addCommand() {
        return tigerkusCategory().then(literal("add").then(playerArgument().executes(ctx -> {
            int added = 0;

            Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "player");
            for (GameProfile profile : profiles) {
                TigerKus.CONFIG.addTiger(profile.getId());
                ctx.getSource().sendSuccess(Component.translatable("Tiger %s has been added to the list!", profile.getName()), false);
            }

            return added;
        })));
    }

    public LiteralArgumentBuilder<CommandSourceStack> removeCommand() {
        return tigerkusCategory().then(literal("remove").then(playerArgument().executes(ctx -> {
            int removed = 0;

            Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "player");
            for (GameProfile profile : profiles) {
                TigerKus.CONFIG.removeTiger(profile.getId());
                ctx.getSource().sendSuccess(Component.translatable("%s is not Tiger anymore :(", profile.getName()), false);
            }

            return removed;
        })));
    }


    public LiteralArgumentBuilder<CommandSourceStack> reloadCommand() {
        return tigerkusCategory().then(literal("reload").executes(ctx -> {
            TigerKus.CONFIG.reload();
            ctx.getSource().sendSuccess(Component.translatable("Config has been reloaded"), false);
            return 0;
        }));
    }
}
