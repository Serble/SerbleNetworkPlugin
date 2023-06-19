package net.serble.serblenetworkplugin.API;

import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.WorldGroupInventoryManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InventoryManagementService {
    private final WorldGroupInventoryManager worldGroupInventoryManager;

    public InventoryManagementService(WorldGroupInventoryManager worldGroupInventoryManager) {
        this.worldGroupInventoryManager = worldGroupInventoryManager;
    }

    public void setSpawnPoint(Player p, Location loc) {
        String worldGroup = worldGroupInventoryManager.getPlayerWorldGroup(p);
        worldGroupInventoryManager.setPlayerCurrentSpawnPoint(GameProfileUtils.getPlayerUuid(p), worldGroup, loc);
    }

    public Location getSpawnPoint(Player p) {
        String worldGroup = worldGroupInventoryManager.getPlayerWorldGroup(p);
        return worldGroupInventoryManager.getPlayerCurrentSpawnPoint(GameProfileUtils.getPlayerUuid(p), worldGroup);
    }
}
