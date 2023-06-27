package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.PlayerUuidCacheHandler;
import net.serble.serblenetworkplugin.ProfilePermissionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileCommand implements CommandExecutor, TabCompleter {

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
            UUID profile = PlayerUuidCacheHandler.getInstance().getPlayerUuid(p.getUniqueId());
            String profileName = Main.sqlData.getGameProfileName(profile);
            if (profileName == null) profileName = "default";

            p.sendMessage(Functions.translate("&aActive Profile: &7" + profileName));
            p.sendMessage(Functions.translate("&aYour profiles:"));
            for (String userPfName : PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId())) {
                p.sendMessage(Functions.translate("&7- " + userPfName));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                p.sendMessage(Functions.translate("&cUsage: /profile add <profile>"));
                return true;
            }

            if (PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId()).size() >= 50) {
                p.sendMessage(Functions.translate("&cYou cannot have more than 50 profiles!"));
                return true;
            }

            String profileName = args[1];

            Main.sqlData.createProfile(p.getUniqueId(), UUID.randomUUID(), profileName);
            PlayerUuidCacheHandler.getInstance().invalidateProfileList(p.getUniqueId());
            p.sendMessage(Functions.translate("&aProfile &7" + profileName + "&a added!"));
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
            p.sendMessage(Functions.translate("&aProfile set to &7" + profileName + "&a!"));
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            ProfilePermissionsManager.loadPermissions(p);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();

        if (!(sender instanceof Player)) return results;
        if (!sender.hasPermission("serble.profiles")) return results;
        Player p = (Player) sender;

        if (args.length == 1) {
            results.add("add");
            results.add("set");
            return results;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            for (String profile : PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId())) {
                if (!profile.toLowerCase().startsWith(args[1].toLowerCase())) continue;
                results.add(profile);
            }
        }

        return results;
    }

}
