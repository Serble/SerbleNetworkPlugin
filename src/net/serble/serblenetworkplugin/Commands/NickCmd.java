package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("serble.nick.self") && !sender.hasPermission("serble.nick.others")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        if (args.length == 1) {

            if (!sender.hasPermission("serble.nick.self")) {
                sender.sendMessage(Functions.translate("&4You do not have permission!"));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("You cannot do that!");
                return true;
            }

            Player p = (Player) sender;

            String newName = args[0];

            p.setDisplayName(newName);
            Main.sqlData.setNick(p.getUniqueId(), newName);

            sender.sendMessage(Functions.translate("&aUser has been successfully nicked!"));
            return true;
        } else if (args.length == 2) {

            if (!sender.hasPermission("serble.nick.others")) {
                sender.sendMessage(Functions.translate("&4You do not have permission!"));
                return true;
            }
            Player target;
            try {
                target = Bukkit.getPlayer(args[0]);
            } catch (Exception e) {
                sender.sendMessage(Functions.translate("&4Invalid Player"));
                return true;
            }
            String newName = args[1];

            if (Bukkit.getPlayer(newName) != null && !sender.hasPermission("serble.nick.bypassfilter")) {
                sender.sendMessage(Functions.translate("&4You do not have permission to nick as an online player"));
                return true;
            }

            target.setDisplayName(newName);
            Main.sqlData.setNick(target.getUniqueId(), newName);

            sender.sendMessage(Functions.translate("&aUser has been successfully nicked!"));
            return true;

        } else {
            sender.sendMessage(Functions.translate("&4Usage: /nick <PLAYER> <NICKNAME>"));
            sender.sendMessage(Functions.translate("&4Usage: /nick <NICKNAME>"));
            return true;
        }

    }


}
