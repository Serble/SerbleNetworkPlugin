package net.serble.serblenetworkplugin.Schemas;

import net.serble.serblenetworkplugin.Functions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlashCommand {
    private final CommandSender executor;
    private final SlashCommandArgument[] args;
    private final boolean isAllowed;
    private final CommandSenderType senderType;
    private final Player[] targets;
    private final String usage;

    public SlashCommand(CommandSender executor, SlashCommandArgument[] args, boolean isAllowed, CommandSenderType senderType, String usage, Player... targets) {
        this.executor = executor;
        this.args = args;
        this.isAllowed = isAllowed;
        this.senderType = senderType;
        this.targets = targets;
        this.usage = Functions.translate(usage);
    }

    public CommandSender getExecutor() {
        return executor;
    }

    public SlashCommandArgument[] getArgs() {
        return args;
    }

    public SlashCommandArgument getArg(int index) {
        if (index >= args.length) return null;
        return args[index];
    }

    public SlashCommandArgument getArgIgnoreNull(int index) {
        if (index >= args.length) return new SlashCommandArgument(null);
        return args[index];
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public CommandSenderType getSenderType() {
        return senderType;
    }

    public Player[] getTargets() {
        return targets;
    }

    public void sendUsage() {
        executor.sendMessage(usage);
    }

    public String combineArgs(int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i].getText()).append(" ");
        }
        return builder.toString();
    }
}