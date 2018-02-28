package xyz.sethy.permissions.commands;

import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.Group;
import xyz.sethy.permissions.dto.PermissionsUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveSubGroupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("permissions.setrank")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /removesubgroup <player> <group>");
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
        if (permissionsUser.getSubGroups().contains(group.getName())) {
            sender.sendMessage(ChatColor.RED + String.format("The player '%s' does not have the sub group '%s'.", target.getName(), group.getName()));
            return true;
        }

        permissionsUser.getSubGroups().remove(group.getName());

        sender.sendMessage(ChatColor.YELLOW + "You have added the sub group '" + ChatColor.GREEN + group.getName() + ChatColor.YELLOW + "' to " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + ".");
        if (target.isOnline()) {
            Player playerTarget = (Player) target;
            playerTarget.sendMessage(ChatColor.YELLOW + "You have been removed from the sub group '" + ChatColor.GREEN + group.getName() + ChatColor.YELLOW + "'.");
            Main.getInstance().applyPermissions(playerTarget, permissionsUser);
        }
        permissionsUser.getNeedsUpdating().lazySet(true);
        return true;
    }
}
