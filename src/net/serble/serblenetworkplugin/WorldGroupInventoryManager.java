package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.API.DebugService;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class WorldGroupInventoryManager implements Listener {

    private final HashMap<UUID, HashMap<String, StoredInventory>> storedInventories = new HashMap<>();

    private void cacheInventory(UUID player, String worldGroup, StoredInventory storedInventory) {
        if (!storedInventories.containsKey(player)) {
            storedInventories.put(player, new HashMap<>());
        }
        storedInventories.get(player).put(worldGroup, storedInventory);
    }

    private StoredInventory getCacheInventory(UUID player, String worldGroup) {
        if (!storedInventories.containsKey(player)) {
            return null;
        }
        return storedInventories.get(player).get(worldGroup);
    }

    public WorldGroupInventoryManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.plugin);
    }

    public boolean isPlayerInGroup(String worldGroupName, Player player) {
        return Main.plugin.getConfig().getStringList("worldgroups." + worldGroupName + ".worlds").contains(player.getWorld().getName());
    }

    public String getPlayerWorldGroup(Player player) {
        for (String worldGroupName : Objects.requireNonNull(Main.plugin.getConfig().getConfigurationSection("worldgroups")).getKeys(false)) {
            if (isPlayerInGroup(worldGroupName, player)) {
                DebugManager.getInstance().debug(player, "You are in world group: " + worldGroupName);
                return worldGroupName;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DebugManager.getInstance().debug(player, "Loading your inventory... Your world: " + player.getWorld().getName());
        loadPlayerInventory(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DebugManager.getInstance().debug(player, "Saving your inventory... Your world: " + player.getWorld().getName());
        savePlayerInventory(player);
    }

    @EventHandler
    public void onPlayerLeaveWorld(PlayerTeleportEvent event) {
        if (Objects.requireNonNull(event.getFrom().getWorld()).getUID() == Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getUID()) {
            return;
        }
        Player player = event.getPlayer();
        DebugManager.getInstance().debug(player, "Saving your inventory... Your world: " + player.getWorld().getName());
        savePlayerInventory(player);
    }

    @EventHandler
    public void onPlayerJoinWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        DebugManager.getInstance().debug(player, "Loading your inventory... Your world: " + player.getWorld().getName());
        loadPlayerInventory(player);
    }

    public void saveAllInventories() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            DebugManager.getInstance().debug(player, "Saving your inventory... Your world: " + player.getWorld().getName());
            savePlayerInventory(player);
        }
    }

    private File getPlayerDataFile(Player player, String worldGroup) {
        UUID uuid = GameProfileUtils.getPlayerUuid(player);
        File worldGroupFolder = new File(Main.plugin.getDataFolder(), worldGroup);

        if (!worldGroupFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            worldGroupFolder.mkdirs();
        }

        File playerDataFile = new File(worldGroupFolder, uuid.toString() + ".yml");
        if (!playerDataFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                DebugManager.getInstance().debug(player, "Getting your inventory failed! Reason: " + e.getMessage());
            }
        }

        return playerDataFile;
    }

    private void savePlayerInventoryToFile(Player player, StoredInventory storedInventory, String worldGroup) {
        File playerDataFile = getPlayerDataFile(player, worldGroup);
        FileConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);

        playerDataConfig.set("inventory", storedInventory.getInventoryContents());
        playerDataConfig.set("enderChest", storedInventory.getEnderChestContents());
        playerDataConfig.set("health", storedInventory.getHealth());
        playerDataConfig.set("foodLevel", storedInventory.getFoodLevel());
        playerDataConfig.set("exp", storedInventory.getExp());
        playerDataConfig.set("level", storedInventory.getLevel());
        playerDataConfig.set("gameMode", storedInventory.getGameMode().toString());

        if (storedInventory.getLocation() != null) {
            playerDataConfig.set("location.world", Objects.requireNonNull(storedInventory.getLocation().getWorld()).getName());
            playerDataConfig.set("location.x", storedInventory.getLocation().getX());
            playerDataConfig.set("location.y", storedInventory.getLocation().getY());
            playerDataConfig.set("location.z", storedInventory.getLocation().getZ());
            playerDataConfig.set("location.yaw", storedInventory.getLocation().getYaw());
            playerDataConfig.set("location.pitch", storedInventory.getLocation().getPitch());
        }

        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
            DebugManager.getInstance().debug(player, "Failed to save your inventory to a file! Reason: " + e.getMessage());
        }
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    private StoredInventory loadPlayerInventoryFromFile(Player player, String worldGroup) {
        File playerDataFile = getPlayerDataFile(player, worldGroup);
        FileConfiguration playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);

        ItemStack[] inventory = Objects.requireNonNull(playerDataConfig.getList("inventory")).toArray(new ItemStack[0]);
        ItemStack[] enderChest = Objects.requireNonNull(playerDataConfig.getList("enderChest")).toArray(new ItemStack[0]);
        double health = playerDataConfig.getDouble("health");
        int foodLevel = playerDataConfig.getInt("foodLevel");
        float exp = (float) playerDataConfig.getDouble("exp");
        int level = playerDataConfig.getInt("level");
        GameMode gameMode = GameMode.valueOf(playerDataConfig.getString("gameMode"));

        World world = Bukkit.getWorld(Objects.requireNonNull(playerDataConfig.getString("location.world")));
        double x = playerDataConfig.getDouble("location.x");
        double y = playerDataConfig.getDouble("location.y");
        double z = playerDataConfig.getDouble("location.z");
        float yaw = (float) playerDataConfig.getDouble("location.yaw");
        float pitch = (float) playerDataConfig.getDouble("location.pitch");
        Location location = new Location(world, x, y, z, yaw, pitch);

        return new StoredInventory(inventory, enderChest, health, foodLevel, exp, level, gameMode, location);
    }

    public void savePlayerInventory(Player player) {
        UUID uuid = GameProfileUtils.getPlayerUuid(player);
        String worldGroup = getPlayerWorldGroup(player);
        if (worldGroup == null) {
            DebugManager.getInstance().debug(player, "You are not in a world group! Not saving...");
            return;
        }

        boolean saveLocations = Main.plugin.getConfig().getBoolean("worldgroups." + worldGroup + ".saveplayerlocations");

        StoredInventory storedInventory = new StoredInventory(player.getInventory().getContents(), player.getEnderChest().getContents(), player.getHealth(), player.getFoodLevel(), player.getExp(), player.getLevel(), player.getGameMode(), saveLocations ? player.getLocation() : null);
        cacheInventory(uuid, worldGroup, storedInventory);
        savePlayerInventoryToFile(player, storedInventory, worldGroup);
        String firstSlot = player.getInventory().getItem(0) == null ? "null" : player.getInventory().getItem(0).getType() == null ? "null" : player.getInventory().getItem(0).getType().name();
        DebugManager.getInstance().debug(player, "Saved your inventory! Your world: " + player.getWorld().getName() + ". First slot: " + firstSlot);
    }

    public void loadPlayerInventory(Player player) {
        UUID uuid = GameProfileUtils.getPlayerUuid(player);
        String worldGroup = getPlayerWorldGroup(player);
        if (worldGroup == null) {
            DebugManager.getInstance().debug(player, "You are not in a world group! Not loading...");
            return;
        }

        if (getCacheInventory(uuid, worldGroup) == null) {
            StoredInventory storedInventory = StoredInventory.getDefaultInventory();
            try {
                storedInventory = loadPlayerInventoryFromFile(player, worldGroup);
            } catch (Exception e) {
                e.printStackTrace();
                if (player.getUniqueId() == uuid) {
                    // it's a default profile so keep what they already had
                    DebugManager.getInstance().debug(player, "No inventory found for you, using whatever inventory you currently have! Reason: " + e.getMessage());
                    return;
                }
                // Just use default
                DebugManager.getInstance().debug(player, "No inventory found for you, resetting your inventory! Reason: " + e.getMessage());
            }
            cacheInventory(uuid, worldGroup, storedInventory);
        }

        StoredInventory storedInventory = getCacheInventory(uuid, worldGroup);
        if (storedInventory != null) {
            player.getInventory().setContents(storedInventory.getInventoryContents());
            String firstSlot = player.getInventory().getItem(0) == null ? "null" : storedInventory.getInventoryContents()[0].getType() == null ? "null" : storedInventory.getInventoryContents()[0].getType().name();
            DebugManager.getInstance().debug(player, "Loaded your inventory! Your world: " + player.getWorld().getName() + ". First slot: " + firstSlot);
            if (storedInventory.getEnderChestContents() != null) {
                player.getEnderChest().setContents(storedInventory.getEnderChestContents());
            }
            player.setHealth(storedInventory.getHealth());
            player.setFoodLevel(storedInventory.getFoodLevel());
            player.setExp(storedInventory.getExp());
            player.setLevel(storedInventory.getLevel());
            player.setGameMode(storedInventory.getGameMode());
            if (storedInventory.getLocation() != null && Objects.requireNonNull(storedInventory.getLocation().getWorld()).getUID() == player.getWorld().getUID()) {
                player.teleport(storedInventory.getLocation());
                DebugManager.getInstance().debug(player, "You have been teleported to your previous location!");
            } else {
                DebugManager.getInstance().debug(player, "Not teleporting you to your previous location because it is not in the same world!");
            }
        } else {
            Bukkit.getLogger().info("Could not load player inventory for " + player.getName());
            DebugManager.getInstance().debug(player, "Could not load your inventory!");
        }
    }
}

class StoredInventory {
    private final ItemStack[] inventoryContents;
    private final ItemStack[] enderChestContents;
    private final double health;
    private final int foodLevel;
    private final float exp;
    private final int level;
    private final GameMode gameMode;
    private final Location location;

    public StoredInventory(ItemStack[] inventoryContents, ItemStack[] enderChest, double health, int foodLevel, float exp, int level, GameMode gameMode, Location location) {
        this.inventoryContents = inventoryContents;
        this.enderChestContents = enderChest;
        this.health = health;
        this.foodLevel = foodLevel;
        this.exp = exp;
        this.level = level;
        this.gameMode = gameMode;
        this.location = location;
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

    public GameMode getGameMode() {
        return gameMode;
    }

    public Location getLocation() {
        return location;
    }

    public static StoredInventory getDefaultInventory() {
        return new StoredInventory(new ItemStack[36], new ItemStack[27], 20, 20, 0, 0, GameMode.SURVIVAL, null);
    }
}