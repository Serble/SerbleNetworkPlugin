package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Chat;
import net.serble.serblenetworkplugin.Functions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSudoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("serble.chatsudo")) {
            sender.sendMessage(Functions.translate("&4You do not have permission!"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Functions.translate("&4Usage: /chatsudo <PLAYER> <MESSAGE>"));
            return true;
        }

        Player p;
        try {
            p = Bukkit.getPlayer(args[0]);
            if (p == null) throw new NullPointerException("Player is null");
        } catch (Exception e) {
            sender.sendMessage(Functions.translate("&4Player doesn't exist"));
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i] + " ");
        }

        Chat.FakeChat(p, message.toString());
        return true;
    }

}
