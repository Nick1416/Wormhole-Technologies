# Wormhole Technologies

Minecraft **1.12.2** / **Forge** mod by **Nick1416**.

Wormhole-era power generation, item duplication, Immersive Engineering mineral tuners, teleport pads, and a late-game **Aetherius** progression line.

Repository: https://github.com/Nick1416/Wormhole-Technologies

## Features

- **Power**: Naquadah Generator → Wormhole Energy Converter (stable, needs a wormhole connection)
- **Duplication** (`mode=cc` only): Wormhole Duplicator / Unstable Wormhole Duplicator (templates without NBT; this mod’s own items cannot be duplicated)
- **Wormhole connection**: the Duplicators and the Energy Converter only run while connected (see below)
- **Mineral Tuners**: set Immersive Engineering excavator minerals in a chunk (soft dependency on IE)
- **Pads**: linked Wormhole Teleport Pads with RF upkeep. Pads only link within the same dimension (the Stargate is the way between worlds); old cross-dimension links stop working. The recipe needs a Naquadah Crystal from P3X-1416 (Silk Touch), so pads come after the Stargate
- **Late game**: Compressed Naquadah → High Energy Refiner (30 min) → Aetherius → Relativistic Computer
- **Quantum clock**: the Relativistic Computer GUI has a clock toggle. **+1** makes local time race: it is required for the Dialing Computer/Stargate, and the seated Aetherius wears down (durability bar) over 30 minutes (`aetheriusDecayTicks`, 36,000 ticks); at zero it shatters and is lost. **-1** slows time: Aetherius recovers at the same rate (a nearly spent one takes 30 minutes, less if it is less worn). The clock only runs while the computer is powered with Aetherius seated, and only Aetherius **in the computer's slot** is affected; Aetherius anywhere else (inventories, chests, a refiner) never changes. New computers start at -1, so existing gates need the clock switched to +1.
- **Automation**: Wormhole Duplicator, Unstable Wormhole Duplicator and Wormhole Energy Converter accept Block of Refined Naquadah from hoppers/pipes/itemducts on any side (fuel slot takes only valid fuel; the duplicators' nugget/template slots still accept their own items) and only let products and decayed Unstable Blocks of Naquadah be extracted.
- **Stargate**: titanium gate + Dialing Computer → the P3X-1416 dimension (see below)

## Dependencies

| Dependency | Required? | Notes |
|---|---|---|
| Minecraft Forge 1.12.2 | **Required** | Built against 14.23.5.2847 |
| Immersive Engineering | Soft / recommended | Mineral tuners no-op without IE; rest of the mod loads |
| JEI | Soft | Recipe/info integration when present |
| Galacticraft (Planets) | Soft / recommended | Its Titanium Block (`galacticraftplanets:asteroids_block` meta 7) builds the gate frame. Without it, the mod's own Block of Titanium (Titanium Ingots refined from oredict `itemSilicon`, e.g. AE2 silicon) or any oredict `blockTitanium` works; with no titanium and no silicon, the Reinforced Gate Frame recipe is enabled |
| Compact Machines 3 | Soft / recommended | Quantum Circuit miniaturization recipe (shown in CM3's JEI category); without CM3 a crafting-table fallback recipe is enabled |

`@Mod` declares `after:immersiveengineering;after:jei;after:compactmachines3;after:galacticraftplanets;` so those mods initialize first when present. They are **not** hard requirements.

## Version 0.1.0 — breaking rename

**0.1.0 renames the modid from `rfgen` to `wormholetech`.** Registry names, assets, lang keys, and the Java package (`com.nick1416.wormholetech`) all changed.

**Existing worlds need a migration or a fresh start.** Items/blocks saved under `rfgen:*` will not map automatically.

## Wormhole connection

The Wormhole Duplicator, Unstable Wormhole Duplicator and Wormhole Energy Converter only work while connected to a wormhole.

- **Fuel**: Block of Refined Naquadah (`wormholetech:refined_naquadah_block`, 9 Refined Naquadah in a 3x3).
- Put blocks in the machine's wormhole slot and press **Establish connection to a wormhole** in its GUI (the button is grayed out without a block).
- While connected, the first block decays over `refinedNaquadahBlockDecayTicks` game ticks (config `wormhole_connection`, default 12000 = ~10 minutes). It then becomes an Unstable Block of Naquadah in the output slot and the next block starts. The connection ends when the last block is used up.
- If the Unstable output slot is full, the finished block waits there and the machine pauses until there is room.
- Taking the decaying block out ends the connection; the block keeps its progress in NBT (shown in its tooltip, it does not stack) and continues where it left off when put back into an empty wormhole slot.
- **Unstable Wormhole Duplicator**: single use. It holds a single block and has no Unstable output. Once connected it performs exactly one duplication (1 diamond + 1 template → 2), then immediately drops its contents (including the output) and collapses into an Unstable Block of Naquadah; the Refined Naquadah block is consumed. If the block decays before a duplication happens, it collapses the same way.
- Breaking a machine keeps its inventory and connection state in the dropped item.

## Stargate and P3X-1416

- **Frame**: a Nether-portal-style rectangle of titanium blocks, at least 4 wide and 5 tall (outer), up to 23x23. Corners are optional, the inside must be empty. Accepted blocks: Galacticraft's Titanium Block when Galacticraft Planets is installed, plus anything registered as oredict `blockTitanium` (including this mod's Block of Titanium), plus the mod's own Reinforced Gate Frame (its recipe only appears when no titanium source exists). Galacticraft is never required at compile or run time.
- **Titanium**: Titanium Ingot (oredict `ingotTitanium`) comes from the High Energy Refiner: 1 silicon (`itemSilicon`) -> 1 ingot in 200 ticks (`highEnergyRefinerTitaniumTicks`). 9 ingots <-> 1 Block of Titanium (`blockTitanium`), a gate frame block.
- **Fallbacks** (Forge condition `wormholetech:fallback`): without Thermal Foundation or any other `nuggetDiamond`, the mod adds a Diamond Nugget (1 diamond <-> 9 nuggets, oredict `nuggetDiamond`); without Galacticraft Planets, any other `blockTitanium` and any `itemSilicon`, it adds the Reinforced Gate Frame recipe (INI/NON/INI: iron ingot, Refined Naquadah, obsidian -> 2).
- **Dialing Computer**: place it touching a frame block and next to an **active** Relativistic Computer with its quantum clock at **+1**, give it RF (default half the computer's upkeep, 25,000 RF/t while open) and put an Ancient Tablet (the address) in its slot. Press **Dial**. The status line explains what is missing.
- The wormhole collapses when the tablet is removed, the power runs out, the frame breaks or the computer stops. Conditions are checked every second and on neighbour changes; after a collapse you have to dial again.
- **Travel**: walk into the wormhole (players only). On P3X-1416 you arrive on a small platform in front of a return gate built from Ancient Gate Frame blocks. The return gate is always open and takes you back to the gate you came from. It is built once, near (0, 0), and reused (and repaired) on later trips.
- **Getting home**: the return gate (Ancient Gate Frame and its portal) is unbreakable like bedrock: it survives explosions, pistons, withers and dragons, and it never drops an item, so the frame block can't be obtained in survival. It still repairs itself on every arrival as a safety net. Dying on P3X-1416 respawns you in the Overworld, at your bed/spawn point if it is valid there, otherwise at world spawn.
- **Gate Locator** (compass + iron + Refined Naquadah + Quantum Circuit; crafted only, never given automatically): works like a compass. On P3X-1416 it points to the return gate; in other dimensions it points to the origin gate you last left from, and spins if there is none in that dimension. Right-click shows the distance and direction, e.g. "Return gate: 132 blocks NE".
- **P3X-1416** (`p3xDimensionId`, default -1416): an alien world with one biome, Naquadah Wastes. Steep hills of dark blue-violet **Naquadric Stone** under pale **Cinder Soil**, a violet sky with rose-amber fog and teal sunsets, teal water, rock spires, the occasional floating rock, glowing **Naquadah Crystals** on the surface and in caves, **Ancient Stone** obelisks and ruins, and small temples everywhere in newly generated chunks, about 1 in 60 chunks (open shrine, closed sanctum or stepped pyramid, each with bookcases inside; about half hold a chest with modest loot). Vanilla hostile mobs spawn at night and in caves as usual, plus the occasional **Emerald Drone**: a small hostile robot that spawns only on the open surface (day or night, alone or in pairs), fights in melee and drops 0-2 emeralds (Looting adds more). There is a spawn egg. Terrain and features near (0, 0) are kept clear so the return gate platform stays safe. Changes only apply to newly generated chunks: chunks that already exist keep the old look.
- **Naquadah Ore** (natural, oredict `oreNaquadah`): generates only on P3X-1416 (about 11 veins of 4-8 per chunk, y 5-80, plus small exposed clusters on the surface). Needs an iron pickaxe or better. It drops itself; smelt it in a vanilla furnace for Refined Naquadah (shown in JEI). This is separate from the craftable **Synthetic Naquadah Ore**, which still works as before. Vanilla ores do not generate on P3X-1416. Naquadah Crystals drop nothing unless mined with Silk Touch; they are needed for Wormhole Teleport Pads.
- **P3X-1416 hazards** (newly generated chunks for the worldgen parts):
  - **Dust storms**: P3X has its own weather and never rains (overworld rain no longer reaches it). Roughly every 1.5 days (18000-54000 ticks of clear sky) a storm blows for 3000-9000 ticks: dense rose-amber haze closes visibility to about 28 blocks and dust drifts on the wind. During a storm Emerald Drones see farther (follow range 24 to 40) and move 20% faster.
  - **Crystal caves**: about 1 in 80 chunks hide a sealed pocket (Y 14-38) lined with Naquadric Stone and floored with Naquadah Crystals.
  - **Night**: crystals sparkle more and exposed surface ore glints. Every minute at night each player (survival/adventure) has a 1 in 2 chance of an Emerald Drone patrol of 2-4 arriving 24-40 blocks away and hunting them, capped at 6 drones within 48 blocks. No patrols by day.
  - **Guardian temples**: about 1 in 400 chunks, a 13x13 Ancient Stone hall with a loot chest on a dais (diamonds, emeralds, treasure enchanted books, Refined Naquadah...) guarded by an **Emerald Sentinel**: a large near-black Emerald Drone with 120 HP, 10 armour and 8 damage, a boss health bar, that never despawns and drops 4-8 emeralds and 2-4 Refined Naquadah. Defeating one grants the "Guardian Fallen" challenge. Its boss bar only shows within 32 blocks of a living Sentinel (a guardian in another temple no longer shows one).
  - **Prysmian Ore**: deep ore at vanilla diamond rarity (1 vein of up to 8 per chunk, Y 1-16, in Naquadric Stone). Iron pickaxe; drops Prysmian (Fortune adds more, Silk Touch gives the ore). Oredict `orePrysmian`, and Prysmian is `gemPrysmian`. Config `p3xPrysmianOreVeinsPerChunk`, `p3xPrysmianOreVeinSize`, `p3xPrysmianOreMaxY`.
- Config category `stargate`: `stargateEnabled`, `p3xDimensionId`, `dialingComputerRfPerTick` (-1 = half the Relativistic Computer upkeep), buffer/receive, the P3X ore settings (`p3xNaquadahOreVeinsPerChunk`, `p3xNaquadahOreVeinSizeMin`/`p3xNaquadahOreVeinSize`, `p3xNaquadahOreMinY`/`MaxY`, `p3xSurfaceOreClusterChance`) and feature frequency (`p3xCrystalsPerChunk`, `p3xSpireChance`, `p3xObeliskChance`, `p3xTempleChance`, `p3xTempleChestChance`; chances are 1 in N chunks, 0 disables), and `emeraldDroneSpawnWeight` (default 50, groups of 1-2; zombies are 100). Hazards: `p3xDustStorms` (on/off), `p3xDustStormClearTicks` (avg 36000) / `p3xDustStormDurationTicks` (avg 6000), `p3xCrystalCaveChance` (80), `p3xNightPatrolIntervalTicks` (1200, 0 = off), `p3xGuardianTempleChance` (400).

## Prysmian tiers

| Tier | Block | Recipe | Used by |
|---|---|---|---|
| Base | Block of Prysmian | 4 Prysmian (2x2) | Unstable Naquadah Reactor |
| Advanced | Prysmian Logic Frame | 4 Prysmian + 5 Quantum Circuit | Naquadah Reactor |

The Unstable Block of Prysmian is no longer craftable or used by any recipe; it stays registered so existing blocks don't vanish from worlds.

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

### Mode (`general` → `mode`)

`S:mode=normal` (default) or `S:mode=cc`. There is no auto mode. It is read once at startup, so changing it needs a restart.

- **cc** (Compact Claustrophobia): everything, including the Wormhole Duplicator and Unstable Wormhole Duplicator.
- **normal**: the two duplicators are taken out of gameplay. Their blocks stay registered so existing worlds load safely, but:
  - their crafting recipes are not loaded, and JEI hides them along with the Wormhole Duplication category;
  - the tablet fragment about them is skipped (later fragments are renumbered);
  - the "Echo Chamber" advancement is removed and "High Energy Refiner" follows the Energy Converter instead;
  - placed duplicators are inert: no ticking, no GUI (right-click shows a notice), no hopper/pipe access; breaking one drops the block plus its contents as loose items.

Nothing else in the progression needs a duplicator. The server sends its mode to each client on login; a client with a different mode is disconnected with a message saying which mode to set.

## Building

```bash
./gradlew build
```

Requires **JDK 8**. Immersive Engineering is `provided` from `libs/` when present; JEI uses the BlameJared/ProgWML Maven coordinates in `build.gradle`. CI builds may succeed without the optional IE jar.

## License

Mod code and assets: **MIT** — see `LICENSE` (Copyright 2026 Nick1416 / Nicky Starr).

The MDK also ships Forge/Paulscode license texts (`LICENSE.txt`, etc.) which apply to those bundled third-party materials, not to this mod’s own code.

## Derelict Vessel (second gate address)

- **Address**: defeating an Emerald Sentinel drops **Derelict Coordinates** (guardian temple chests have a 50% chance too). Put them in the Dialing Computer instead of the Ancient Tablet; the gate then opens to the derelict vessel (dimension id `derelictDimensionId`, default -1417). Same gate rules as P3X-1416; you arrive at an always-open return gate in the ship's hub room (chunk 0,0).
- **The ship**: permanent night under a star field, no weather. One endless deck (y 64-70) of 3-wide corridors and rooms between two hull layers, floating in the void. Hull Plating, Bulkheads, Hull Lamps, Viewport Glass and Modulator Cradles are unbreakable (bedrock-like, blast-proof, immovable), so the maze can't be tunnelled and nobody gets out except through the gate. Generated per chunk from hashed coordinates (no cascading worldgen).
- **Crawlers**: Emerald Drones and **Emerald Wisps** (small flying drones with red eyes, 10 HP, 3 damage, dive attacks; drop iron nuggets/redstone, rarely Raw Naquadah) spawn on the deck, plus an extra spawner (`derelictSpawnIntervalTicks`, `derelictCrawlerCap`). No spawns in the hub room.
- **Rooms**: 1 in `derelictRoomChance` (7) chunks is an open room, half of them with a loot chest. 1 in `derelictModulatorRoomChance` (770, about nether fortress rarity) chunks is a **modulator room**: a **Quantum Field Modulator** locked in a Modulator Cradle, guarded by a **Giant Emerald Sentinel** (300 HP, 16 armour, 14 damage, boss bar within 32 blocks, never despawns, rich drops).
- **Cradle mechanic**: the Modulator is unbreakable while locked. The Giant Sentinel's death releases every Modulator within 24 blocks (a random tick also releases it if no living Giant Sentinel is within 24 blocks); then it can be mined and drops itself. It can't be crafted and has no GUI or item I/O (its use is a TODO). Advancement: "Contained Energy".
