package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Chat;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommandArgument;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatSudoCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        SlashCommandArgument playerArg = cmd.getArg(0);
        if (playerArg == null) {
            cmd.sendUsage();
            return;
        }

        List<Player> p = playerArg.getPlayerList();
        if (p == null) {
            cmd.sendUsage();
            return;
        }
        String msg = cmd.combineArgs(1);

        for (Player player : p) {
            Chat.fakeChat(player, msg);
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs()).setCase(new TabCompletePlayerResult());
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
