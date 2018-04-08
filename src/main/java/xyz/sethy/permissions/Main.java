package xyz.sethy.permissions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.sethy.permissions.commands.*;
import xyz.sethy.permissions.dto.Group;
import xyz.sethy.permissions.dto.PermissionsUser;
import xyz.sethy.permissions.expection.NoDefaultGroupExpection;
import xyz.sethy.permissions.handler.PermissionsUserHandler;
import xyz.sethy.permissions.listener.AsyncPlayerChatListener;
import xyz.sethy.permissions.listener.PlayerJoinListener;
import xyz.sethy.permissions.thread.IOThread;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {
    private static Main instance;
    private Gson gson;
    private List<Group> groups;
    private String fileName;
    private PermissionsUserHandler permissionsUserHandler;
    private ScheduledExecutorService scheduledExecutorService;

    public static Main getInstance() {
        return instance;
    }

    private static void setInstance(Main newInstance) {
        instance = newInstance;
    }

    @Override
    public void onLoad() {
        setInstance(this);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.fileName = getDataFolder() + File.separator + "groups.json";
        this.groups = new ArrayList<>();
        this.permissionsUserHandler = new PermissionsUserHandler();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        loadGroups();
    }

    @Override
    public void onEnable() {
        if (this.findDefaultGroup() == null) {
            try {
                throw new NoDefaultGroupExpection();
            } catch (NoDefaultGroupExpection NoDefaultGroupExpection) {
                NoDefaultGroupExpection.printStackTrace();
            }
        }

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
        getCommand("setrank").setExecutor(new SetRankCommand());
        getCommand("reloadgroups").setExecutor(new ReloadGroupsCommand());
        getCommand("addpermission").setExecutor(new AddPermissionCommand());
        getCommand("removepermission").setExecutor(new RemovePermissionCommand());
        getCommand("permissionsinfo").setExecutor(new PermissionsInfoCommand());
        getCommand("addsubgroup").setExecutor(new AddSubGroupCommand());
        getCommand("removesubgroup").setExecutor(new RemoveSubGroupCommand());

        this.scheduledExecutorService.scheduleAtFixedRate(new IOThread(), 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        String json = this.gson.toJson(this.groups);

        File file = new File(this.fileName);
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGroups() {
        File file = new File(this.fileName);
        if (!file.exists()) {
            getDataFolder().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JsonParser jsonParser = new JsonParser();

            try (FileReader fileReader = new FileReader(this.fileName)) {
                JsonElement element = jsonParser.parse(fileReader);
                Type type = new TypeToken<List<Group>>() {
                }.getType();
                List<Group> groups1 = this.gson.fromJson(element, type);
                this.groups.addAll(groups1);
                System.out.println(element);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.groups.isEmpty()) {
            Group group = new Group("member", "&7", "", Collections.emptyList(), Collections.emptyList(), true);
            this.groups.add(group);
        }
    }

    public Group findByName(final String name) {
        return this.groups.stream().filter(group -> group.getName().equalsIgnoreCase(name)).findFirst().orElse(this.findDefaultGroup());
    }

    public Group findDefaultGroup() {
        return this.groups.stream().filter(Group::getDefaultGroup).findFirst().orElse(null);
    }

    public void applyPermissions(Player player, PermissionsUser permissionsUser) {
        List<Group> groups = new ArrayList<>();

        if (permissionsUser == null) {
            permissionsUser = getPermissionsUserHandler().findByUniqueId(player.getUniqueId());
        }

        Group group = this.findByName(permissionsUser.getGroup().get());
        if (group == null)
            group = this.findDefaultGroup();

        groups.add(group);

        PermissionAttachment attachment = player.addAttachment(this);
        group.getPermissions().forEach(s -> attachment.setPermission(s, true));

        for (String s : group.getInherit()) {
            Group inherit = this.findByName(s);
            if (inherit == null)
                continue;

            inherit.getPermissions().forEach(p -> attachment.setPermission(p, true));
        }

        for (String s : permissionsUser.getSubGroups()) {
            Group group1 = findByName(s);
            if (group1 == null)
                continue;

            groups.add(group1);

            group1.getPermissions().forEach(p -> attachment.setPermission(p, true));

            for (String s1 : group.getInherit()) {
                Group inherit = this.findByName(s1);
                if (inherit == null)
                    continue;

                inherit.getPermissions().forEach(p -> attachment.setPermission(p, true));
            }
        }

        List<String> userPermissions = permissionsUser.getPermissions();
        userPermissions.forEach(p -> attachment.setPermission(p, true));

        for (PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()) {

            if (!playerHasPermission(player, permissionAttachmentInfo, userPermissions, groups))
                attachment.unsetPermission(permissionAttachmentInfo.getPermission());

        }
    }

    private boolean playerHasPermission(final Player player, final PermissionAttachmentInfo permssion, final List<String> permissions, final List<Group> groups) {
        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (permissions.contains(attachmentInfo.getPermission()))
                continue;

            return (anyGroupHasPermission(groups, permssion.getPermission()));
        }
        return false;
    }

    private boolean anyGroupHasPermission(final List<Group> groups, final String permission) {
        for (Group group : groups) {
            if (group.getPermissions().contains(permission))
                return true;

            for (String inheritSTR : group.getInherit()) {
                Group inherit = findByName(inheritSTR);
                if (inherit.getPermissions().contains(permission))
                    return true;
            }
        }

        return false;
    }

    public PermissionsUserHandler getPermissionsUserHandler() {
        return permissionsUserHandler;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}
