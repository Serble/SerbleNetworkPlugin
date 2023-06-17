package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.GameProfileUtils;
import net.serble.serblenetworkplugin.ProfilePermissionsManager;
import net.serble.serblenetworkplugin.Schemas.PermissionSettings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;
import java.util.UUID;

public class ProfilePermissionsCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("serble.permissions")) {
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return true;
        }

        // profileperms set <profile uuid> <permission node> <true/false>
        // profileperms unset <profile uuid> <permission node>
        if (args.length == 0) {
            sender.sendMessage(Functions.translate("&cUsage: /profileperms <set/unset> <user> <permission node> <true/false>"));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 4) {
                sender.sendMessage(Functions.translate("&cUsage: /profileperms set <user> <permission node> <true/false>"));
                return true;
            }

            UUID profile;
            try {
                profile = GameProfileUtils.getPlayerUuid(Objects.requireNonNull(Bukkit.getPlayer(args[1])));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Functions.translate("&cInvalid profile uuid!"));
                return true;
            }

            String node = args[2];

            boolean value;
            if (args[3].equalsIgnoreCase("true")) {
                value = true;
            } else if (args[3].equalsIgnoreCase("false")) {
                value = false;
            } else {
                sender.sendMessage(Functions.translate("&cInvalid value!"));
                return true;
            }

            ProfilePermissionsManager.addPermission(profile, new PermissionSettings(node, value));
        }

        if (args[0].equalsIgnoreCase("unset")) {
            if (args.length != 3) {
                sender.sendMessage(Functions.translate("&cUsage: /profileperms unset <profile uuid> <permission node>"));
                return true;
            }

            UUID profile;
            try {
                profile = UUID.fromString(args[1]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Functions.translate("&cInvalid profile uuid!"));
                return true;
            }

            String node = args[2];

            ProfilePermissionsManager.removePermission(profile, new PermissionSettings(node, false));
        }
        return false;
    }

}
