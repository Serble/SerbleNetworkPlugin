package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.NicknameManager;
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
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return true;
        }

        if (args.length == 1) {
            if (!sender.hasPermission("serble.nick.others")) {
                sender.sendMessage(Functions.translate("&cYou do not have permission!"));
                return true;
            }

            Player target;
            try {
                target = Bukkit.getPlayer(args[0]);
            } catch (Exception e) {
                sender.sendMessage(Functions.translate("&cInvalid Player"));
                return true;
            }

            if (sender.hasPermission("serble.nick.others")) {
                assert target != null;
                NicknameManager.unNick(target);
                sender.sendMessage(Functions.translate("&aUser's disguise has been reset!"));
            }

        } else {

            if (!(sender instanceof Player)) {
                sender.sendMessage("You cannot do that!");
                return true;
            }

            Player p = (Player) sender;

            if (!sender.hasPermission("serble.nick.self")) {
                sender.sendMessage(Functions.translate("&cYou do not have permission!"));
                return true;
            }

            NicknameManager.unNick(p);
            sender.sendMessage(Functions.translate("&aYour disguise has been reset!"));
        }
        return true;

    }
}
