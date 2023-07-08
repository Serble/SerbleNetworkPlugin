package net.serble.serblenetworkplugin.Commands.Executors;

import net.serble.serblenetworkplugin.Commands.FancyCommands;
import net.serble.serblenetworkplugin.Commands.SerbleCommand;
import net.serble.serblenetworkplugin.Commands.SlashCommand;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletePlayerResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompleteWorldResult;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnPointCommand extends SerbleCommand {

    @Override
    public void execute(SlashCommand cmd) {
        List<Player> players = cmd.getArgIgnoreNull(0).getPlayerList();
        if (players == null) {
            cmd.sendUsage();
            return;
        }

        Integer x = cmd.getArgIgnoreNull(1).getInteger();
        Integer y = cmd.getArgIgnoreNull(2).getInteger();
        Integer z = cmd.getArgIgnoreNull(3).getInteger();
        World world = cmd.getArgIgnoreNull(4).getWorld();

        Location senderLocation = FancyCommands.getLocationOfCommandSender(cmd.getExecutor());

        if (x == null || y == null || z == null) {
            x = senderLocation.getBlockX();
            y = senderLocation.getBlockY();
            z = senderLocation.getBlockZ();
        }

        if (world == null) {
            world = senderLocation.getWorld();
            if (world == null) {
                cmd.sendError("Console users must specify a world!");
                return;
            }
        }

        Location location = new Location(world, x, y, z);
        for (Player player : players) {
            String worldGroup = Main.worldGroupInventoryManager.getPlayerWorldGroup(player);
            if (worldGroup == null) {
                cmd.sendError("Player " + player.getName() + " is not in a world group!");
                continue;
            }
            Main.worldGroupInventoryManager.setPlayerCurrentSpawnPoint(player, worldGroup, location);
            cmd.send("&aSet spawn point for " + player.getName() + " to " + location + " in world group " + worldGroup);
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompletePlayerResult())
                .setCase(new TabCompleteWorldResult(), null, null, null, null);
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
