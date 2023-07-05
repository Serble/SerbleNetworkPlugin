package net.serble.serblenetworkplugin.Commands.TabComplete;

import net.serble.serblenetworkplugin.Commands.SlashCommandArgument;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionBuilder {
    private final SlashCommandArgument[] args;
    private final List<TabCompleteBuilderEntry> entries;
    private TabCompleteSlotResult everyArgEntry;
    private List<String> rawOutput;

    public TabCompletionBuilder(SlashCommandArgument[] args) {
        this.args = args;
        entries = new ArrayList<>();
    }

    public TabCompletionBuilder(List<String> raw) {
        this.rawOutput = raw;
        entries = new ArrayList<>();
        args = null;
    }

    public TabCompletionBuilder setCase(TabCompleteSlotResult result, String... precedingArgs) {
        if (precedingArgs == null) {
            precedingArgs = new String[0];
        }
        TabCompleteBuilderEntry entry = new TabCompleteBuilderEntry(precedingArgs, result);
        entries.add(entry);
        return this;
    }

    public TabCompletionBuilder setEveryCase(TabCompleteSlotResult result) {
        everyArgEntry = result;
        return this;
    }

    public List<String> process() {
        if (rawOutput != null) {
            return rawOutput;
        }

        if (everyArgEntry != null) {
            return everyArgEntry.get();
        }

        // Get all entries where the preceding args match the args (ignore nulls)
        List<TabCompleteBuilderEntry> matchingEntries = new ArrayList<>();
        for (TabCompleteBuilderEntry entry : entries) {
            String[] precedingArgs = entry.getPrecedingArgs();
            assert args != null;
            if (precedingArgs.length != args.length-1) continue;
            boolean match = true;
            for (int i = 0; i < precedingArgs.length; i++) {
                if (precedingArgs[i] == null) continue;
                if (!precedingArgs[i].equalsIgnoreCase(args[i].getText())) {
                    match = false;
                    break;
                }
            }
            if (match) matchingEntries.add(entry);
        }

        // Get the results from the matching entries
        List<String> results = new ArrayList<>();
        for (TabCompleteBuilderEntry entry : matchingEntries) {
            TabCompleteSlotResult result = entry.getResult();
            results.addAll(result.get());
        }
        return results;
    }

}
