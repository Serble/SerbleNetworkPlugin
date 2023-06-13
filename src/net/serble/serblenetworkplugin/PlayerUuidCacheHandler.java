package net.serble.serblenetworkplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerUuidCacheHandler implements Listener {

    private static PlayerUuidCacheHandler instance;
    private final HashMap<UUID, UUID> uuidCache = new HashMap<>();

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
        return uuid;
    }

    public void invalidatePlayerUuid(UUID playerUuid) {
        uuidCache.remove(playerUuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        invalidatePlayerUuid(event.getPlayer().getUniqueId());
    }

}
