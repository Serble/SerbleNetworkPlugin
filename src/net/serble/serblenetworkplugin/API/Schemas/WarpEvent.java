package net.serble.serblenetworkplugin.API.Schemas;

import org.bukkit.entity.Player;

import java.util.List;

public class WarpEvent {
    private Player partyLeader;
    private Player target;  // Target should be warped to party leader

    public WarpEvent(Player partyLeader, Player target) {
        this.partyLeader = partyLeader;
        this.target = target;
    }

    public Player getPartyLeader() {
        return this.partyLeader;
    }

    public Player getTarget() {
        return this.target;
    }
}
