package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.Cache.MoneyCacheManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.Objects;


public class ScoreboardManager implements Listener {
    private int taskID;

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
            createScoreboard(e.getPlayer());
            startScoreboard(e.getPlayer());
        }, 20);
    }

    public void startScoreboard(Player player) {
        if (Main.plugin.getConfig().getBoolean("disablescoreboard")) return;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

            int count = 0;
            final Scoreboard lb = new Scoreboard(player.getUniqueId());
            boolean hasDeletedSSB = false;

            @Override
            public void run() {
                if (!lb.hasIDlb())
                    lb.setIDlb(taskID);
                if (count == 2)
                    count = 0;
                switch(count) {
                    case 0:
                        break;
                    case 1:
                        if (!Main.plugin.getConfig().getStringList("worlds").contains(player.getWorld().getName()) ||
                                Main.plugin.getConfig().getStringList("noscoreboardlobbys").contains(player.getWorld().getName())) {
                            if (!hasDeletedSSB) {
                                hasDeletedSSB = true;
                                if (Main.plugin.getConfig().getStringList("dontclearscoreboardworlds").contains(player.getWorld().getName())) {
                                    return;
                                }
                                player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
                            }
                            return;
                        }
                        createScoreboard(player);
                        hasDeletedSSB = false;
                        break;
                }
                count++;
            }

        }, 0, 20);
    }

    public void createScoreboard(Player player) {
        // scoreboard
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        org.bukkit.scoreboard.Scoreboard main = manager.getNewScoreboard();

        // title
        String worldName = "ERROR";
        for (int i = 0; i < Main.plugin.getConfig().getStringList("worlds").size(); i++) {
            if (Main.plugin.getConfig().getStringList("worlds").get(i).equals(player.getWorld().getName())) {
                worldName = Main.plugin.getConfig().getStringList("worldnames").get(i);
                break;
            }
        }
        String text = String.format("<<< Serble - %s >>>", worldName);
        Objective obj = main.registerNewObjective("lobbyboard", "dummy", ChatColor.BLUE + text);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // line
        Score line = obj.getScore(ChatColor.BLACK + "=======================");
        line.setScore(9);

        // help
        Score help = obj.getScore(ChatColor.BLUE + "Type /menu");
        help.setScore(8);

        // online players
        Score onlineplayer = obj.getScore(ChatColor.YELLOW + "Players Online: " + Bukkit.getOnlinePlayers().size());
        onlineplayer.setScore(7);

        // money
        Score money = obj.getScore(ChatColor.YELLOW + "Money: " + MoneyCacheManager.getMoney(GameProfileUtils.getPlayerUuid(player)));
        money.setScore(6);

        // exp
        int level = ExperienceManager.getLevel(player);
        int progress = ExperienceManager.getXp(player) - level * 1000;
        Score exp = obj.getScore(ChatColor.YELLOW + "Level: " + level + " (" + progress + "/1000)");
        exp.setScore(5);

        // tps
        Score tps = obj.getScore(ChatColor.YELLOW + "TPS: " + Math.round(TpsTracker.getTPS()));
        tps.setScore(4);

        // discord
        Score discord = obj.getScore(ChatColor.YELLOW + "Discord: https://discord.serble.net.");
        discord.setScore(3);

        // line 2
        Score line2 = obj.getScore(ChatColor.BLACK + "========================");
        line2.setScore(2);

        // ip
        Score ip = obj.getScore(ChatColor.BLUE + "mc.serble.net");
        ip.setScore(1);

        // done
        player.setScoreboard(main);

    }

}
