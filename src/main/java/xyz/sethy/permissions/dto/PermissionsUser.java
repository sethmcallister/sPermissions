package xyz.sethy.permissions.dto;

import com.google.gson.Gson;
import xyz.sethy.permissions.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PermissionsUser {
    private final UUID uniqueId;
    private final AtomicReference<String> group;
    private final List<String> subGroups;
    private final List<String> permissions;
    private final AtomicBoolean needsUpdating;

    public PermissionsUser(final UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.group = new AtomicReference<>("");
        this.subGroups = new LinkedList<>();
        this.permissions = new LinkedList<>();
        this.needsUpdating = new AtomicBoolean(false);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public AtomicReference<String> getGroup() {
        return group;
    }

    public List<String> getSubGroups() {
        return subGroups;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public AtomicBoolean getNeedsUpdating() {
        return needsUpdating;
    }

    public void save() {
        File file = new File(Main.getInstance().getDataFolder() + File.separator + "users" + File.separator + getUniqueId() + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(new Gson().toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
