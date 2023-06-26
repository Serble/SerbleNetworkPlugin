package net.serble.serblenetworkplugin.Commands;

import com.google.gson.Gson;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.Party;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.UnprocessedCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SerbleDumpCommand implements CommandExecutor {

    private void dump(CommandSender sender, Object dataset) {
        if (dataset instanceof String) {
            sender.sendMessage(Functions.translate((String) dataset));
            return;
        }
        if (dataset == null) {
            sender.sendMessage(Functions.translate("&cnull"));
            return;
        }
        sender.sendMessage(Functions.translate("&a" + dataset.getClass().getSimpleName() + ":"));
        sender.sendMessage(Functions.translate("&7" + new Gson().toJson(dataset)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SlashCommand cmd = new UnprocessedCommand(sender, args)
                .withPermission("serble.dump")
                .withUsage("/serbledump <dataset>")
                .process();

        if (!cmd.isAllowed()) {
            return true;
        }

        String whatToDump = cmd.getArg(0) == null ? null : cmd.getArg(0).getText();
        if (whatToDump == null) {
            cmd.sendUsage();
            return true;
        }

        try {
            switch (whatToDump.toLowerCase()) {
                default:
                    cmd.sendUsage("Invalid dataset");
                    return true;

                case "myparty":
                    if (!(sender instanceof Player)) {
                        cmd.sendUsage("Only players can use this command");
                        return true;
                    }
                    Player p = (Player) sender;
                    Party party = Main.partyManager.getParty(p);
                    dump(p, party);
                    return true;

                case "allparties":
                    dump(sender, Main.partyManager.getParties());
                    return true;

            }
        } catch (Exception e) {
            dump(sender, "&cAn error occurred while dumping the dataset");
            e.printStackTrace();
            return true;
        }
    }

}
