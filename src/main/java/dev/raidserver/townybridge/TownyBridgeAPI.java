package dev.raidserver.townybridge;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The API object injected into KubeJS script bindings as {@code TownyBridge}.
 * <p>
 * All methods are plain Java — no reflection, no Class.forName, no getClass().
 * KubeJS's class-filter sandbox only runs inside JS; this code is trusted mod code.
 * <p>
 * {@code getPlayersTown} now accepts {@link ServerPlayer} directly — no reflection
 * needed, since mod code is not subject to the KubeJS sandbox.  Graal/Polyglot
 * passes the Java object through transparently regardless of parameter type.
 */
public class TownyBridgeAPI {

    // ── Town lookup ───────────────────────────────────────────────────────────

    /**
     * Returns the Town whose name matches (case-insensitive), or {@code null}.
     */
    public Town getTownByName(String name) {
        if (name == null) return null;
        String lower = name.toLowerCase();
        for (Town t : TownyUniverse.getInstance().getTowns()) {
            if (t.getName().toLowerCase().equals(lower)) return t;
        }
        return null;
    }

    /**
     * Returns all registered town names (for tab-completion in scripts).
     */
    public List<String> getAllTownNames() {
        Collection<Town> towns = TownyUniverse.getInstance().getTowns();
        List<String> names = new ArrayList<>(towns.size());
        for (Town t : towns) names.add(t.getName());
        return names;
    }

    // ── Player → Town ─────────────────────────────────────────────────────────

    /**
     * Resolves the Town of a NeoForge {@link ServerPlayer} (passed from KubeJS).
     * Internally looks up the matching Bukkit Player by UUID — Mohist keeps them
     * in sync, so this is always safe on a running Mohist server.
     *
     * @param player  the KubeJS / NeoForge {@code ServerPlayer} instance
     * @return the player's Town, or {@code null} if they have none / are offline
     */
    public Town getPlayersTown(ServerPlayer player) {
        if (player == null) return null;
        try {
            UUID uuid = player.getUUID();
            Player bukkit = Bukkit.getPlayer(uuid);
            if (bukkit == null) return null;
            Resident resident = TownyAPI.getInstance().getResident(bukkit);
            if (resident == null) return null;
            return TownyAPI.getInstance().getResidentTownOrNull(resident);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Town membership ───────────────────────────────────────────────────────

    /**
     * Returns all online Bukkit {@link Player} objects belonging to {@code town}.
     */
    public List<Player> getOnlineTownMembers(Town town) {
        if (town == null) return List.of();
        return TownyAPI.getInstance().getOnlinePlayers(town);
    }

    // ── PVP helpers ───────────────────────────────────────────────────────────

    /**
     * Reads the current PVP flag of a town.
     */
    public boolean isTownPVP(Town town) {
        return town != null && town.isPVP();
    }

    /**
     * Sets the town's PVP flag and persists it.
     * Returns the <em>old</em> value so the script can restore it later.
     */
    public boolean setTownPVP(Town town, boolean value) {
        if (town == null) return false;
        boolean old = town.isPVP();
        town.setPVP(value);
        town.save();
        return old;
    }

    /**
     * Restores the PVP flag to a previously-saved value and persists it.
     */
    public void restoreTownPVP(Town town, boolean savedValue) {
        if (town == null) return;
        town.setPVP(savedValue);
        town.save();
    }

    // ── Messaging ─────────────────────────────────────────────────────────────

    /**
     * Sends a raw (already-formatted) message to every online member of {@code town}.
     */
    public void broadcastToTown(Town town, String message) {
        if (town == null || message == null) return;
        for (Player p : getOnlineTownMembers(town)) {
            p.sendMessage(message);
        }
    }
}
