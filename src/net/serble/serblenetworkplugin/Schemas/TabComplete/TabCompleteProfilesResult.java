package net.serble.serblenetworkplugin.Schemas.TabComplete;

import net.serble.serblenetworkplugin.PlayerUuidCacheHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteProfilesResult implements TabCompleteSlotResult {
    private final Player player;

    public TabCompleteProfilesResult(Player p) {
        player = p;
    }

    @Override
    public List<String> get() {
        List<String> results = new ArrayList<>(PlayerUuidCacheHandler.getInstance().getProfileList(player.getUniqueId()));
        results.add("default");
        return results;
    }
}
