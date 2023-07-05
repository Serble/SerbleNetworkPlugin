package net.serble.serblenetworkplugin.API;

import org.bukkit.entity.Player;

public interface DebugService {

    void debug(Player p, String message);
    void debug(Player p, String message, boolean proxyFunctionCall);
    void serverDebug(String message);
    boolean isDebugging(Player p);
    void setIsDebugging(Player p, boolean debug);
}
