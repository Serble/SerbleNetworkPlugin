package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.AchievementsManager;
import net.serble.serblenetworkplugin.Schemas.Achievement;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.Bukkit;

public class CosmeticCmd extends SerbleCommand {

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
