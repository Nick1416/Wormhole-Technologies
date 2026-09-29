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
| Compact Machines 3 | Soft / recommended | Quantum Circuit miniaturization recipe (shown in CM3's JEI category); without CM3 a crafting-table fallback recipe is enabled |

`@Mod` declares `after:immersiveengineering;after:jei;after:compactmachines3;` so those mods initialize first when present. They are **not** hard requirements.

## Version 0.1.0 — breaking rename

**0.1.0 renames the modid from `rfgen` to `wormholetech`.** Registry names, assets, lang keys, and the Java package (`com.nick1416.wormholetech`) all changed.

**Existing worlds need a migration or a fresh start.** Items/blocks saved under `rfgen:*` will not map automatically.

## Prysmian tiers

| Tier | Block | Recipe | Used by |
|---|---|---|---|
| Base | Block of Prysmian | 4 Prysmian (2x2) | Unstable Naquadah Reactor |
| Advanced | Prysmian Logic Frame | 4 Prysmian + 5 Quantum Circuit | Naquadah Reactor |

The Unstable Block of Prysmian is no longer craftable or used by any recipe; it stays registered so existing blocks don't vanish from worlds. Quantum Glue is still craftable but no longer used by the Prysmian recipes.

### Quantum Circuit (Compact Machines 3 miniaturization)

Build a 5x5x5 structure inside a Compact Machines field projector and throw in **Refined Naquadah** as the catalyst → **2 Quantum Circuits**. Every block is stone (it looks like a solid stone cube) except the inner 3x3 of the middle layer:

```
D C D      C = redstone comparator   T = redstone torch
R D R      R = redstone repeater     D = redstone dust
D T D
```

That is 5 redstone dust, 2 repeaters, 1 comparator and 1 torch. Repeater/comparator facing, delay and mode don't matter, and the layout works in any of the 4 horizontal orientations. Repeaters change block (powered/unpowered) depending on their input, so hidden power-state variants of the recipe are registered alongside the main one; JEI only shows the single main recipe.

The recipe ships inside the Wormhole jar (`assets/wormholetech/compactmachines3/recipes/quantum_circuit.json`) and is registered into Compact Machines 3 at startup, so it appears in CM3's JEI miniaturization category. Pack makers can override it by placing a recipe with the same `"name"` (`wormholetech:quantum_circuit`) in `config/compactmachines3/recipes/` (this also disables the hidden power-state variants).

Without Compact Machines 3 installed, a shaped crafting-table fallback is enabled instead → 2 Quantum Circuits:

```
D C D      D = redstone   C = comparator   R = repeater
R N R      T = redstone torch   N = Refined Naquadah
D T D
```

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
