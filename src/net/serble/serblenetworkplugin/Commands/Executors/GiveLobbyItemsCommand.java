package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.MenuItemManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import org.bukkit.entity.Player;

public class GiveLobbyItemsCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player target = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayer();
        if (target == null) {
            cmd.sendUsage("Invalid player");
            return;
        }

        MenuItemManager.GiveMenuItems(target);

        if (cmd.getSenderType() == CommandSenderType.Player) {
            cmd.send("&aGave " + target.getName() + " lobby items");
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompletePlayerResult());
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
