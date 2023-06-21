package net.serble.serblenetworkplugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class AchievementsManager {

    // Progress before the achievement is completed
    public static HashMap<Achievement, Integer> AchievementProgressLimits = new HashMap<>() {{
        put(Achievement.OPEN_MENU, 1);
        put(Achievement.GO_SHOPPING, 1);
        put(Achievement.BEDWARS_WIN, 1);
        put(Achievement.BEDWARS_PLAYER, 10);
        put(Achievement.PARKOUR_PLAYER, 1);
        put(Achievement.PARKOUR_PRO, 3);
        put(Achievement.FORTNITE, 14);
        put(Achievement.LOOKING_FANCY, 1);
        put(Achievement.HELLO_OWNER, 1);
        put(Achievement.HELLO_CO_OWNER, 1);
        put(Achievement.I_HAVE_A_VOICE, 1);
        put(Achievement.IMPROVEMENT, 1);
        put(Achievement.NUMBER1, 1);
        put(Achievement.BED_REMOVAL_SERVICE, 50);
        put(Achievement.TOUCH_GRASS, 1);
    }};

    // Achievement Proper Names
    public static HashMap<Achievement, String> AchievementNames = new HashMap<>() {{
        put(Achievement.OPEN_MENU, "Game Time");
        put(Achievement.GO_SHOPPING, "Going Shopping");
        put(Achievement.BEDWARS_WIN, "BedWars Winner");
        put(Achievement.BEDWARS_PLAYER, "BedWars Player");
        put(Achievement.PARKOUR_PLAYER, "Parkour Player");
        put(Achievement.PARKOUR_PRO, "Parkour Pro");
        put(Achievement.FORTNITE, "Fortnite");
        put(Achievement.LOOKING_FANCY, "Looking Fancy");
        put(Achievement.HELLO_OWNER, "Hello CoPokBl");
        put(Achievement.HELLO_CO_OWNER, "Hello Calcilore");
        put(Achievement.I_HAVE_A_VOICE, "I Have A Voice!");
        put(Achievement.IMPROVEMENT, "Improvement");
        put(Achievement.NUMBER1, "#1");
        put(Achievement.BED_REMOVAL_SERVICE, "Bed Removal Service");
        put(Achievement.TOUCH_GRASS, "Touch Grass");
    }};

    // Achievement Descriptions
    public static HashMap<Achievement, String> AchievementDescriptions = new HashMap<>() {{
        put(Achievement.OPEN_MENU, "Open The Game Menu");
        put(Achievement.GO_SHOPPING, "Open The Shop");
        put(Achievement.BEDWARS_WIN, "Win a game of BedWars");
        put(Achievement.BEDWARS_PLAYER, "Player 10 games on BedWars");
        put(Achievement.PARKOUR_PLAYER, "Beat a parkour map");
        put(Achievement.PARKOUR_PRO, "Beat 3 parkour maps");
        put(Achievement.FORTNITE, "Claim you daily reward 14 times");
        put(Achievement.LOOKING_FANCY, "Open the cosmetics menu with /cosmetic");
        put(Achievement.HELLO_OWNER, "Right click the CoPokBl NPC in the lobby");
        put(Achievement.HELLO_CO_OWNER, "Right click the Calcilore NPC in the lobby");
        put(Achievement.I_HAVE_A_VOICE, "Speak in chat");
        put(Achievement.IMPROVEMENT, "Beat a parkour personal best");
        put(Achievement.NUMBER1, "Beat a global parkour record");
        put(Achievement.BED_REMOVAL_SERVICE, "Break 50 beds in BedWars");
        put(Achievement.TOUCH_GRASS, "Break some grass");
    }};


    // <Achievement, List with [coins, exp]>
    public static HashMap<Achievement, ArrayList<String>> AchievementRewards = new HashMap<>() {{
        put(Achievement.OPEN_MENU, new ArrayList<>() {{
            add("sysgivemoney {player} 20 Achievement");
            add("sysgivexp {player} 40 Achievement");
        }});
        put(Achievement.GO_SHOPPING, new ArrayList<>() {{
            add("sysgivemoney {player} 20 Achievement");
            add("sysgivexp {player} 40 Achievement");
        }});
        put(Achievement.BEDWARS_WIN, new ArrayList<>() {{
            add("sysgivemoney {player} 50 Achievement");
            add("sysgivexp {player} 100 Achievement");
        }});
        put(Achievement.BEDWARS_PLAYER, new ArrayList<>() {{
            add("sysgivemoney {player} 100 Achievement");
            add("sysgivexp {player} 200 Achievement");
        }});
        put(Achievement.PARKOUR_PLAYER, new ArrayList<>() {{
            add("sysgivemoney {player} 100 Achievement");
            add("sysgivexp {player} 200 Achievement");
        }});
        put(Achievement.PARKOUR_PRO, new ArrayList<>() {{
            add("sysgivemoney {player} 200 Achievement");
            add("sysgivexp {player} 500 Achievement");
        }});
        put(Achievement.FORTNITE, new ArrayList<>() {{
            add("sysgivemoney {player} 100 Achievement");
            add("sysgivexp {player} 250 Achievement");
        }});
        put(Achievement.LOOKING_FANCY, new ArrayList<>() {{
            add("sysgivemoney {player} 20 Achievement");
            add("sysgivexp {player} 40 Achievement");
        }});
        put(Achievement.HELLO_OWNER, new ArrayList<>() {{
            add("sysgivemoney {player} 200 Achievement");
            add("sysgivexp {player} 750 Achievement");
            add("msg {player} Hello There");
        }});
        put(Achievement.HELLO_CO_OWNER, new ArrayList<>() {{
            add("sysgivemoney {player} 200 Achievement");
            add("sysgivexp {player} 750 Achievement");
            add("msg {player} Hello There");
        }});
        put(Achievement.I_HAVE_A_VOICE, new ArrayList<>() {{
            add("sysgivemoney {player} 20 Achievement");
            add("sysgivexp {player} 40 Achievement");
        }});
        put(Achievement.IMPROVEMENT, new ArrayList<>() {{
            add("sysgivemoney {player} 40 Achievement");
            add("sysgivexp {player} 80 Achievement");
        }});
        put(Achievement.NUMBER1, new ArrayList<>() {{
            add("sysgivemoney {player} 200 Achievement");
            add("sysgivexp {player} 400 Achievement");
        }});
        put(Achievement.BED_REMOVAL_SERVICE, new ArrayList<>() {{
            add("sysgivemoney {player} 100 Achievement");
            add("sysgivexp {player} 200 Achievement");
        }});
        put(Achievement.TOUCH_GRASS, new ArrayList<>() {{
            add("sysgivemoney {player} 40 Achievement");
            add("sysgivexp {player} 80 Achievement");
        }});
    }};

    public static String generateMySqlFieldString() {
        StringBuilder str = new StringBuilder();
        Achievement[] values = Achievement.values();
        for (Achievement a : values) {
            str.append(a.toString()).append(" INT NOT NULL DEFAULT 0, ");
        }
        return str.toString();
    }

    public static void fixMySqlColumns() {
        PreparedStatement ps;
        Achievement[] values = Achievement.values();
        ResultSet rs;
        for (Achievement a : values) {
            try {
                String columnName = a.toString();

                // Check if the column exists
                ps = Main.plugin.SQL.getConnection().prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = ? AND table_name = 'serble_achievements' AND column_name = ?");
                ps.setString(1, "serblenetworkplugin");
                ps.setString(2, columnName);
                rs = ps.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt(1);

                    // If the column does not exist, add it
                    if (count == 0) {
                        ps = Main.plugin.SQL.getConnection().prepareStatement("ALTER TABLE serble_achievements ADD COLUMN " + columnName + " INT;");
                        ps.executeUpdate();
                    }
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe(e.toString());
            }
        }
    }

    private static void CompletedAchievement(Player p, Achievement a) {
        TextComponent achievementDescription = new TextComponent(AchievementDescriptions.get(a));
        achievementDescription.setColor(ChatColor.GOLD.asBungee());

        TextComponent achievementName = new TextComponent(AchievementNames.get(a));
        achievementName.setColor(ChatColor.GREEN.asBungee());
        achievementName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{achievementDescription}));

        TextComponent message = new TextComponent("Achievement Complete: ");
        message.setColor(ChatColor.BLUE.asBungee());
        message.addExtra(achievementName);

        p.spigot().sendMessage(ChatMessageType.CHAT, message);

        ArrayList<String> rewards = AchievementRewards.get(a);
        for (String reward : rewards) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("{player}", p.getName()));
        }
    }

    public static void GrantAchievementProgress(Player p, Achievement achievement, int progress) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {  // Enter async context because we are accessing the database
            int achievementLimit = AchievementProgressLimits.get(achievement);
            int currentProgress = Main.sqlData.getAchievement(GameProfileUtils.getPlayerUuid(p), achievement);
            if (currentProgress == achievementLimit) {
                return;  // The achievement is already complete
            }
            if (currentProgress + progress >= achievementLimit) {
                // They completed it
                Bukkit.getScheduler().runTask(Main.plugin, () -> {
                    CompletedAchievement(p, achievement);  // This needs to be run sync because it runs commands
                });
                Main.sqlData.setAchievement(GameProfileUtils.getPlayerUuid(p), achievement, achievementLimit);
                return;
            }
            Main.sqlData.setAchievement(GameProfileUtils.getPlayerUuid(p), achievement, currentProgress + progress);
        });
    }

    public static void GrantAchievementProgress(Player p, Achievement achievement) {
        GrantAchievementProgress(p, achievement, 1);
    }

}