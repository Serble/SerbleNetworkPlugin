package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class GrantAchievementProgressCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Achievement achievement;
        try {
            achievement = Achievement.valueOf(cmd.getArg(1).getText());
        } catch (IllegalArgumentException e) {
            cmd.sendUsage("Invalid achievement");
            return;
        }

        Integer amount = cmd.getArg(2) == null ? null : cmd.getArg(2).getInteger();
        if (amount == null) {
            cmd.sendUsage("Invalid progress amount");
            return;
        }

        List<Player> p = cmd.getArg(0) == null ? null : cmd.getArg(0).getPlayerList();
        if (p == null) {
            cmd.sendUsage("Invalid player");
            return;
        }

        for (Player player : p) AchievementsManager.GrantAchievementProgress(player, achievement, amount);
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompletePlayerResult())
                .setCase(new TabCompleteEnumResult(Arrays.stream(Achievement.values()).map(Enum::name).toArray(String[]::new)), (String) null);
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
