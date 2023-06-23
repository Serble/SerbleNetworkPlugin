package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.*;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnPointCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        SlashCommand command = new UnprocessedCommand(sender, args)
                .withPermission("serble.setspawnpoint")
                .withUsage("/setspawnpoint <user> <x> <y> <z> [world]")
                .process();
        if (!command.isAllowed()) {
            return false;
        }

        List<Player> players = command.getArgIgnoreNull(0).getPlayerList();
        if (players == null) {
            command.sendUsage();
            return true;
        }

        Integer x = command.getArgIgnoreNull(1).getInteger();
        Integer y = command.getArgIgnoreNull(2).getInteger();
        Integer z = command.getArgIgnoreNull(3).getInteger();
        World world = command.getArgIgnoreNull(4).getWorld();

        Location senderLocation = FancyCommands.getLocationOfCommandSender(sender);

        if (x == null || y == null || z == null) {
            x = senderLocation.getBlockX();
            y = senderLocation.getBlockY();
            z = senderLocation.getBlockZ();
        }

        if (world == null) {
            world = senderLocation.getWorld();
            if (world == null) {
                sender.sendMessage(Functions.translate("&cConsole users must specify a world!"));
                return true;
            }
        }

        Location location = new Location(world, x, y, z);
        for (Player player : players) {
            String worldGroup = Main.worldGroupInventoryManager.getPlayerWorldGroup(player);
            if (worldGroup == null) {
                sender.sendMessage(Functions.translate("&cPlayer " + player.getName() + " is not in a world group!"));
                continue;
            }
            Main.worldGroupInventoryManager.setPlayerCurrentSpawnPoint(GameProfileUtils.getPlayerUuid(player), worldGroup, location);
            sender.sendMessage(Functions.translate("&aSet spawn point for " + player.getName() + " to " + location + " in world group " + worldGroup));
        }
        return true;
    }
}
