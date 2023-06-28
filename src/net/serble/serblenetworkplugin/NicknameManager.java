package net.serble.serblenetworkplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.*;

public class NicknameManager implements Listener {

    private static final ArrayList<String> adjectives = new ArrayList<>() {{
        add("red");
        add("bouncy");
        add("blue");
        add("happy");
        add("swift");
        add("mighty");
        add("tiny");
        add("shiny");
        add("brave");
        add("lucky");
        add("fuzzy");
        add("squeaky");
        add("bright");
        add("chilly");
        add("calm");
        add("wacky");
        add("rainy");
        add("jumpy");
        add("daring");
        add("sly");
        add("majestic");
        add("crimson");
    }};

    private static final ArrayList<String> nouns = new ArrayList<>() {{
        add("apple");
        add("spider");
        add("balloon");
        add("kitten");
        add("dragon");
        add("sword");
        add("cupcake");
        add("pony");
        add("robot");
        add("unicorn");
        add("penguin");
        add("banana");
        add("star");
        add("pumpkin");
        add("owl");
        add("taco");
        add("ninja");
        add("giraffe");
        add("elephant");
        add("hamster");
        add("castle");
        add("spaceship");
    }};

    private static final ArrayList<String> skins = new ArrayList<>() {{
        add("steve");
        add("technoblade");
        add("skeppy");
        add("bedlessnoob");
        add("dream");
        add("georgenotfound");
        add("sapnap");
        add("badboyhalo");
        add("fundy");
        add("wilbursoot");
        add("philza");
        add("tommyinnit");
        add("tubbo");
        add("nihachu");
        add("jschlatt");
        add("antfrost");
        add("quackity");
        add("karljacobs");
        add("ponk");
        add("awesamdude");
        add("ranboo");
        add("purpled");
        add("foolishgamers");
        add("eret");
        add("calcilore");
        add("copokbl");
    }};

    private static final Random random = new Random();
    private static final HashMap<UUID, String> rankNicknames = new HashMap<>();

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
        Functions.runAsync(() -> {
            Main.sqlData.setNick(playerUuid, null);
            setRankNick(playerUuid, null);
            Main.sqlData.setNickSkin(playerUuid, null);
        });  // That's too many mysql queries to run on the main thread
        NickAPI.resetNick(p);
        NickAPI.refreshPlayer(p);
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        updateName(e.getPlayer());
    }

    public static void nick(Player p, String name, String rank, String skin) {
        UUID userId = GameProfileUtils.getPlayerUuid(p);
        Main.sqlData.setNick(userId, name);  // These need to finish before updateName is called or else updateName won't get the correct name
        if (!Objects.equals(rank, "")) {
            setRankNick(userId, rank);
        }
        if (!Objects.equals(skin, "")) {
            Main.sqlData.setNickSkin(userId, skin);
        }
        updateName(p);
    }

    public static void randomNick(Player p) {
        nick(p, generateName(), "default", randomSkin());
    }

    public static String generateName() {
        return adjectives.get(random.nextInt(adjectives.size())) + nouns.get(random.nextInt(nouns.size())) + random.nextInt(99);
    }

    public static String randomSkin() {
        return skins.get(random.nextInt(skins.size()));
    }

    public static String getRankNick(UUID uuid) {
        if (!rankNicknames.containsKey(uuid)) {
            rankNicknames.put(uuid, Main.sqlData.getRankNick(uuid));
        }
        return rankNicknames.get(uuid);
    }

    public static void setRankNick(UUID uuid, String rankNick) {
        rankNicknames.put(uuid, rankNick);
        Main.sqlData.setRankNick(uuid, rankNick);
    }

    public static void invalidateCache(UUID uuid) {
        rankNicknames.remove(uuid);
    }

}
