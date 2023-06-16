package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.PlayerUuidCacheHandler;
import net.serble.serblenetworkplugin.ProfilePermissionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ProfileCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You cannot do this!");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("serble.profiles")) {
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return true;
        }

        if (args.length == 0) {
            // Display current profile
            UUID profile = Main.sqlData.getActiveUuid(p.getUniqueId());
            String profileName = Main.sqlData.getGameProfileName(profile);
            if (profileName == null) profileName = "default";

            p.sendMessage(Functions.translate("&aActive Profile: &7" + profileName));
            p.sendMessage(Functions.translate("&aYour profiles:"));
            for (String userPfName : Main.sqlData.listUserProfiles(p.getUniqueId())) {
                p.sendMessage(Functions.translate("&7- " + userPfName));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                p.sendMessage(Functions.translate("&cUsage: /profile add <profile>"));
                return true;
            }

            String profileName = args[1];

            Main.sqlData.createProfile(p.getUniqueId(), UUID.randomUUID(), profileName);
            p.sendMessage("Profile added!");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 2) {
                p.sendMessage(Functions.translate("&cUsage: /profile set <profile>"));
                return true;
            }

            String profileName = args[1];

            UUID profile = Main.sqlData.getProfileIdFromName(p.getUniqueId(), profileName);
            if (profile == null && !profileName.equalsIgnoreCase("default")) {
                p.sendMessage(Functions.translate("&cProfile not found!"));
                return true;
            } else if (profileName.equalsIgnoreCase("default")) {
                profile = p.getUniqueId();
            }

            Main.worldGroupInventoryManager.savePlayerInventory(p);
            ProfilePermissionsManager.removeAllPermissions(p);

            assert profile != null;
            Main.sqlData.setActiveProfile(p.getUniqueId(), profile == p.getUniqueId() ? "0" : profile.toString());
            PlayerUuidCacheHandler.getInstance().invalidatePlayerUuid(p.getUniqueId());
            p.sendMessage(Functions.translate("&aProfile set!"));
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            return true;
        }
        return false;
    }

}
