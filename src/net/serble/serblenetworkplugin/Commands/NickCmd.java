package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.NicknameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class NickCmd implements CommandExecutor {

    // /nick <PLAYER> <NAME> <RANK> <SKIN>

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.nick.self") && !sender.hasPermission("serble.nick.others")) {
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return true;
        }

        if (args.length > 0) {
            if (!sender.hasPermission("serble.nick.others")) {
                sender.sendMessage(Functions.translate("&cYou do not have permission!"));
                return true;
            }

            Player target;
            try {
                target = Objects.requireNonNull(Bukkit.getPlayer(args[0]));
            } catch (Exception e) {
                sender.sendMessage(Functions.translate("&cInvalid Player"));
                return true;
            }

            String name = NicknameManager.generateName();
            if (args.length > 1) {
                name = args[1];
                if (Bukkit.getPlayer(name) != null && !sender.hasPermission("serble.nick.bypassfilter")) {
                    sender.sendMessage(Functions.translate("&cYou do not have permission to nick as an online player"));
                    return true;
                }
            }

            String rank = "default";
            if (args.length > 2) {
                rank = args[2];
            }

            String skin = NicknameManager.randomSkin();
            if (args.length > 3) {
                skin = args[3];
            }

            NicknameManager.nick(target, name, rank, skin);

            sender.sendMessage(Functions.translate("&a" + target.getName() + " has been successfully nicked as " + name));
        } else {
            if (!sender.hasPermission("serble.nick.self")) {
                sender.sendMessage(Functions.translate("&cYou do not have permission!"));
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("You cannot do that!");
                return true;
            }
            NicknameManager.randomNick((Player) sender);
            sender.sendMessage(Functions.translate("&aYou have been nicked successfully"));
            sender.sendMessage(Functions.translate("&aRejoin for this to fully take effect"));
        }
        return true;
    }


}
