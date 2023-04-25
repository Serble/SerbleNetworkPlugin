package net.serble.serblenetworkplugin;

import org.bukkit.entity.Player;

import java.util.UUID;

// There are 1000 points in a level

public class ExperienceManager {

    public static int getXp(Player p) {
        return Main.sqlData.getXp(p.getUniqueId());
    }

    public static int getLevel(int xp) {
        return xp / 1000;
    }

    public static int getLevel(UUID player) {
        return getLevel(Main.sqlData.getXp(player));
    }

    public static int getLevel(Player player) {
        return getLevel(getXp(player));
    }

    public static float getVanillaXpAmount(int xp) {
        double customLevels = (double) xp / 1000;
        int wholeCustomLevels = (int) customLevels;
        double partialCustomLevels = customLevels - wholeCustomLevels;

        double minecraftExperience = 0;

        for (int i = 1; i <= wholeCustomLevels; i++) {
            if (i <= 16) {
                minecraftExperience += 17;
            } else if (i <= 31) {
                minecraftExperience += 3 * i - 31;
            } else {
                minecraftExperience += 7 * i - 155;
            }
        }

        if (partialCustomLevels > 0) {
            int nextLevel = wholeCustomLevels + 1;
            double nextLevelExperience;
            if (nextLevel <= 16) {
                nextLevelExperience = 17;
            } else if (nextLevel <= 31) {
                nextLevelExperience = 3 * nextLevel - 31;
            } else {
                nextLevelExperience = 7 * nextLevel - 155;
            }

            minecraftExperience += partialCustomLevels * nextLevelExperience;
        }

        return (float) minecraftExperience;
    }

    public static float[] convertCustomExperienceToMinecraft(int customExperience) {
        int level = customExperience / 1000;
        int remainingExp = customExperience - (level * 1000);

        return new float[]{level, (float) remainingExp / 1000};
    }

    public static float[] convertCustomExperienceToMinecraft(Player p) {
        return convertCustomExperienceToMinecraft(getXp(p));
    }

    public static int getExperienceForLevel(int level) {
        if (level <= 16) {
            return 17;
        } else if (level <= 31) {
            return 3 * level - 31;
        } else {
            return 7 * level - 155;
        }
    }

    public static void setPlayerExperience(Player player, int customExperience) {
        float[] minecraftExperience = convertCustomExperienceToMinecraft(customExperience);
        player.setLevel(((int) minecraftExperience[0]));
        player.setExp(minecraftExperience[1]);
    }

    public static void setPlayerExperience(Player player) {
        setPlayerExperience(player, getXp(player));
    }

}
