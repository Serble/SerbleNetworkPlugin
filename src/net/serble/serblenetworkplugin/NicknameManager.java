package net.serble.serblenetworkplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class NicknameManager implements Listener {

    private static final ArrayList<String> adjectives = new ArrayList<>() {{
        add("red");
        add("bouncy");
    }};

    private static final ArrayList<String> nouns = new ArrayList<>() {{
        add("apple");
        add("spider");
    }};

    private static final ArrayList<String> skins = new ArrayList<>() {{
        add("steve");
        add("technoblade");
        add("skeppy");
        add("bedlessnoob");
    }};

    private static final Random random = new Random();

    public static void updateName(Player p) {
        UUID playerId = GameProfileUtils.getPlayerUuid(p);
        String nickname = Main.sqlData.getNick(playerId);
        if (Objects.equals(nickname, "") || nickname == null) {
            NickAPI.resetNick(p);
        } else {
            NickAPI.nick(p, nickname);
        }
        String rankSkin = Main.sqlData.getNickSkin(GameProfileUtils.getPlayerUuid(p));
        if (Objects.equals(nickname, "") || rankSkin == null) {
            NickAPI.resetSkin(p);
        } else {
            NickAPI.setSkin(p, rankSkin);
        }
        NickAPI.refreshPlayer(p);
    }

    public static void unNick(Player p) {
        UUID playerUuid = GameProfileUtils.getPlayerUuid(p);
        Main.sqlData.setNickSkin(playerUuid, null);
        Main.sqlData.setNick(playerUuid, null);
        Main.sqlData.setRankNick(playerUuid, null);
        NickAPI.resetNick(p);
        NickAPI.refreshPlayer(p);
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        updateName(e.getPlayer());
    }

    public static void nick(Player p, String name, String rank, String skin) {
        UUID userId = GameProfileUtils.getPlayerUuid(p);
        Main.sqlData.setNick(userId, name);
        if (!Objects.equals(rank, "")) {
            Main.sqlData.setRankNick(userId, rank);
        }
        if (!Objects.equals(skin, "")) {
            Main.sqlData.setNickSkin(userId, skin);
        }
        updateName(p);
    }

    public static void randomNick(Player p) {
        String name = generateName();
        nick(p, name, "default", randomSkin());
    }

    public static String generateName() {
        return adjectives.get(random.nextInt(adjectives.size())) + nouns.get(random.nextInt(nouns.size())) + random.nextInt(99);
    }

    public static String randomSkin() {
        return skins.get(random.nextInt(skins.size()));
    }

}
