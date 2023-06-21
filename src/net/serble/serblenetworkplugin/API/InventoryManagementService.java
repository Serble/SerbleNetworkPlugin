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
        if (p.getWorld().getUID() != loc.getWorld().getUID()) throw new IllegalArgumentException("Player and location must be in the same world!");
        String worldGroup = worldGroupInventoryManager.getPlayerWorldGroup(p);
        worldGroupInventoryManager.setPlayerCurrentSpawnPoint(GameProfileUtils.getPlayerUuid(p), worldGroup, loc);
    }

    public Location getSpawnPoint(Player p) {
        String worldGroup = worldGroupInventoryManager.getPlayerWorldGroup(p);
        return worldGroupInventoryManager.getPlayerCurrentSpawnPoint(GameProfileUtils.getPlayerUuid(p), worldGroup);
    }

    public void saveInventory(Player p) {
        worldGroupInventoryManager.savePlayerInventory(p);
    }

    public void loadInventory(Player p) {
        worldGroupInventoryManager.loadPlayerInventory(p);
    }

}
