package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import org.bukkit.Bukkit;

public class BuildCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Bukkit.dispatchCommand(cmd.getExecutor(), "rg toggle-bypass");
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return null;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
