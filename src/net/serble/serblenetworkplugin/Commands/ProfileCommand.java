package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.*;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommandArgument;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompleteProfilesResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ProfileCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        Player p = cmd.getPlayerExecutor();

        SlashCommandArgument firstArg = cmd.getArgIgnoreNull(0);
        if (cmd.getArgs().length == 0) {
            // Display current profile
            UUID profile = PlayerUuidCacheHandler.getInstance().getPlayerUuid(p.getUniqueId());
            String profileName = Main.sqlData.getGameProfileName(profile);
            if (profileName == null) profileName = "default";

            cmd.send("&aActive Profile: &7" + profileName);
            cmd.send("&aYour profiles:");
            for (String userPfName : PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId())) {
                cmd.send("&7- " + userPfName);
            }
            return;
        }

        if (firstArg.equalsIgnoreCase("add")) {
            if (PlayerUuidCacheHandler.getInstance().getProfileList(p.getUniqueId()).size() >= 50) {
                cmd.sendError("You cannot have more than 50 profiles!");
                return;
            }

            String profileName = cmd.getArgIgnoreNull(1).getText();
            if (profileName == null) {
                cmd.sendUsage();
                return;
            }

            Main.sqlData.createProfile(p.getUniqueId(), UUID.randomUUID(), profileName);
            PlayerUuidCacheHandler.getInstance().invalidateProfileList(p.getUniqueId());
            cmd.send("&aProfile &7" + profileName + "&a added!");
            return;
        }

        if (firstArg.equalsIgnoreCase("set")) {
            String profileName = cmd.getArgIgnoreNull(1).getText();
            if (profileName == null) {
                cmd.sendUsage();
                return;
            }

            UUID profile = Main.sqlData.getProfileIdFromName(p.getUniqueId(), profileName);
            if (profile == null && !profileName.equalsIgnoreCase("default")) {
                cmd.sendError("Profile not found!");
                return;
            } else if (profileName.equalsIgnoreCase("default")) {
                profile = p.getUniqueId();
            }

            Main.worldGroupInventoryManager.savePlayerInventory(p);
            ProfilePermissionsManager.removeAllPermissions(p);

            assert profile != null;
            Main.sqlData.setActiveProfile(p.getUniqueId(), profile == p.getUniqueId() ? "0" : profile.toString());
            CacheInvalidationManager.invalidateUuidCachesForPlayer(p.getUniqueId());
            p.sendMessage(Functions.translate("&aProfile set to &7" + profileName + "&a!"));
            Main.worldGroupInventoryManager.loadPlayerInventory(p);
            ProfilePermissionsManager.loadPermissions(p);
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompleteEnumResult("add", "set"))
                .setCase(new TabCompleteProfilesResult(cmd.getPlayerExecutor()), "set");
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return PLAYER_SENDER;
    }
}
