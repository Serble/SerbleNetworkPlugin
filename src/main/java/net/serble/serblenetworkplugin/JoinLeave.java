package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Cache.CacheInvalidationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.serble.serblenetworkplugin.Functions.getPlayerRankDisplay;

public class JoinLeave implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        // Make sure that we have no invalid cache
        CacheInvalidationManager.invalidateCacheForPlayer(p.getUniqueId());
        DebugManager.getInstance().debug(p, "Welcome! Your cache has been invalidated.");

        // update nickname
        if (Main.sqlData.existsInNicks(GameProfileUtils.getPlayerUuid(p))) {
            p.setDisplayName(Main.sqlData.getNick(GameProfileUtils.getPlayerUuid(p)));
        }

        // Tell them their current profile
        String profileName = Main.sqlData.getGameProfileName(GameProfileUtils.getPlayerUuid(p));
        if (profileName == null) profileName = "default";
        p.sendMessage(Functions.translate("&aActive profile: &7" + profileName));

        // send join message
        if (Main.hasConfig) {
            String rankDisplay = getPlayerRankDisplay(p);
            String finalMessage = "&2+ &r" + rankDisplay + " " + p.getDisplayName();
            e.setJoinMessage(Functions.translate(finalMessage));
        } else {
            e.setJoinMessage("");
            ConfigManager.runTaskAfterConfig(() -> {
                String rankDisplay = getPlayerRankDisplay(p);
                String finalMessage = "&2+ &r" + rankDisplay + " " + p.getDisplayName();
                Bukkit.broadcastMessage(Functions.translate(finalMessage));
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (Main.hasConfig) {
            String rankDisplay = getPlayerRankDisplay(p);
            String finalMessage = "&2- &r" + rankDisplay + " " + p.getDisplayName();
            e.setQuitMessage(Functions.translate(finalMessage));
        } else {
            e.setQuitMessage("");
            ConfigManager.runTaskAfterConfig(() -> {
                String rankDisplay = getPlayerRankDisplay(p);
                String finalMessage = "&2- &r" + rankDisplay + " " + p.getDisplayName();
                Bukkit.broadcastMessage(Functions.translate(finalMessage));
            });
        }
    }

}
