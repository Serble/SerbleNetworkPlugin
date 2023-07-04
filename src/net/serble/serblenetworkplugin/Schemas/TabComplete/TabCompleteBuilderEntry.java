package net.serble.serblenetworkplugin.Schemas.TabComplete;

public class TabCompleteBuilderEntry {
    private final String[] precedingArgs;
    private final TabCompleteSlotResult result;

    public TabCompleteBuilderEntry(String[] precedingArgs, TabCompleteSlotResult result) {
        this.precedingArgs = precedingArgs;
        this.result = result;
    }

    public String[] getPrecedingArgs() {
        return precedingArgs;
    }

    public TabCompleteSlotResult getResult() {
        return result;
    }
}
