package net.serble.serblenetworkplugin.Commands.TabComplete;

import java.util.Arrays;
import java.util.List;

public class TabCompleteEnumResult implements TabCompleteSlotResult {
    private final String[] values;

    public TabCompleteEnumResult(String... values) {
        this.values = values;
    }

    public <T extends Enum> TabCompleteEnumResult(T values) {
        this.values = (String[]) Arrays.stream(values.getClass().getEnumConstants()).map(Enum::name).toArray();
    }

    @Override
    public List<String> get() {
        return List.of(values);
    }
}
