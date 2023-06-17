package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.DebugManager;
import net.serble.serblenetworkplugin.Functions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SerbleDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.debug")) {
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can do this");
            return true;
        }

        boolean value = !DebugManager.getInstance().isDebugging((Player) sender);
        DebugManager.getInstance().setIsDebugging((Player) sender, value);
        sender.sendMessage(Functions.translate("&aDebug mode is now " + (value ? "&aenabled" : "&cdisabled")));
        return true;
    }

}
