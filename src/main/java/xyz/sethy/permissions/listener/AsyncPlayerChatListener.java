package xyz.sethy.permissions.listener;

import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.Group;
import xyz.sethy.permissions.dto.PermissionsUser;
import xyz.sethy.permissions.event.PermissionChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PermissionsUser permissionsUser = Main.getInstance().getPermissionsUserHandler().findByUniqueId(player.getUniqueId());
        Group group = Main.getInstance().findByName(permissionsUser.getGroup().get());

        PermissionChatEvent permissionChatEvent = new PermissionChatEvent(player, group, "", "");
        Bukkit.getPluginManager().callEvent(permissionChatEvent);
        if (permissionChatEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        event.setFormat(ChatColor.translateAlternateColorCodes('&', group.getPrefix() + permissionChatEvent.getPrefix() + "%s" + group.getSuffx() + permissionChatEvent.getSuffix() + " &6\u00BB &r") + "%s");
    }
}
