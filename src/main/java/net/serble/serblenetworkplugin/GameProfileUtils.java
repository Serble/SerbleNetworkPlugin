package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Cache.PlayerUuidCacheHandler;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameProfileUtils {

    public static UUID getPlayerUuid(Player p) {
        return PlayerUuidCacheHandler.getInstance().getPlayerUuid(p.getUniqueId());
    }

    public static UUID getPlayerUuid(UUID p) {
        return PlayerUuidCacheHandler.getInstance().getPlayerUuid(p);
    }

    public static UUID getPlayerFromProfile(UUID profile) {
        UUID id = PlayerUuidCacheHandler.getInstance().getPlayerFromProfile(profile);
        if (id == null) return profile;
        return id;
    }

}
