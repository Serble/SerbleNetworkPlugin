package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompleteEmptyResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;

public abstract class SerbleCommand {
    public abstract void execute(SlashCommand cmd);
    public abstract TabCompletionBuilder tabComplete(SlashCommand cmd);
    public abstract CommandSenderType[] getAllowedSenders();

    public static final CommandSenderType[] ALL_SENDERS = null;
    public static final CommandSenderType[] PLAYER_SENDER = new CommandSenderType[] { CommandSenderType.Player };
    public static final TabCompletionBuilder EMPTY_TAB_COMPLETE = new TabCompletionBuilder((SlashCommandArgument[]) null).setEveryCase(new TabCompleteEmptyResult());

    public CommandSenderType[] getArrayOfSenders(CommandSenderType... senders) {
        return senders;
    }

}
