# TownyBridge

Server-only companion mod for **KubeJS + Towny on Mohist** (NeoForge 1.21.1).

Exposes a narrow, typed `TownyBridge` binding into KubeJS server scripts, bypassing
KubeJS's reflection-primitive class filter entirely.  Your scripts call
`TownyBridge.getTownByName(...)` — zero `Java.loadClass`, zero `getClass()`, zero
reflection in JS at all.

## Building

Requires **JDK 21**.

```bash
./gradlew build
```

Output jar: `build/libs/townybridge-1.0.0.jar`

Or push to GitHub — the Actions workflow builds it automatically and uploads the jar
as a workflow artifact.

## Installation

1. Drop `townybridge-1.0.0.jar` into your Mohist server's `mods/` folder.
2. Ensure `kubejs` and `towny` are also present (KubeJS as a NeoForge mod, Towny as a Bukkit plugin).
3. Place `raid.js` (or any script using `TownyBridge`) in `kubejs/server_scripts/`.

## API surface

```js
// All available from server scripts as TownyBridge.*

TownyBridge.getTownByName(name)          // → Town | null
TownyBridge.getAllTownNames()            // → List<String>
TownyBridge.getPlayersTown(player)       // → Town | null   (pass the KubeJS player object)
TownyBridge.getOnlineTownMembers(town)   // → List<Player>
TownyBridge.isTownPVP(town)             // → boolean
TownyBridge.setTownPVP(town, value)     // → boolean (returns OLD value)
TownyBridge.restoreTownPVP(town, saved) // → void
TownyBridge.broadcastToTown(town, msg)  // → void  (msg already color-coded)
```

## How it works

KubeJS's class filter blocks `Java.loadClass` for Bukkit/Towny classes by design.
This mod runs as **trusted NeoForge mod code** (outside the sandbox), does all the
Towny/Bukkit interop there, and injects one named object into KubeJS bindings via
the `KubeJSPlugin` service-loader mechanism.  The JS side never touches a class
reference directly.
