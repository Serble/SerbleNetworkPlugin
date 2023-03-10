package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("serble.economy")) {
            sender.sendMessage(Functions.translate("&4You do not have permission to do this!"));
            return true;
        }

        //       0   1    2
        // money balance [USER]
        // money set USER AMOUNT
        // money add USER AMOUNT

        if (args.length == 0) {
            sender.sendMessage(Functions.translate("&4Usage: /money set USER AMOUNT"));
            sender.sendMessage(Functions.translate("&4Usage: /money add USER AMOUNT"));
            sender.sendMessage(Functions.translate("&4Usage: /money bal [USER]"));
            return false;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 3) {
                sender.sendMessage(Functions.translate("&4Usage: /money set USER AMOUNT"));
                return false;
            }

            Player p;
            OfflinePlayer p2;
            UUID uuid = null;
            try {
                p = Bukkit.getPlayer(args[1]);
                if (p == null) throw new NullPointerException("Player is null");
                uuid = p.getUniqueId();
            } catch (Exception e) {
                // invalid player
                p2 = Bukkit.getOfflinePlayer(args[1]);
                if (p2.hasPlayedBefore()) {
                    sender.sendMessage(Functions.translate
                            ("&7&lWARNING: &r&7Obtaining offline player through depreciated method! This is not recommended"));
                    uuid = p2.getUniqueId();
                } else {
                    sender.sendMessage(Functions.translate("&4That is not a valid player name! Usage: /money set USER AMOUNT"));
                    return false;
                }
            }

            int value;
            try {
                value = Integer.parseInt(args[2]);
            } catch (Exception e) {
                // invalid number
                sender.sendMessage(Functions.translate("&4That is not a valid integer! Usage: /money set USER AMOUNT"));
                return false;
            }

            Main.sqlData.setMoney(uuid, value);
            sender.sendMessage(Functions.translate(String.format("&aSet %s's balance to &7" + value, args[1])));
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 3) {
                sender.sendMessage(Functions.translate("&4Usage: /money add USER AMOUNT"));
                return false;
            }

            Player p;
            OfflinePlayer p2;
            UUID uuid = null;
            try {
                p = Bukkit.getPlayer(args[1]);
                if (p == null) throw new NullPointerException("Player is null");
                uuid = p.getUniqueId();
            } catch (Exception e) {
                // invalid player
                p2 = Bukkit.getOfflinePlayer(args[1]);
                if (p2.hasPlayedBefore()) {
                    sender.sendMessage(Functions.translate
                            ("&7&lWARNING: &r&7Obtaining offline player through depreciated method! This is not recommended"));
                    uuid = p2.getUniqueId();
                } else {
                    sender.sendMessage(Functions.translate("&4That is not a valid player name! Usage: /money set USER AMOUNT"));
                    return false;
                }
            }

            int value;
            try {
                value = Integer.parseInt(args[2]);
            } catch (Exception e) {
                // invalid number
                sender.sendMessage(Functions.translate("&4That is not a valid integer! Usage: /money add USER AMOUNT"));
                return false;
            }

            Main.sqlData.addMoney(uuid, value);
            sender.sendMessage(Functions.translate(String.format("&aAdded &7" + value + "&a to %s's balance ", args[1])));
            return true;
        }

        if (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")) {
            if (sender instanceof Player) {
                if (args.length == 1) {
                    int bal = Main.sqlData.getMoney(((Player)sender).getUniqueId());
                    sender.sendMessage(Functions.translate("&aBalance: &7" + bal));
                    return true;
                }
            }
            if (args.length != 2) {
                sender.sendMessage(Functions.translate("&4Usage: /money balance USER or /money balance"));
                return false;
            }
            Player p;
            OfflinePlayer p2;
            UUID uuid = null;
            try {
                p = Bukkit.getPlayer(args[1]);
                if (p == null) throw new NullPointerException("Player is null");
                uuid = p.getUniqueId();
            } catch (Exception e) {
                // invalid player
                p2 = Bukkit.getOfflinePlayer(args[1]);
                if (p2.hasPlayedBefore()) {
                    sender.sendMessage(Functions.translate
                            ("&7&lWARNING: &r&7Obtaining offline player through depreciated method! This is not recommended"));
                    uuid = p2.getUniqueId();
                } else {
                    sender.sendMessage(Functions.translate("&4That is not a valid player name! Usage: /money set USER AMOUNT"));
                    return false;
                }
            }

            int bal = Main.sqlData.getMoney(uuid);
            sender.sendMessage(Functions.translate(String.format("&aBalance of %s: &7" + bal, sender.getName())));
            return true;
        }

        return false;
    }

}
