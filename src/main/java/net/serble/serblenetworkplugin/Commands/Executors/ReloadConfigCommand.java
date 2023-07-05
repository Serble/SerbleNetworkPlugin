package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.ConfigManager;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;

public class ReloadConfigCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Main.plugin.reloadConfig();
        cmd.send("&aLocal config reloaded!");

        if (cmd.getSenderType() == CommandSenderType.Player) {
            ConfigManager.requestConfig(cmd.getPlayerExecutor(), true);
            cmd.send("&aGlobal config requested!");
            return;
        }
        cmd.send("&cYou are not a player, so we cannot request the global config!");
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
