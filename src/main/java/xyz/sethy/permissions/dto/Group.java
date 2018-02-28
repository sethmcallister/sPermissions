package xyz.sethy.permissions.dto;

import java.util.List;

public class Group {
    private final String name;
    private final String prefix;
    private final String suffx;
    private final List<String> permissions;
    private final List<String> inherit;
    private final Boolean defaultGroup;

    public Group(final String name, final String prefix, final String suffx, final List<String> permissions, final List<String> inherit, final Boolean defaultGroup) {
        this.name = name;
        this.prefix = prefix;
        this.suffx = suffx;
        this.permissions = permissions;
        this.inherit = inherit;
        this.defaultGroup = defaultGroup;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffx() {
        return suffx;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getInherit() {
        return inherit;
    }

    public String getName() {
        return name;
    }

    public Boolean getDefaultGroup() {
        return defaultGroup;
    }
}
