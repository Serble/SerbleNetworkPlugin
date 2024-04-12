package net.serble.serblenetworkplugin.Commands;

import net.serble.serblenetworkplugin.Commands.Executors.*;
import net.serble.serblenetworkplugin.Commands.TabComplete.TabCompletionBuilder;
import net.serble.serblenetworkplugin.Functions;
import net.serble.serblenetworkplugin.Main;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final HashMap<String, SerbleCommand> commands = new HashMap<>();

    public void registerCommands() {
        registerCommand("menu", new MenuCommand());
        registerCommand("adminmode", new AdminModeCommand());
        registerCommand("spawn", new SpawnCommand());
        registerCommand("cosmetic", new CosmeticCommand());
        registerCommand("ranknick", new RankNickCommand());
        registerCommand("nick", new NickCommand());
        registerCommand("unnick", new UnnickCommand());
        registerCommand("build", new BuildCommand());
        registerCommand("reloadconfig", new ReloadConfigCommand());
        registerCommand("money", new MoneyCommand());
        registerCommand("store", new StoreCommand());
        registerCommand("sysgivemoney", new SystemGiveMoneyCommand());
        registerCommand("play", new PlayCommand());
        registerCommand("chatsudo", new ChatSudoCommand());
        registerCommand("serblexp", new SerbleXpCommand());
        registerCommand("sysgivexp", new SystemGiveXpCommand());
        registerCommand("grantachievementprogress", new GrantAchievementProgressCommand());
        registerCommand("profile", new ProfileCommand());
        registerCommand("profileperms", new ProfilePermissionsCommand());
        registerCommand("serbledebug", new SerbleDebugCommand());
        registerCommand("setspawnpoint", new SetSpawnPointCommand());
        registerCommand("sysdebug", new SystemDebugCommand());
        registerCommand("givelobbyitems", new GiveLobbyItemsCommand());
        registerCommand("nickas", new NickAsCommand());
        registerCommand("profileof", new ProfilesOfCommand());
        registerCommand("serbledump", new SerbleDumpCommand());
        registerCommand("achievements", new AchievementsCommand());
        registerCommand("proxyexecute", new ProxyExecuteCommand());
        registerCommand("mysqllog", new MySqlLogCommand());
        registerCommand("ping", new PingCommand());
    }

    private void registerCommand(String cmd, SerbleCommand executor) {
        PluginCommand pluginCommand = Objects.requireNonNull(Main.plugin.getCommand(cmd));
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);

        commands.put(cmd, executor);
    }

    private SerbleCommand getExecutor(Command bukkitCommand) {
        return commands.getOrDefault(bukkitCommand.getName(), null);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command bukkitCommand, @NotNull String label, String[] args) {
        SerbleCommand executor = getExecutor(bukkitCommand);
        if (executor == null) {
            sender.sendMessage(Functions.translate("&cCommand not found"));
            return true;
        }
        UnprocessedCommand unprocessedCommand = new UnprocessedCommand(sender, args);
        unprocessedCommand.withUsage(bukkitCommand.getUsage());
        unprocessedCommand.withValidSenders(executor.getAllowedSenders());

        if (bukkitCommand.getPermission() != null) {
            unprocessedCommand.withPermission(bukkitCommand.getPermission());
        }
        SlashCommand cmd = unprocessedCommand.process();

        if (!cmd.isAllowed()) {
            return true;
        }
        executor.execute(cmd);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command bukkitCommand, @NotNull String label, String[] args) {
        SerbleCommand executor = getExecutor(bukkitCommand);
        if (executor == null) {
            sender.sendMessage(Functions.translate("&cCommand not found"));
            return new ArrayList<>();
        }
        UnprocessedCommand unprocessedCommand = new UnprocessedCommand(sender, args);
        unprocessedCommand.withUsage(bukkitCommand.getUsage());
        unprocessedCommand.withValidSenders(executor.getAllowedSenders());

        if (bukkitCommand.getPermission() != null) {
            unprocessedCommand.withPermission(bukkitCommand.getPermission());
        }
        SlashCommand cmd = unprocessedCommand.process();

        if (!cmd.isAllowed()) {
            return new ArrayList<>();
        }
        TabCompletionBuilder tabBuilder = executor.tabComplete(cmd);

        if (tabBuilder == null) {  // If the tab completer is null, return an empty list
            return new ArrayList<>();
        }

        List<String> tabComplete = tabBuilder.process();
        if (tabComplete == null) {
            List<String> usageCompleter = new ArrayList<>();

            String usage = bukkitCommand.getUsage();
            String[] usageSplit = usage.split(" ");

            if (usageSplit.length > args.length) {
                String usageArg = usageSplit[args.length];
                if (usageArg.startsWith("<") || usageArg.startsWith("[")) {
                    usageCompleter.add(usageArg
                            .replace("<", "")
                            .replace(">", "")
                            .replace("[", "")
                            .replace("]", ""));
                }
            }

            return usageCompleter;
        }

        // Remove all entries that don't start with the last argument
        if (args.length > 0) {
            String lastArg = args[args.length - 1];
            tabComplete.removeIf(s -> !s.toLowerCase().startsWith(lastArg.toLowerCase()));
        }

        return tabComplete;
    }
}
