package xyz.sethy.permissions.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.Group;
import xyz.sethy.permissions.dto.PermissionsUser;

public class SetRankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("permissions.setrank")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /setrank <player> <group>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "We couldn't find a player with the name of '" + args[0] + "'.");
            return true;
        }

        PermissionsUser permissionsUser = Main.getInstance().getPermissionsUserHandler().findByUniqueId(target.getUniqueId());

        Group group = Main.getInstance().findByName(args[1]);
        if (group == null) {
            sender.sendMessage(ChatColor.RED + "No group with the name '" + args[1] + "' was found.");
            return true;
        }

        permissionsUser.getGroup().lazySet(group.getName());
        sender.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + "'s rank to '" + ChatColor.GREEN + group.getName() + ChatColor.YELLOW + "'.");
        if (target.isOnline()) {
            Player targetPlayer = (Player) target;
            targetPlayer.sendMessage(ChatColor.YELLOW + "Your rank has been set to '" + ChatColor.GREEN + group.getName() + ChatColor.YELLOW + "'.");
            Main.getInstance().applyPermissions(targetPlayer, permissionsUser);
        }
        permissionsUser.getNeedsUpdating().lazySet(true);
        return true;
    }
}
