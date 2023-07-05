package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.Bukkit;

public class CosmeticCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Bukkit.dispatchCommand(cmd.getExecutor(), "uc menu main");
        AchievementsManager.GrantAchievementProgress(cmd.getPlayerExecutor(), Achievement.LOOKING_FANCY);
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return EMPTY_TAB_COMPLETE;
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
