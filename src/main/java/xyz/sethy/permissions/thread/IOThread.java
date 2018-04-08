package xyz.sethy.permissions.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.PermissionsUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IOThread extends Thread {
    private final Gson gson;

    public IOThread() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        setName("Permissions - IOThread");
    }

    @Override
    public void run() {
        for (PermissionsUser permissionsUser : Main.getInstance().getPermissionsUserHandler().findAll()) {
            if (permissionsUser.getNeedsUpdating().get()) {
                savePlayer(permissionsUser);
            }
        }
    }

    private void savePlayer(final PermissionsUser permissionsUser) {
        File file = new File(Main.getInstance().getDataFolder() + File.separator + "users" + File.separator + permissionsUser.getUniqueId() + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.gson.toJson(permissionsUser));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
