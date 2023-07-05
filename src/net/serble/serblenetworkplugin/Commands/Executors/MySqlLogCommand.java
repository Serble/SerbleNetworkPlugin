package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;

public class MySqlLogCommand extends SerbleCommand {
    public static boolean enabled = false;

    @Override
    public void execute(SlashCommand cmd) {
        enabled = !enabled;
        cmd.send("&aMySQL logging is now " + (enabled ? "enabled" : "disabled"));
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
