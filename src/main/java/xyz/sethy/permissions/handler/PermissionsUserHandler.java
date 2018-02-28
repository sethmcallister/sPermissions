package xyz.sethy.permissions.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.PermissionsUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionsUserHandler {
    private final Gson gson;
    private final List<PermissionsUser> permissionsUsers;

    public PermissionsUserHandler() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.permissionsUsers = new ArrayList<>();

        loadData();
    }

    private void loadData() {
        File file = new File(Main.getInstance().getDataFolder() + File.separator + "users");
        if (!file.exists()) {
            file.mkdir();
            return;
        }

        for (int i = 0; i < file.listFiles().length; i++) {
            File userFile = file.listFiles()[i];

            JsonParser parser = new JsonParser();
            try {
                JsonElement element = parser.parse(new FileReader(userFile));
                PermissionsUser practicePlayer = this.gson.fromJson(element, PermissionsUser.class);
                this.permissionsUsers.add(practicePlayer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public PermissionsUser findByUniqueId(final UUID uuid) {
        return this.permissionsUsers.stream().filter(permissionsUser -> permissionsUser.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public List<PermissionsUser> findAll() {
        return permissionsUsers;
    }
}
