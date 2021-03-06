package xyz.sethy.permissions.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.sethy.permissions.Main;

public class ReloadGroupsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("permissions.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return true;
        }
        Main.getInstance().loadGroups();
        sender.sendMessage(ChatColor.YELLOW + "You have successfully reloaded the groups.");
        Bukkit.getOnlinePlayers().forEach(player -> Main.getInstance().applyPermissions(player, null));
        return true;
    }
}
