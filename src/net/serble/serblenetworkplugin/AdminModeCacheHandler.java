package net.serble.serblenetworkplugin;

import java.util.HashMap;
import java.util.UUID;

public class AdminModeCacheHandler {
    // PLEASE NOTE THAT UUID MUST BE THE MOJANG UUID, NOT PROFILE UUID
    public static HashMap<UUID, Boolean> adminModeCache = new HashMap<>();

    public static void setAdminMode(UUID player, boolean adminMode) {
        adminModeCache.put(player, adminMode);
        Functions.runAsync(() -> Main.sqlData.setAdminMode(player, adminMode), true);
    }

    public static boolean isAdminMode(UUID player) {
        if (adminModeCache.containsKey(player)) {
            return adminModeCache.get(player);
        } else {
            boolean adminMode = Main.sqlData.getAdminMode(player);
            adminModeCache.put(player, adminMode);
            return adminMode;
        }
    }

    public static void invalidateCacheForPlayer(UUID player) {
        adminModeCache.remove(player);
    }
}
