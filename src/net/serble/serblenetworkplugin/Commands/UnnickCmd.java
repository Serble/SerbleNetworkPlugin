package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnnickCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("serble.ranknick.self") && !sender.hasPermission("serble.ranknick.others")
        && !sender.hasPermission("serble.nick.self") && !sender.hasPermission("serble.nick.others")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }

        if (args.length == 1) {
            if (!sender.hasPermission("serble.ranknick.others") && !sender.hasPermission("serble.nick.others")) {
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

            if (sender.hasPermission("serble.nick.others")) {
                target.setDisplayName(null);
                Main.sqlData.setNick(target.getUniqueId(), null);
                sender.sendMessage(Functions.translate("&aUser's nickname has been reset!"));
            }

            if (sender.hasPermission("serble.ranknick.others")) {
                Main.sqlData.setRankNick(target.getUniqueId(), null);
                sender.sendMessage(Functions.translate("&aUser's rank disguise has been reset!"));
            }

            return true;

        } else {

            if (!(sender instanceof Player)) {
                sender.sendMessage("You cannot do that!");
                return true;
            }

            Player p = (Player) sender;

            if (!sender.hasPermission("serble.ranknick.self") && !sender.hasPermission("serble.nick.self")) {
                sender.sendMessage(Functions.translate("&4You do not have permission!"));
                return true;
            }

            if (sender.hasPermission("serble.nick.self")) {
                p.setDisplayName(null);
                Main.sqlData.setNick(p.getUniqueId(), null);
                sender.sendMessage(Functions.translate("&aYour nickname has been reset!"));
            }

            if (sender.hasPermission("serble.ranknick.self")) {
                Main.sqlData.setRankNick(p.getUniqueId(), null);
                sender.sendMessage(Functions.translate("&aYour rank disguise has been reset!"));
            }

            return true;
        }

    }
}
