package net.serble.serblenetworkplugin.Schemas.TabComplete;

import java.util.List;

public class TabCompleteEnumResult implements TabCompleteSlotResult {
    private final String[] values;

    public TabCompleteEnumResult(String... values) {
        this.values = values;
    }

    @Override
    public List<String> get() {
        return List.of(values);
    }
}
