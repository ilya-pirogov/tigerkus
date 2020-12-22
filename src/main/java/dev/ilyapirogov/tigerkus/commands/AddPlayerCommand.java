//package dev.ilyapirogov.tigerkus.commands;
//
//import com.mojang.authlib.GameProfile;
//import me.ilyapirogov.tigerkus.TigerKus;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommand;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.TextComponentString;
//import net.minecraftforge.server.permission.PermissionAPI;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class AddPlayerCommand implements ICommand {
//    private List<String> aliases;
//
//    public AddPlayerCommand() {
//        this.aliases = new ArrayList<>();
//        this.aliases.add(getName());
//    }
//
//    @Override
//    public String getName() {
//        return "tiger_add";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return String.format("/%s <player_name>", getName());
//    }
//
//    @Override
//    public List<String> getAliases() {
//        return aliases;
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if (args.length != 1) {
//            sender.sendMessage(new TextComponentString(
//                    String.format("Invalid argument. Usage: %s", getUsage(sender))));
//            return;
//        }
//
//        String playerName = args[0];
//        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
//        if (profile != null) {
//            TigerKus.addPlayer(profile.getId());
//            sender.sendMessage(new TextComponentString(
//                    String.format("§l§2%s§r has been added to Tiger list", profile.getName())));
//        } else {
//            sender.sendMessage(new TextComponentString(
//                    String.format("Player with name §l§2%s§r not found", playerName)));
//        }
//    }
//
//    @Override
//    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
//        return sender instanceof EntityPlayer && PermissionAPI.hasPermission((EntityPlayer) sender, TigerKus.PERM_ADD);
//    }
//
//    @Override
//    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//        if (args.length == 1) {
//            return Arrays.asList(server.getOnlinePlayerNames());
//        }
//        return new ArrayList<>();
//    }
//
//    @Override
//    public boolean isUsernameIndex(String[] args, int index) {
//        return index == 0;
//    }
//
//    @Override
//    public int compareTo(ICommand iCommand) {
//        return getName().compareTo(iCommand.getName());
//    }
//}
