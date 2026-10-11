"""Generates the P3X-1416 block textures: Naquadric Stone, Ashen Regolith, (natural) Naquadah Ore,
Naquadah Crystal and Ancient Stone. Deterministic (fixed seeds).

Run from the repo root with Pillow installed:  python tools/gen_p3x_blocks.py
"""
import random
from PIL import Image

OUT = "src/main/resources/assets/wormholetech/textures/blocks/"


def clamp(v):
    return max(0, min(255, int(v)))


def shade(c, k):
    return (clamp(c[0] * k), clamp(c[1] * k), clamp(c[2] * k), 255)


def blotchy(seed, base, spread, blob=0.35):
    """Stone-like texture: per-pixel noise plus a few darker/lighter blobs (tiles seamlessly)."""
    rnd = random.Random(seed)
    im = Image.new("RGBA", (16, 16))
    px = im.load()
    field = [[rnd.uniform(-1, 1) for _ in range(16)] for _ in range(16)]
    # smooth with wrap-around so blobs tile
    sm = [[0.0] * 16 for _ in range(16)]
    for y in range(16):
        for x in range(16):
            s = 0.0
            for dy in (-1, 0, 1):
                for dx in (-1, 0, 1):
                    s += field[(y + dy) % 16][(x + dx) % 16]
            sm[y][x] = s / 9.0
    for y in range(16):
        for x in range(16):
            k = 1.0 + sm[y][x] * spread * 2.2 + rnd.uniform(-spread, spread) * blob
            px[x, y] = shade(base, k)
    return im


def naquadric_stone():
    return blotchy(1416, (62, 56, 98), 0.30, blob=0.8)


def ashen_regolith():
    im = blotchy(2026, (172, 162, 186), 0.10, blob=0.9)
    px = im.load()
    rnd = random.Random(77)
    for _ in range(14):  # darker grit specks
        x, y = rnd.randrange(16), rnd.randrange(16)
        px[x, y] = shade(px[x, y], 0.78)
    return im


FLECK = [(70, 225, 175, 255), (150, 255, 210, 255), (30, 150, 120, 255)]


ORE_SHAPES = [
    # (origin, cells) - irregular fleck clusters of different sizes; first cell is the bright core
    ((2, 2), [(1, 1), (0, 1), (1, 0), (2, 1), (1, 2)]),
    ((10, 1), [(1, 1), (0, 1), (1, 2)]),
    ((6, 6), [(1, 1), (2, 1), (1, 2), (0, 2), (2, 0), (3, 1)]),
    ((12, 7), [(0, 0), (1, 0), (0, 1)]),
    ((1, 11), [(1, 1), (2, 1), (1, 0), (0, 2), (2, 2)]),
    ((10, 11), [(1, 1), (0, 1), (1, 2), (2, 2)]),
    ((6, 13), [(0, 0)]),
    ((14, 3), [(0, 0)]),
]


def natural_ore():
    im = naquadric_stone()
    px = im.load()
    for (ox, oy), rel in ORE_SHAPES:
        cells = [((ox + x) % 16, (oy + y) % 16) for (x, y) in rel]
        for (x, y) in cells:
            for (dx, dy) in ((-1, 0), (1, 0), (0, -1), (0, 1)):
                q = ((x + dx) % 16, (y + dy) % 16)
                if q not in cells:
                    px[q] = shade(px[q], 0.68)
        for i, (x, y) in enumerate(cells):
            px[x, y] = FLECK[1] if i == 0 else (FLECK[0] if i % 3 else FLECK[2])
    return im


def ancient_stone():
    base = (112, 118, 140)
    im = blotchy(9, base, 0.08, blob=0.6)
    px = im.load()
    mortar = (64, 66, 84, 255)
    hi = shade(base, 1.22)
    for x in range(16):  # two courses of large cut blocks
        px[x, 0] = mortar
        px[x, 8] = mortar
        px[x, 1] = hi
        px[x, 9] = hi
    for y in range(1, 8):
        px[0, y] = mortar
        px[1, y] = hi
    for y in range(9, 16):
        px[8, y] = mortar
        px[9, y] = hi
    # faint teal glyph inlay on the top course (mirrored)
    glyph = [(5, 3), (6, 4), (7, 5), (8, 5), (9, 4), (10, 3), (7, 6), (8, 6)]  # chevron
    for (x, y) in glyph:
        px[x, y] = (70, 160, 168, 255)
    return im


def crystal():
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()
    # three shards (cross model, cutout): centre tall, two leaning
    shards = [((6, 7), 3, 14), ((2, 3), 3, 8), ((10, 11), 3, 10)]
    for (xs, w, h) in shards:
        x0 = xs[0]
        for y in range(16 - h, 16):
            t = (y - (16 - h)) / float(h)
            for dx in range(w):
                x = x0 + dx
                if y == 16 - h and dx == w - 1:
                    continue  # pointed tip
                c = (90, 230, 210, 255) if dx < w - 1 else (40, 170, 170, 255)
                if t < 0.2:
                    c = (210, 255, 245, 255)
                px[x, y] = c
            # dark edge on the right side of each shard
            if x0 + w < 16:
                px[x0 + w, y] = (20, 90, 100, 255) if t > 0.15 else px[x0 + w, y]
    return im


def main():
    naquadric_stone().save(OUT + "naquadric_stone.png")
    ashen_regolith().save(OUT + "ashen_regolith.png")
    natural_ore().save(OUT + "naquadah_ore_natural.png")
    ancient_stone().save(OUT + "ancient_stone.png")
    crystal().save(OUT + "naquadah_crystal.png")


if __name__ == "__main__":
    main()
