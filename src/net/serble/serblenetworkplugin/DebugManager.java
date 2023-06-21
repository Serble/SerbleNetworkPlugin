package net.serble.serblenetworkplugin;

import net.serble.serblenetworkplugin.API.DebugService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DebugManager implements DebugService {

    private static DebugManager instance;
    private final HashMap<UUID, Boolean> debugModeCache = new HashMap<>();

    public static DebugManager getInstance() {
        if (instance == null) {
            instance = new DebugManager();
        }
        return instance;
    }

    public void debug(Player p, String message, boolean proxyFunctionCall) {
        if (!isDebugging(p)) {
            return;
        }

        // set the string loc to the stack trace of the calling function. For example: "SerbleDebugCommand.java:15"
        int stackTraceIndex = proxyFunctionCall ? 3 : 2;
        String loc = Thread.currentThread().getStackTrace()[stackTraceIndex].getFileName() + ":" + Thread.currentThread().getStackTrace()[stackTraceIndex].getLineNumber();

        p.sendMessage(Functions.translate("&7[DEBUG] [" + loc + "] " + message));
    }

    public void debug(Player p, String message) {
        debug(p, message, true);
    }

    public void serverDebug(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("serble.staff")) continue;
            if (!isDebugging(p)) continue;
            debug(p, message, true);
        }
    }

    public boolean isDebugging(Player p) {
        if (debugModeCache.containsKey(p.getUniqueId())) {
            return debugModeCache.get(p.getUniqueId());
        }
        boolean debug = Main.sqlData.getDebugToggle(p.getUniqueId());
        debugModeCache.put(p.getUniqueId(), debug);
        return debug;
    }

    public void setIsDebugging(Player p, boolean debug) {
        debugModeCache.put(p.getUniqueId(), debug);
        Functions.runAsync(() -> Main.sqlData.setDebugToggle(p.getUniqueId(), debug));
    }

}
