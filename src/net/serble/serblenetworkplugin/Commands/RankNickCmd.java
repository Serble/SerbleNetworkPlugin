package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;

public class RankNickCmd extends SerbleCommand {
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
