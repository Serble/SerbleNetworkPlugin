package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;

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
