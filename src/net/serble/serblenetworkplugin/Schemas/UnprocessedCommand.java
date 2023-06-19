package net.serble.serblenetworkplugin.Schemas;

import net.serble.serblenetworkplugin.FancyCommands;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnprocessedCommand {
    private final CommandSender sender;
    private final String[] args;
    private String permission = null;
    private final List<CommandSenderType> validSenders = new ArrayList<>();
    private String usage = null;

    public UnprocessedCommand(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public UnprocessedCommand withPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public UnprocessedCommand withValidSenders(CommandSenderType... validSenders) {
        this.validSenders.addAll(Arrays.asList(validSenders));
        return this;
    }

    public UnprocessedCommand withUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public SlashCommand process() {
        return FancyCommands.processCommand(sender, args, permission, usage, validSenders.toArray(new CommandSenderType[0]));
    }

}
