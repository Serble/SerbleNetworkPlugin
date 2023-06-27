package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.ProfilePermissionsManager;
import net.serble.serblenetworkplugin.Schemas.PermissionSettings;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommandArgument;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ProfilePermissionsCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.permissions")
                .withUsage("/profileperms <set/unset> <user> <permission node> <true/false>")
                .process();
        if (!cmd.isAllowed()) return true;

        // profileperms set <profile uuid> <permission node> <true/false>
        // profileperms unset <profile uuid> <permission node>

        SlashCommandArgument setOrUnsetArg = cmd.getArgIgnoreNull(0);

        if (setOrUnsetArg.equalsIgnoreCase("set")) {
            if (args.length != 4) {
                sender.sendMessage(Functions.translate("&cUsage: /profileperms set <user> <permission node> <true/false>"));
                return true;
            }

            List<Player> player = cmd.getArgIgnoreNull(1).getPlayerList();
            if (player == null) {
                cmd.sendUsage("Invalid player");
                return true;
            }

            String node = args[2];

            Boolean value = cmd.getArgIgnoreNull(3).getBoolean();
            if (value == null) {
                cmd.sendUsage("Invalid value");
                return true;
            }

            for (Player p : player) {
                ProfilePermissionsManager.addPermission(GameProfileUtils.getPlayerUuid(p), new PermissionSettings(node, value));
            }
            return true;
        }

        if (setOrUnsetArg.equalsIgnoreCase("unset")) {
            if (args.length != 3) {
                cmd.sendUsage();
                return true;
            }

            List<Player> player = cmd.getArgIgnoreNull(1).getPlayerList();
            if (player == null) {
                cmd.sendUsage("Invalid player");
                return true;
            }

            String node = args[2];

            for (Player p : player) {
                ProfilePermissionsManager.removePermission(GameProfileUtils.getPlayerUuid(p), new PermissionSettings(node, false));
            }
            return true;
        }
        cmd.sendUsage();
        return false;
    }

}
