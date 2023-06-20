package net.serble.serblenetworkplugin;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

// There are 1000 points in a level

public class ExperienceManager {
    private static final HashMap<UUID, Integer> xpCache = new HashMap<>();

    public static int getXp(Player p) {
        return Main.sqlData.getXp(GameProfileUtils.getPlayerUuid(p));
    }

    public static int getLevel(int xp) {
        return xp / 1000;
    }

    public static int getLevel(Player player) {
        return getLevel(getXp(player));
    }

    public static float[] convertCustomExperienceToMinecraft(int customExperience) {
        int level = customExperience / 1000;
        int remainingExp = customExperience - (level * 1000);

        return new float[]{level, (float) remainingExp / 1000};
    }

    public static void setPlayerExperience(Player player, int customExperience) {
        float[] minecraftExperience = convertCustomExperienceToMinecraft(customExperience);
        player.setLevel(((int) minecraftExperience[0]));
        player.setExp(minecraftExperience[1]);
    }

    public static void setPlayerExperience(Player player) {
        setPlayerExperience(player, getXp(player));
    }

    public static void setSerbleXp(UUID player, int xp) {
        if (xpCache.containsKey(player)) {
            xpCache.replace(player, xp);
        } else {
            xpCache.put(player, xp);
        }
        Main.sqlData.setXp(player, xp);
    }

    public static void addSerbleXp(UUID player, int xp) {
        if (xpCache.containsKey(player)) {
            xpCache.replace(player, xpCache.get(player) + xp);
        } else {
            xpCache.put(player, xp);
        }
        Main.sqlData.addXp(player, xp);
    }

    public static int getSerbleXp(UUID player) {
        if (xpCache.containsKey(player)) {
            return xpCache.get(player);
        } else {
            int xp = Main.sqlData.getXp(player);
            xpCache.put(player, xp);
            return xp;
        }
    }

    public static void invalidateCacheForUser(UUID player) {
        xpCache.remove(player);
    }

}
