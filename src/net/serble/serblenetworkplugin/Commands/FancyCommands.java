package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FancyCommands {

    public static SlashCommand processCommand(CommandSender sender, String[] args, String permission, String usage, CommandSenderType... allowedSenderTypes) {
        CommandSenderType senderType = CommandSenderType.Console;
        if (sender instanceof Player) {
            senderType = CommandSenderType.Player;
        }
        if (sender instanceof BlockCommandSender) {
            senderType = CommandSenderType.CommandBlock;
        }

        if (allowedSenderTypes != null && allowedSenderTypes.length > 0) {
            boolean isAllowed = false;
            for (CommandSenderType allowedSenderType : allowedSenderTypes) {
                if (allowedSenderType == senderType) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                sender.sendMessage(Functions.translate("&cYou cannot do this!"));
                return new SlashCommand(sender, new SlashCommandArgument[0], false, senderType, usage);
            }
        }

        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(Functions.translate("&cYou do not have permission!"));
            return new SlashCommand(sender, new SlashCommandArgument[0], false, senderType, usage);
        }

        List<SlashCommandArgument> newArgs = new ArrayList<>();
        for (String arg : args) {
            if (arg.contains("@p") && senderType != CommandSenderType.Console) {
                // Nearest player to the sender
                Player nearestPlayer = null;
                double nearestDistance = Double.MAX_VALUE;
                Location senderLocation = getLocationOfCommandSender(sender);
                if (senderLocation != null) {
                    for (Player player : Main.plugin.getServer().getOnlinePlayers()) {
                        if (player.getWorld().getUID() != senderLocation.getWorld().getUID()) continue;
                        double distance = player.getLocation().distanceSquared(senderLocation);
                        if (distance < nearestDistance) {
                            nearestPlayer = player;
                            nearestDistance = distance;
                        }
                    }
                    if (nearestPlayer != null) {
                        newArgs.add(new SlashCommandArgument(arg.replace("@p", nearestPlayer.getName())));
                        continue;
                    }
                }
            }

            if (arg.contains("@s") && senderType == CommandSenderType.Player) {
                // Sender
                newArgs.add(new SlashCommandArgument(arg.replace("@s", sender.getName())));
                continue;
            }

            if (arg.contains("@r")) {
                // Random player
                Player[] players = Main.plugin.getServer().getOnlinePlayers().toArray(new Player[0]);
                if (players.length > 0) {
                    newArgs.add(new SlashCommandArgument(arg.replace("@r", players[Functions.getRandom().nextInt(players.length)].getName())));
                    continue;
                }
            }

            if (arg.contains("@a")) {
                // All players
                Player[] players = Main.plugin.getServer().getOnlinePlayers().toArray(new Player[0]);
                if (players.length > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (Player player : players) {
                        builder.append(player.getName()).append(",");
                    }
                    newArgs.add(new SlashCommandArgument(arg.replace("@a", builder.substring(0, builder.length() - 1))));
                    continue;
                }
            }

            if (arg.contains("@e")) {
                // All entities
                StringBuilder builder = new StringBuilder();
                Location senderLoc = getLocationOfCommandSender(sender);
                if (senderLoc != null) {
                    for (Entity entity : Objects.requireNonNull(senderLoc.getWorld()).getEntities()) {
                        builder.append(entity.getUniqueId()).append(",");
                    }
                    newArgs.add(new SlashCommandArgument(arg.replace("@e", builder.substring(0, builder.length() - 1))));
                    continue;
                }
            }

            newArgs.add(new SlashCommandArgument(arg));
        }

        return new SlashCommand(sender, newArgs.toArray(new SlashCommandArgument[0]), true, senderType, usage);
    }

    public static SlashCommand processCommand(CommandSender sender, String[] args) {
        return processCommand(sender, args, null, "");
    }

    public static Location getLocationOfCommandSender(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getLocation();
        }
        if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getLocation();
        }
        return null;
    }

}
