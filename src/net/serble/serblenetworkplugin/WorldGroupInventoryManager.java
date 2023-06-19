package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Schemas.StoredInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    // Returns the players spawnpoint as set in the cache or null if it is null or the player is not in the cache
    public Location getPlayerCurrentSpawnPoint(UUID player, String worldGroup) {
        StoredInventory storedInventory = getCacheInventory(player, worldGroup);
        if (storedInventory == null) {
            return null;
        }
        return storedInventory.getSpawnLocation();
    }

    // Sets the players spawnpoint in the cache (it will be saved to memory if the player leaves the world or quits)
    public void setPlayerCurrentSpawnPoint(UUID player, String worldGroup, Location location) {
        StoredInventory storedInventory = getCacheInventory(player, worldGroup);
        if (storedInventory == null) {
            loadPlayerInventory(Bukkit.getPlayer(GameProfileUtils.getPlayerFromProfile(player)));
            storedInventory = getCacheInventory(player, worldGroup);
            if (storedInventory == null) {
                DebugManager.getInstance().debug(Bukkit.getPlayer(GameProfileUtils.getPlayerFromProfile(player)), "&cFailed to set spawnpoint in cache, cache refusing to load user.");
                return;
            }
        }
        storedInventory.setSpawnLocation(location);
        cacheInventory(player, worldGroup, storedInventory);
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

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String worldGroup = getPlayerWorldGroup(player);
        if (worldGroup == null) {
            return;
        }
        Location spawnLocation = getPlayerCurrentSpawnPoint(GameProfileUtils.getPlayerUuid(player.getUniqueId()), worldGroup);
        if (spawnLocation == null) {
            return;
        }
        if (spawnLocation.getWorld().getUID() != player.getWorld().getUID()) {
            DebugManager.getInstance().debug(player, "&6Your respawn location was in a different world, so it was not set.");
            return;
        }
        event.setRespawnLocation(spawnLocation);
        DebugManager.getInstance().debug(player, "Your respawn location has been set to your last spawnpoint in this world group. Location: " + spawnLocation);
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
        playerDataConfig.set("potionEffects", storedInventory.getPotionEffects());

        if (storedInventory.getLocation() != null) {
            playerDataConfig.set("location.world", Objects.requireNonNull(storedInventory.getLocation().getWorld()).getName());
            playerDataConfig.set("location.x", storedInventory.getLocation().getX());
            playerDataConfig.set("location.y", storedInventory.getLocation().getY());
            playerDataConfig.set("location.z", storedInventory.getLocation().getZ());
            playerDataConfig.set("location.yaw", storedInventory.getLocation().getYaw());
            playerDataConfig.set("location.pitch", storedInventory.getLocation().getPitch());
        }

        if (storedInventory.getSpawnLocation() != null) {
            playerDataConfig.set("spawnpoint.world", Objects.requireNonNull(storedInventory.getSpawnLocation().getWorld()).getName());
            playerDataConfig.set("spawnpoint.x", storedInventory.getSpawnLocation().getX());
            playerDataConfig.set("spawnpoint.y", storedInventory.getSpawnLocation().getY());
            playerDataConfig.set("spawnpoint.z", storedInventory.getSpawnLocation().getZ());
        }

        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
            DebugManager.getInstance().debug(player, "&cFailed to save your inventory to a file! Reason: " + e.getMessage());
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

        List<?> potionEffectsList = playerDataConfig.getList("potionEffects");
        List<PotionEffect> potionEffects = new ArrayList<>();
        if (potionEffectsList != null) {
            for (Object potionEffect : Objects.requireNonNull(playerDataConfig.getList("potionEffects"))) {
                potionEffects.add((PotionEffect) potionEffect);
            }
        }

        // spawnpoint
        Location spawnpoint = null;
        if (playerDataConfig.contains("spawnpoint")) {
            world = Bukkit.getWorld(Objects.requireNonNull(playerDataConfig.getString("spawnpoint.world")));
            x = playerDataConfig.getDouble("spawnpoint.x");
            y = playerDataConfig.getDouble("spawnpoint.y");
            z = playerDataConfig.getDouble("spawnpoint.z");
            spawnpoint = new Location(world, x, y, z);
        }

        return new StoredInventory(inventory, enderChest, health, foodLevel, exp, level, gameMode, location, potionEffects, spawnpoint);
    }

    public void savePlayerInventory(Player player) {
        UUID uuid = GameProfileUtils.getPlayerUuid(player);
        String worldGroup = getPlayerWorldGroup(player);
        if (worldGroup == null) {
            DebugManager.getInstance().debug(player, "You are not in a world group! Not saving...");
            return;
        }

        boolean saveLocations = Main.plugin.getConfig().getBoolean("worldgroups." + worldGroup + ".saveplayerlocations");

        StoredInventory cachedInventory = getCacheInventory(uuid, worldGroup);
        StoredInventory storedInventory = new StoredInventory(
                player.getInventory().getContents(),
                player.getEnderChest().getContents(),
                player.getHealth(), player.getFoodLevel(),
                player.getExp(), player.getLevel(),
                player.getGameMode(),
                saveLocations ? player.getLocation() : null,
                player.getActivePotionEffects(),
                cachedInventory == null ? null : cachedInventory.getSpawnLocation());
        cacheInventory(uuid, worldGroup, storedInventory);
        savePlayerInventoryToFile(player, storedInventory, worldGroup);
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
                    DebugManager.getInstance().debug(player, "&rNo inventory found for you, using whatever inventory you currently have! Reason: " + e.getMessage());
                    return;
                }
                // Just use default
                DebugManager.getInstance().debug(player, "&rNo inventory found for you, resetting your inventory! Reason: " + e.getMessage());
            }
            cacheInventory(uuid, worldGroup, storedInventory);
        }

        StoredInventory storedInventory = getCacheInventory(uuid, worldGroup);
        if (storedInventory != null) {
            player.getInventory().setContents(storedInventory.getInventoryContents());
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
            player.getActivePotionEffects().clear();
            player.getActivePotionEffects().addAll(storedInventory.getPotionEffects());
        } else {
            Bukkit.getLogger().info("Could not load player inventory for " + player.getName());
            DebugManager.getInstance().debug(player, "&cCould not load your inventory!");
        }
    }
}