package net.serble.serblenetworkplugin;

import org.bukkit.entity.Player;

import java.util.List;

public class PlayerGameModeLocator {

    public static String getGameMode(Player p) {
        List<String> worlds = Main.plugin.getConfig().getStringList("worlds");
        List<String> worldNames = Main.plugin.getConfig().getStringList("worldnames");

        String worldDisplay = Main.plugin.getConfig().getString("unknownworld");
        if (worldDisplay == null) {
            worldDisplay = "Unknown World";
        }

        for (int i = 0; i < worlds.size(); i++) {
            if (p.getWorld().getName().equals(worlds.get(i))) {
                worldDisplay = worldNames.get(i);
            }
        }
        return worldDisplay;
    }

}
