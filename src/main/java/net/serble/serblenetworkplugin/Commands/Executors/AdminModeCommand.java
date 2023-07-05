package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Cache.AdminModeCacheHandler;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.MenuItemManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.entity.Player;

public class AdminModeCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();

        // We are going to save the player's inventory before we clear it
        // Inventories should never be saved while in admin mode
        Main.worldGroupInventoryManager.savePlayerInventory(p);

        if (AdminModeCacheHandler.isAdminMode(p.getUniqueId())) {
            AdminModeCacheHandler.setAdminMode(p.getUniqueId(), false);
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            if (!MenuItemManager.shouldNotGetItems(p)) {
                MenuItemManager.GiveMenuItems(p);  // Give lobby items to player if in the lobby
            }
            p.sendMessage(Functions.translate("&bAdmin Mode has been &4&lDisabled"));
        } else {
            Main.worldGroupInventoryManager.savePlayerInventory(p);
            AdminModeCacheHandler.setAdminMode(p.getUniqueId(), true);
            p.getInventory().clear();
            p.sendMessage(Functions.translate("&bAdmin Mode has been &2&lEnabled"));
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return EMPTY_TAB_COMPLETE;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return getArrayOfSenders(CommandSenderType.Player);
    }
}
