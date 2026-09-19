# Wormhole Technologies

Minecraft **1.12.2** / **Forge** mod by **Nick1416**.

Wormhole-era power generation, item duplication, Immersive Engineering mineral tuners, teleport pads, and a late-game **Aetherius** progression line.

Repository: https://github.com/Nick1416/Wormhole-Technologies

## Features

- **Power**: Naquadah Generator → Unstable Wormhole Energy Converter → Wormhole Energy Converter (stable)
- **Duplication**: Wormhole Duplicator / Unstable Wormhole Duplicator (templates without NBT; this mod’s own items cannot be duplicated)
- **Mineral Tuners**: set Immersive Engineering excavator minerals in a chunk (soft dependency on IE)
- **Pads**: linked Wormhole Teleport Pads with RF upkeep; cross-dimension links via Relativistic Computer + Aetherius
- **Late game**: Compressed Naquadah → High Energy Refiner → Aetherius → Relativistic Computer

## Dependencies

| Dependency | Required? | Notes |
|---|---|---|
| Minecraft Forge 1.12.2 | **Required** | Built against 14.23.5.2847 |
| Immersive Engineering | Soft / recommended | Mineral tuners no-op without IE; rest of the mod loads |
| JEI | Soft | Recipe/info integration when present |

`@Mod` declares `after:immersiveengineering;after:jei;` so those mods initialize first when present. They are **not** hard requirements.

## Version 0.1.0 — breaking rename

**0.1.0 renames the modid from `rfgen` to `wormholetech`.** Registry names, assets, lang keys, and the Java package (`com.nick1416.wormholetech`) all changed.

**Existing worlds need a migration or a fresh start.** Items/blocks saved under `rfgen:*` will not map automatically.

## Install

Intended for Compact Claustrophobia–style and similar 1.12.2 modpacks:

1. Install Forge 1.12.2
2. Drop `WormholeTechnologies-0.1.0.jar` into `mods/`
3. Optionally install Immersive Engineering (tuners) and JEI

## Configuration

Forge config file (suggested path from FML): `config/wormholetech.cfg` (exact name follows Forge’s suggested file for the mod).

Configurable RF rates, lifetimes, buffers, and pad/computer upkeep — defaults match the previous hardcoded values.

## Building

```bash
./gradlew build
```

Requires **JDK 8**. Immersive Engineering is `provided` from `libs/` when present; JEI uses the BlameJared/ProgWML Maven coordinates in `build.gradle`. CI builds may succeed without the optional IE jar.

## License

Mod code and assets: **MIT** — see `LICENSE` (Copyright 2026 Nick1416 / Nicky Starr).

The MDK also ships Forge/Paulscode license texts (`LICENSE.txt`, etc.) which apply to those bundled third-party materials, not to this mod’s own code.
