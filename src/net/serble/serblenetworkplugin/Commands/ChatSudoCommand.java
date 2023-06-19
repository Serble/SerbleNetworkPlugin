package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Chat;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommandArgument;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatSudoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.chatsudo")
                .withUsage("&cUsage: /chatsudo <PLAYER> <MESSAGE>")
                .process();
        if (!cmd.isAllowed()) {
            return false;
        }

        SlashCommandArgument playerArg = cmd.getArg(0);
        if (playerArg == null) {
            cmd.sendUsage();
            return true;
        }

        List<Player> p = playerArg.getPlayerList();
        if (p == null) {
            cmd.sendUsage();
            return true;
        }
        String msg = cmd.combineArgs(1);

        for (Player player : p) {
            Chat.FakeChat(player, msg);
        }
        return true;
    }

}
