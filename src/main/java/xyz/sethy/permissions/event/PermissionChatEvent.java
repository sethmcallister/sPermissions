package xyz.sethy.permissions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.sethy.permissions.dto.Group;

public class PermissionChatEvent extends Event implements Cancellable {

    private final Player player;
    private final Group group;
    private String prefix;
    private String suffix;
    private boolean cancelled;

    public PermissionChatEvent(final Player player, final Group group, final String prefix, final String suffix) {
        this.player = player;
        this.group = group;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }

    public HandlerList getHandler() {
        return new HandlerList();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Player getPlayer() {
        return player;
    }

    public Group getGroup() {
        return group;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
