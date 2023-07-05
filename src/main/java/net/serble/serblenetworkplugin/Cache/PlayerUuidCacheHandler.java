package net.serble.serblenetworkplugin.Cache;

import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerUuidCacheHandler {

    private static PlayerUuidCacheHandler instance;
    private final HashMap<UUID, UUID> uuidCache = new HashMap<>();
    private final HashMap<UUID, UUID> backwardsCache = new HashMap<>();
    private final HashMap<UUID, List<String>> profileListCache = new HashMap<>();
    private final HashMap<UUID, String> profileNameCache = new HashMap<>();

    public static PlayerUuidCacheHandler getInstance() {
        if (instance == null) {
            instance = new PlayerUuidCacheHandler();
        }
        return instance;
    }

    public PlayerUuidCacheHandler() {
        if (instance != null) {
            throw new IllegalStateException("Cannot instantiate a new singleton instance of singleton class PlayerUuidCacheHandler");
        }
    }

    public String dumpUuidCache() {
        StringBuilder sb = new StringBuilder();
        sb.append("UUID Cache:\n");
        for (UUID uuid : uuidCache.keySet()) {
            sb.append(uuid.toString()).append(" -> ").append(uuidCache.get(uuid).toString()).append("\n");
        }
        return sb.toString();
    }

    public UUID getPlayerUuid(UUID playerUuid) {
        if (uuidCache.containsKey(playerUuid)) {
            return uuidCache.get(playerUuid);
        }
        UUID uuid = Main.sqlData.getActiveUuid(playerUuid);
        uuidCache.put(playerUuid, uuid);
        backwardsCache.put(uuid, playerUuid);
        Player p = Bukkit.getPlayer(playerUuid);
        if (p != null) {
            DebugManager.getInstance().debug(p, "Your UUID has been updated to " + uuid.toString() + " in the cache.");
        }
        return uuid;
    }

    public void invalidatePlayerUuid(UUID playerUuid) {
        UUID lastId = uuidCache.remove(playerUuid);
        if (lastId != null) backwardsCache.remove(lastId);
        Player p = Bukkit.getPlayer(playerUuid);
        if (p != null) {
            DebugManager.getInstance().debug(p, "Your UUID was invalidated in the cache.");
        }
    }

    public void invalidateProfileList(UUID playerUuid) {
        profileListCache.remove(playerUuid);
    }

    public UUID getPlayerFromProfile(UUID profile) {
        if (backwardsCache.containsKey(profile)) {
            return backwardsCache.get(profile);
        }
        UUID uuid = Main.sqlData.getMojangUserFromProfile(profile);
        backwardsCache.put(profile, uuid);
        return uuid;
    }

    public List<String> getProfileList(UUID playerUuid) {
        if (profileListCache.containsKey(playerUuid)) {
            return profileListCache.get(playerUuid);
        }
        List<String> profileList = Main.sqlData.listUserProfiles(playerUuid);
        profileListCache.put(playerUuid, profileList);
        return profileList;
    }

    public String getProfileName(UUID profile) {
        if (profileNameCache.containsKey(profile)) {
            return profileNameCache.get(profile);
        }
        String profileName = Main.sqlData.getGameProfileName(profile);
        if (profileName == null) {
            profileName = "default";
        }
        profileNameCache.put(profile, profileName);
        return profileName;
    }

}
