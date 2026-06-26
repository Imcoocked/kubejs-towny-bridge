package dev.raidserver.townybridge;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import org.slf4j.Logger;

/**
 * TownyBridge — server-only companion mod for KubeJS + Towny on Mohist.
 *
 * Injects a single {@code TownyBridge} binding into KubeJS server scripts so
 * raid.js (and similar scripts) can call Towny without touching any of the
 * Java reflection primitives that KubeJS's class-filter blocks.
 *
 * The actual binding registration happens via the KubeJS plugin system:
 * {@link TownyBridgePlugin} is discovered from the META-INF/services file.
 */
@Mod(TownyBridgeMod.MOD_ID)
public class TownyBridgeMod {

    public static final String MOD_ID = "townybridge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TownyBridgeMod(IEventBus modEventBus) {
        modEventBus.addListener(this::onDedicatedServerSetup);
    }

    private void onDedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("[TownyBridge] Server setup complete — 'TownyBridge' binding active in KubeJS server scripts.");
    }
}
