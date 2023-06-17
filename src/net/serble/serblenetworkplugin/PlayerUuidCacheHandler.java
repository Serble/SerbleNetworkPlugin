package net.serble.serblenetworkplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerUuidCacheHandler implements Listener {

    private static PlayerUuidCacheHandler instance;
    private final HashMap<UUID, UUID> uuidCache = new HashMap<>();
    private final HashMap<UUID, UUID> backwardsCache = new HashMap<>();
    private final HashMap<UUID, List<String>> profileListCache = new HashMap<>();

    public static PlayerUuidCacheHandler getInstance() {
        if (instance == null) {
            instance = new PlayerUuidCacheHandler();
        }
        return instance;
    }

    private PlayerUuidCacheHandler() {
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }

    public UUID getPlayerUuid(UUID playerUuid) {
        if (uuidCache.containsKey(playerUuid)) {
            return uuidCache.get(playerUuid);
        }
        UUID uuid = Main.sqlData.getActiveUuid(playerUuid);
        uuidCache.put(playerUuid, uuid);
        backwardsCache.put(uuid, playerUuid);
        return uuid;
    }

    public void invalidatePlayerUuid(UUID playerUuid) {
        UUID lastId = uuidCache.remove(playerUuid);
        if (lastId != null) backwardsCache.remove(lastId);
    }

    public void invalidateProfileList(UUID playerUuid) {
        profileListCache.remove(playerUuid);
    }

    public UUID getPlayerFromProfile(UUID profile) {
        if (backwardsCache.containsKey(profile)) {
            return backwardsCache.get(profile);
        }
        UUID uuid = Main.sqlData.getMojangUserFromProfile(profile);
        uuidCache.put(uuid, profile);
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        invalidatePlayerUuid(event.getPlayer().getUniqueId());
    }

}
