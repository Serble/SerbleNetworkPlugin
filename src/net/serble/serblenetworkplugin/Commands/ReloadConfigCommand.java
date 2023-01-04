package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.ConfigManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.admin")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Sorry but we need a player to request the config!");
            return true;
        }

        Player p = (Player) sender;

        ConfigManager.RequestConfig(p);
        Main.plugin.reloadConfig();

        p.sendMessage("Config rerequested from bungee!");
        p.sendMessage("Local config reloaded!");

        return true;
    }

}
