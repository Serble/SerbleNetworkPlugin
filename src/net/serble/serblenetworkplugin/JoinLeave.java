package net.serble.serblenetworkplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.serble.serblenetworkplugin.Functions.getPlayerRankDisplay;

public class JoinLeave implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        // update nickname
        if (Main.sqlData.existsInNicks(GameProfileUtils.getPlayerUuid(p))) {
            p.setDisplayName(Main.sqlData.getNick(GameProfileUtils.getPlayerUuid(p)));
        }

        // send join message
        String rankDisplay = getPlayerRankDisplay(p);
        String finalMessage = "&2+ &r" + rankDisplay + " " + p.getDisplayName();
        e.setJoinMessage(Functions.translate(finalMessage));

        // Tell them their current profile
        String profileName = Main.sqlData.getGameProfileName(GameProfileUtils.getPlayerUuid(p));
        p.sendMessage(Functions.translate("&aActive profile: &7" + profileName));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String rankDisplay = getPlayerRankDisplay(p);
        String finalMessage = "&2- &r" + rankDisplay + " " + p.getDisplayName();
        e.setQuitMessage(Functions.translate(finalMessage));
    }

}
