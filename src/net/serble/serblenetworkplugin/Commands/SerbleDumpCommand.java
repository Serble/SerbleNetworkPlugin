package net.serble.serblenetworkplugin.Commands;

import com.google.gson.Gson;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import net.serble.serblenetworkplugin.Schemas.CommandSenderType;
import net.serble.serblenetworkplugin.Schemas.Party;
import net.serble.serblenetworkplugin.Schemas.SerbleCommand;
import net.serble.serblenetworkplugin.Schemas.SlashCommand;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompleteEnumResult;
import net.serble.serblenetworkplugin.Schemas.TabComplete.TabCompletionBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SerbleDumpCommand extends SerbleCommand {

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
    public void execute(SlashCommand cmd) {
        String whatToDump = cmd.getArgIgnoreNull(0).getText();
        if (whatToDump == null) {
            cmd.sendUsage();
            return;
        }

        try {
            switch (whatToDump.toLowerCase()) {
                default:
                    cmd.sendUsage("Invalid dataset");
                    return;

                case "myparty":
                    if (cmd.getSenderType() == CommandSenderType.Player) {
                        cmd.sendUsage("Only players can use this command");
                        return;
                    }
                    Player p = cmd.getPlayerExecutor();
                    Party party = Main.partyManager.getParty(p);
                    dump(p, party);
                    return;

                case "allparties":
                    dump(cmd.getExecutor(), Main.partyManager.getParties());
                    //noinspection UnnecessaryReturnStatement
                    return;

            }
        } catch (Exception e) {
            dump(cmd.getExecutor(), "&cAn error occurred while dumping the dataset");
            e.printStackTrace();
        }
    }

    @Override
    public TabCompletionBuilder tabComplete(SlashCommand cmd) {
        return new TabCompletionBuilder(cmd.getArgs())
                .setCase(new TabCompleteEnumResult("myparty", "allparties"));
    }

    @Override
    public CommandSenderType[] getAllowedSenders() {
        return ALL_SENDERS;
    }
}
