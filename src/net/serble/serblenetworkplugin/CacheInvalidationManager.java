package net.serble.serblenetworkplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class CacheInvalidationManager implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        invalidateCacheForPlayer(GameProfileUtils.getPlayerUuid(e.getPlayer()));
    }

    public static void invalidateCacheForPlayer(UUID playerUuid) {
        ExperienceManager.invalidateCacheForUser(playerUuid);
        MoneyCacheManager.invalidateCacheForPlayer(playerUuid);
        AchievementsManager.invalidatePlayerAchievementsCache(playerUuid);
        AdminModeCacheHandler.invalidateCacheForPlayer(playerUuid);
        NicknameManager.invalidateCache(playerUuid);
        invalidateUuidCachesForPlayer(playerUuid);
    }

    public static void invalidateUuidCachesForPlayer(UUID playerUuid) {
        PlayerUuidCacheHandler.getInstance().invalidatePlayerUuid(playerUuid);
        PlayerUuidCacheHandler.getInstance().invalidateProfileList(playerUuid);
    }

}
