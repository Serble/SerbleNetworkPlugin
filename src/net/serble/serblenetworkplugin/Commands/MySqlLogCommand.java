package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MySqlLogCommand implements CommandExecutor {
    public static boolean enabled = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.mysqllog")) {
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return true;
        }
        enabled = !enabled;
        sender.sendMessage(Functions.translate("&aMySQL logging is now " + (enabled ? "enabled" : "disabled")));
        return true;
    }

}
