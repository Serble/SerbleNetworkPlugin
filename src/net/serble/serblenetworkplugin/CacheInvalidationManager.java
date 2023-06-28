package net.serble.serblenetworkplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class CacheInvalidationManager implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        UUID playerUuid = GameProfileUtils.getPlayerUuid(e.getPlayer());
        ExperienceManager.invalidateCacheForUser(playerUuid);
        MoneyCacheManager.invalidateCacheForPlayer(playerUuid);
        AdminModeCacheHandler.invalidateCacheForPlayer(playerUuid);
        AchievementsManager.invalidatePlayerAchievementsCache(playerUuid);
        NicknameManager.invalidateCache(e.getPlayer().getUniqueId());
    }

}
