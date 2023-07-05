package net.serble.serblenetworkplugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.serble.serblenetworkplugin.Cache.AdminModeCacheHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getIdentifier() {
        return "serble";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "CoPokBl";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    @NotNull
    public String getName() {
        return "Serble PAPI Expansion";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String str) {
        switch (str) {
            case "rank": {
                return Functions.getPlayerRankDisplay(p);
            }
            case "coins": {
                return String.valueOf(Main.sqlData.getMoney(p.getUniqueId()));
            }
            case "level": {
                return String.valueOf(ExperienceManager.getLevel(p));
            }
            case "total_xp": {
                return String.valueOf(ExperienceManager.getXp(p));
            }
            case "chat_format": {
                return Chat.getFormat(p);
            }
            case "game_mode": {
                return PlayerGameModeLocator.getGameMode(p);
            }
            case "admin_mode": {
                return AdminModeCacheHandler.isAdminMode(p.getUniqueId()) ? "Enabled" : "Disabled";
            }
            case "admin_mode_color": {
                return AdminModeCacheHandler.isAdminMode(p.getUniqueId()) ? "&a" : "&c";
            }
        }

        return null;
    }

}
