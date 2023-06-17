package net.serble.serblenetworkplugin.API;

import org.bukkit.entity.Player;

public interface DebugService {

    void debug(Player p, String message);
    boolean isDebugging(Player p);
    void setIsDebugging(Player p, boolean debug);
}
