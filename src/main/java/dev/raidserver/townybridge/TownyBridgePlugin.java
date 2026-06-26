package dev.raidserver.townybridge;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

/**
 * KubeJS plugin entry point.
 *
 * KubeJS discovers this class via the service-loader file:
 *   META-INF/services/dev.latvian.mods.kubejs.KubeJSPlugin
 *
 * Registers one binding:
 *   {@code TownyBridge}  — an instance of {@link TownyBridgeAPI}, available
 *                          in every server-side KubeJS script.
 */
public class TownyBridgePlugin extends KubeJSPlugin {

    private static final TownyBridgeAPI API = new TownyBridgeAPI();

    @Override
    public void registerBindings(BindingsEvent event) {
        // Only inject on the server; clients don't have Towny or Bukkit.
        if (event.getType() == ScriptType.SERVER) {
            event.add("TownyBridge", API);
        }
    }
}
