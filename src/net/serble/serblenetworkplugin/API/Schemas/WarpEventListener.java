package net.serble.serblenetworkplugin.API.Schemas;

public interface WarpEventListener {
    boolean onWarpEvent(WarpEvent event);  // Return true if warp was handled and no further action should be taken
}
