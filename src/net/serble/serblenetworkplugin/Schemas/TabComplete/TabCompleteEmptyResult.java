package net.serble.serblenetworkplugin.Schemas.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteEmptyResult implements TabCompleteSlotResult {
    @Override
    public List<String> get() {
        return new ArrayList<>();
    }
}
