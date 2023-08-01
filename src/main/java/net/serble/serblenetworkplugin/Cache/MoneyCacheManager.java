package net.serble.serblenetworkplugin.Cache;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;

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

    public static void setMoney(UUID player, int money, boolean async) {
        if (moneyCache.containsKey(player)) {
            moneyCache.replace(player, money);
        } else {
            moneyCache.put(player, money);
        }
        Functions.runAsync(() -> Main.sqlData.setMoney(player, money), async);
    }

    public static void setMoney(UUID player, int money) {
        setMoney(player, money, true);
    }

    public static void addMoney(UUID player, int money, boolean async) {
        moneyCache.put(player, getMoney(player) + money);
        Functions.runAsync(() -> Main.sqlData.addMoney(player, money), true);
    }

    public static void addMoney(UUID player, int money) {
        addMoney(player, money, true);
    }

    public static void invalidateCacheForPlayer(UUID player) {
        moneyCache.remove(player);
    }
}
