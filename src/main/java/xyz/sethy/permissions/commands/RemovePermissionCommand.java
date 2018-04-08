package xyz.sethy.permissions.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.PermissionsUser;

public class RemovePermissionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("permissions.removepermission")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /removepermission <player> <permission>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "We couldn't find a player with the name of '" + args[0] + "'.");
            return true;
        }

        PermissionsUser permissionsUser = Main.getInstance().getPermissionsUserHandler().findByUniqueId(target.getUniqueId());
        String permission = args[1];

        if (!permissionsUser.getPermissions().contains(permission)) {
            sender.sendMessage(ChatColor.RED + String.format("The player '%s' does not have the permission '%s'.", target.getName(), permission));
            return true;
        }

        permissionsUser.getPermissions().remove(permission);
        sender.sendMessage(ChatColor.YELLOW + "You have remove the permission '" + ChatColor.GREEN + permission + ChatColor.YELLOW + "' from " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + ".");

        if (target.isOnline()) {
            Player targetPlayer = (Player) target;
            Main.getInstance().applyPermissions(targetPlayer, permissionsUser);
            targetPlayer.sendMessage(ChatColor.RED + String.format("You no longer have the permission '%s'.", permission));
        }
        permissionsUser.getNeedsUpdating().lazySet(true);
        return true;
    }
}
