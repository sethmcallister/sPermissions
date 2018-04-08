package xyz.sethy.permissions.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.PermissionsUser;

public class PermissionsInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("permissions.info")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /permissionsinfo <player>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "We couldn't find a player with the name of '" + args[0] + "'.");
            return true;
        }

        PermissionsUser permissionsUser = Main.getInstance().getPermissionsUserHandler().findByUniqueId(target.getUniqueId());

        sender.sendMessage(ChatColor.RED + String.format("%s's Permissions Information:", target.getName()));
        sender.sendMessage(ChatColor.RED + String.format(" Super Group: %s", permissionsUser.getGroup()));
        sender.sendMessage(ChatColor.RED + " Sub Groups: ");
        permissionsUser.getSubGroups().forEach(sub -> sender.sendMessage(String.format("  %s", sub)));
        sender.sendMessage(ChatColor.RED + " Permissions: ");
        permissionsUser.getPermissions().forEach(perm -> sender.sendMessage(String.format("  %s", perm)));
        sender.sendMessage(ChatColor.RED + " Awaiting Update: " + permissionsUser.getNeedsUpdating().get());
        return true;
    }
}
