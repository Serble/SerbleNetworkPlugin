package net.serble.serblenetworkplugin.Schemas;

public class PermissionSettings {
    public String node;
    public boolean value;

    public PermissionSettings(String node, boolean value) {
        this.node = node;
        this.value = value;
    }
}
