package net.serble.serblenetworkplugin.Schemas;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class StoredInventory {
    private final ItemStack[] inventoryContents;
    private final ItemStack[] enderChestContents;
    private final double health;
    private final int foodLevel;
    private final float exp;
    private final int level;
    private final org.bukkit.GameMode gameMode;
    private final Location location;
    private final Collection<PotionEffect> potionEffects;
    private Location spawnLocation;

    public StoredInventory(
            ItemStack[] inventoryContents,
            ItemStack[] enderChest,
            double health,
            int foodLevel,
            float exp,
            int level,
            org.bukkit.GameMode gameMode,
            Location location,
            Collection<PotionEffect> potionEffects,
            @Nullable Location spawnLocation) {
        this.inventoryContents = inventoryContents;
        this.enderChestContents = enderChest;
        this.health = health;
        this.foodLevel = foodLevel;
        this.exp = exp;
        this.level = level;
        this.gameMode = gameMode;
        this.location = location;
        this.potionEffects = potionEffects;
        this.spawnLocation = spawnLocation;
    }

    public ItemStack[] getInventoryContents() {
        return inventoryContents;
    }

    public ItemStack[] getEnderChestContents() {
        return enderChestContents;
    }

    public double getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public org.bukkit.GameMode getGameMode() {
        return gameMode;
    }

    public Location getLocation() {
        return location;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public static StoredInventory getDefaultInventory() {
        return new StoredInventory(new ItemStack[36], new ItemStack[27], 20, 20, 0, 0, GameMode.SURVIVAL, null, new ArrayList<>(), null);
    }
}