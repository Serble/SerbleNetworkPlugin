package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.ConfigManager;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;

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
