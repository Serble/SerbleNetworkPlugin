package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.AdminModeCacheHandler;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.MenuItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminMode implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You cannot do this!");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("serble.adminmode")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        // We are going to save the player's inventory before we clear it
        // Inventories should never be saved while in admin mode
        Main.worldGroupInventoryManager.savePlayerInventory(p);

        if (AdminModeCacheHandler.isAdminMode(p.getUniqueId())) {
            AdminModeCacheHandler.setAdminMode(p.getUniqueId(), false);
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            if (!MenuItemManager.shouldNotGetItems(p)) {
                MenuItemManager.GiveMenuItems(p);  // Give lobby items to player if in the lobby
            }
            sender.sendMessage(Functions.translate("&bAdmin Mode has been &4&lDisabled"));
        } else {
            Main.worldGroupInventoryManager.savePlayerInventory(p);
            AdminModeCacheHandler.setAdminMode(p.getUniqueId(), true);
            p.getInventory().clear();
            sender.sendMessage(Functions.translate("&bAdmin Mode has been &2&lEnabled"));
        }
        return false;
    }

}
