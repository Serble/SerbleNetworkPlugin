package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.MenuItemManager;
import org.bukkit.Bukkit;
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

        if (Main.sqlData.getAdminMode(p.getUniqueId())) {
            Main.sqlData.setAdminMode(p.getUniqueId(), false);
            MenuItemManager.GiveMenuItems(p);
            sender.sendMessage(Functions.translate("&bAdmin Mode has been &4&lDisabled"));
        } else {
            Main.sqlData.setAdminMode(p.getUniqueId(), true);
            p.getInventory().clear();
            sender.sendMessage(Functions.translate("&bAdmin Mode has been &2&lEnabled"));
        }
        Bukkit.dispatchCommand(p, "build");
        return false;
    }

}
