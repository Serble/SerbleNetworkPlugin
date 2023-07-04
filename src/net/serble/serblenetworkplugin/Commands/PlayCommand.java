package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.Bukkit;

public class PlayCommand extends SerbleCommand {

    // THIS HAS BEEN REPLACED BY BUNGEECORD
    @Override
    public void execute(SlashCommand cmd) {
        Bukkit.dispatchCommand(cmd.getExecutor(), "proxyexecute play " + cmd.combineArgs(0));
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return null;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
