package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Schemas.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Functions {

    private static Random random = new Random();

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String getPlayerRankDisplayB(Player p, boolean ignoreNick) {

        if (!Main.hasConfig) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    ConfigManager.RequestConfig(p);
                }
            }.runTaskLater(Main.plugin, 80L);

            return "&2";
        }


        if (!ignoreNick) {
            String rankName = Main.sqlData.getRankNick(GameProfileUtils.getPlayerUuid(p));

            if (rankName != null) {
                for (int i = 0; i < Main.config.Ranks.size(); i++) {
                    Rank rank = Main.config.Ranks.get(i);
                    if (rank.Name.equalsIgnoreCase(rankName)) {
                        return rank.Display;
                    }
                }
            }
        }

        String rankDisplay = "&1[&2Error Getting Rank&1]&2";

        for (int i = 0; i < Main.config.Ranks.size(); i++) {
            Rank rank = Main.config.Ranks.get(i);
            if (p.hasPermission(rank.Permission)) {
                rankDisplay = rank.Display;
            }
        }

        return rankDisplay;
    }

    public static String getPlayerRankDisplay(Player p) {
        return getPlayerRankDisplayB(p, false);
    }

    public static Random getRandom() {
        return random;
    }

}
