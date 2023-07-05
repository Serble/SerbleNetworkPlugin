package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.ExperienceManager;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.MenuItemManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.entity.Player;

import java.util.List;

public class SystemGiveXpCommand extends SerbleCommand {

    public static void giveXp(Player p, int amount, String reason) {
        ExperienceManager.addSerbleXp(GameProfileUtils.getPlayerUuid(p), amount);

        // Message in chat
        p.sendMessage(Functions.translate("&9+ " + amount + " XP (" + reason + ")"));

        // If they have their level on their XP bar then change it
        if (MenuItemManager.shouldNotGetItems(p)) return;
        ExperienceManager.setPlayerExperience(p);
    }

    @Override
    public void execute(SlashCommand cmd) {
        Integer amount = cmd.getArg(1) == null ? null : cmd.getArg(1).getInteger();
        if (amount == null) {
            cmd.sendUsage("Invalid amount");
            return;
        }

        List<Player> players = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayerList();
        if (players == null) {
            cmd.sendUsage("Invalid player");
            return;
        }

        String reason = cmd.combineArgs(2);
        for (Player p : players) giveXp(p, amount, reason);

        if (cmd.getSenderType() == CommandSenderType.Player) {
            cmd.send("&aGave " + cmd.getArg(0).getText() + " " + amount + " XP");
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
