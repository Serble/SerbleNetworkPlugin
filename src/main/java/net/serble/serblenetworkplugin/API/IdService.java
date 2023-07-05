package net.serble.serblenetworkplugin.API;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IdService {
        UUID getPlayerUuid(Player p);
        UUID getPlayerUuid(UUID p);
        UUID getPlayerFromProfile(UUID profile);
}
