package net.serble.serblenetworkplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheInvalidationManager implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        ExperienceManager.invalidateCacheForUser(GameProfileUtils.getPlayerUuid(e.getPlayer()));
        MoneyCacheManager.invalidateCacheForPlayer(GameProfileUtils.getPlayerUuid(e.getPlayer()));
        AdminModeCacheHandler.invalidateCacheForPlayer(GameProfileUtils.getPlayerUuid(e.getPlayer()));
    }

}
