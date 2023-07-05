package net.serble.serblenetworkplugin.API;

import net.serble.serblenetworkplugin.GameProfileUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IdServiceImpl implements IdService {
    @Override
    public UUID getPlayerUuid(Player p) {
        return GameProfileUtils.getPlayerUuid(p);
    }

    @Override
    public UUID getPlayerUuid(UUID p) {
        return GameProfileUtils.getPlayerUuid(p);
    }

    @Override
    public UUID getPlayerFromProfile(UUID profile) {
        return GameProfileUtils.getPlayerFromProfile(profile);
    }
}
