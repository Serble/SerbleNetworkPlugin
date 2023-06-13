package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.API.GameProfileUtils;
import org.bukkit.entity.Player;

// There are 1000 points in a level

public class ExperienceManager {

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

}
