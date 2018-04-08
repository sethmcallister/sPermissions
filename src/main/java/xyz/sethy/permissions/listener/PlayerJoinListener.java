package xyz.sethy.permissions.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.PermissionsUser;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PermissionsUser permissionsUser = Main.getInstance().getPermissionsUserHandler().findByUniqueId(event.getPlayer().getUniqueId());
                if (permissionsUser == null) {
                    permissionsUser = new PermissionsUser(event.getPlayer().getUniqueId());
                    Main.getInstance().getPermissionsUserHandler().findAll().add(permissionsUser);
                    permissionsUser.getNeedsUpdating().lazySet(true);
                }

                Main.getInstance().applyPermissions(event.getPlayer(), permissionsUser);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
}
