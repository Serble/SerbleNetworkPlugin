package net.serble.serblenetworkplugin;

import java.util.HashMap;
import java.util.UUID;

public class MoneyCacheManager {
    private static final HashMap<UUID, Integer> moneyCache = new HashMap<>();

    public static int getMoney(UUID player) {
        if (moneyCache.containsKey(player)) {
            return moneyCache.get(player);
        }
        int money = Main.sqlData.getMoney(player);
        moneyCache.put(player, money);
        return money;
    }

    public static void setMoney(UUID player, int money) {
        if (moneyCache.containsKey(player)) {
            moneyCache.replace(player, money);
        } else {
            moneyCache.put(player, money);
        }
        Main.sqlData.setMoney(player, money);
    }

    public static void addMoney(UUID player, int money) {
        if (moneyCache.containsKey(player)) {
            moneyCache.replace(player, moneyCache.get(player) + money);
        } else {
            moneyCache.put(player, money);
        }
        Main.sqlData.addMoney(player, money);
    }

    public static void invalidateCacheForPlayer(UUID player) {
        moneyCache.remove(player);
    }
}
