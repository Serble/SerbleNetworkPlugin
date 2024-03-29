package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommandArgument;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.ProfilePermissionsManager;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.PermissionSettings;
import org.bukkit.entity.Player;

import java.util.List;

public class ProfilePermissionsCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {  // /profileperms set <user> <permission node> <true/false>
        SlashCommandArgument setOrUnsetArg = cmd.getArgIgnoreNull(0);

        if (setOrUnsetArg.equalsIgnoreCase("set")) {
            List<Player> player = cmd.getArgIgnoreNull(1).getPlayerList();
            if (player == null) {
                cmd.sendUsage("Invalid player");
                return;
            }

            String node = cmd.getArgIgnoreNull(2).getText();
            Boolean value = cmd.getArgIgnoreNull(3).getBoolean();
            if (node == null) {
                cmd.sendUsage("Invalid node");
                return;
            }
            if (value == null) {
                cmd.sendUsage("Invalid value");
                return;
            }

            for (Player p : player) {
                ProfilePermissionsManager.addPermission(GameProfileUtils.getPlayerUuid(p), new PermissionSettings(node, value));
            }
            return;
        }

        if (setOrUnsetArg.equalsIgnoreCase("unset")) {
            List<Player> player = cmd.getArgIgnoreNull(1).getPlayerList();
            if (player == null) {
                cmd.sendUsage("Invalid player");
                return;
            }

            String node = cmd.getArgIgnoreNull(2).getText();
            if (node == null) {
                cmd.sendUsage("Invalid node");
                return;
            }

            for (Player p : player) {
                ProfilePermissionsManager.removePermission(GameProfileUtils.getPlayerUuid(p), new PermissionSettings(node, false));
            }
            return;
        }
        cmd.sendUsage();
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompleteEnumResult("set", "unset"))
                .setCase(new TabCompletePlayerResult(), (String) null)
                .setCase(new TabCompleteEnumResult("true", "false"), null, null, null);
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
