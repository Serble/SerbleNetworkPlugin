package net.serble.serblenetworkplugin.API;

import net.serble.serblenetworkplugin.API.Schemas.WarpEvent;
import net.serble.serblenetworkplugin.API.Schemas.WarpEventListener;
import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.Party;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyService {
    private final List<WarpEventListener> warpEventListeners = new ArrayList<>();

    public void registerWarpListener(WarpEventListener listener) {
        this.warpEventListeners.add(listener);
    }

    public boolean sendWarpEventToListeners(WarpEvent event) {
        for (WarpEventListener listener : this.warpEventListeners) {
            if (listener.onWarpEvent(event)) {
                return true;
            }
        }
        return false;
    }

    public void triggerWarp(Player leader, boolean delayed) {
        // If they are not the leader of a party then return
        Party party = Main.partyManager.getParty(leader);
        if (party != null && party.getLeader() == leader.getUniqueId()) {
            DebugManager.getInstance().debug(leader, "Ignoring warp request because you are not the leader of a party.");
            return;
        }

        Main.partyManager.sendWarpToBungee(leader, delayed);
    }

    public void triggerWarp(Player leader) {
        triggerWarp(leader, false);
    }

    public void triggerDelayedWarp(Player leader) {
        triggerWarp(leader, true);
    }

    public boolean canJoinGame(Player player) {
        return Main.partyManager.canJoinGame(player);
    }

    public boolean canJoinGameAndAlert(Player player) {
        boolean canJoin = Main.partyManager.canJoinGame(player);
        if (!canJoin) {
            player.sendMessage(Functions.translate("&cOnly the party leader can join games."));
        }
        return canJoin;
    }

    public boolean canJoinGameAndAlertOrWarp(Player player) {
        boolean canJoin = canJoinGameAndAlert(player);
        if (!canJoin) {
            return false;
        }
        triggerWarp(player, false);
        return true;
    }

    public boolean canJoinGameAndAlertOrDelayedWarp(Player player) {
        boolean canJoin = canJoinGameAndAlert(player);
        if (!canJoin) {
            return false;
        }
        triggerWarp(player, true);
        return true;
    }

}
