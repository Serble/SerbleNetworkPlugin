package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;

public class RankNickCommand extends SerbleCommand {
    @Override
    public void execute(SlashCommand cmd) {
        cmd.sendError("This command has been disabled. Use /nick instead.");
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return EMPTY_TAB_COMPLETE;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
