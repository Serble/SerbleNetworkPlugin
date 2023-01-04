package net.serble.serblenetworkplugin;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scoreboard {

    private static final Map<UUID, Integer> TASKS = new HashMap<UUID, Integer>();
    private final UUID uuid;

    public Scoreboard(UUID uuid) {
        this.uuid = uuid;
    }

    public void setIDlb(int id) {
        TASKS.put(uuid, id);
    }

    public int getIDlb() {
        return TASKS.get(uuid);
    }

    public boolean hasIDlb() {
        return TASKS.containsKey(uuid);
    }

    public void stoplb() {
        Bukkit.getScheduler().cancelTask(TASKS.get(uuid));
        TASKS.remove(uuid);
    }

}
