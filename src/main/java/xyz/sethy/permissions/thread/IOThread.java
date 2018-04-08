package xyz.sethy.permissions.thread;

import xyz.sethy.permissions.Main;
import xyz.sethy.permissions.dto.PermissionsUser;


public class IOThread extends Thread {
    public IOThread() {
        setName("Permissions - IOThread");
    }

    @Override
    public void run() {
        for (PermissionsUser permissionsUser : Main.getInstance().getPermissionsUserHandler().findAll()) {
            if (permissionsUser.getNeedsUpdating().get()) {
                permissionsUser.save();
            }
        }
    }
}
