"""Generates the stargate textures (Dialing Computer, Ancient Gate Frame, animated portal).

Run from the repo root with Pillow installed:  python tools/gen_stargate_textures.py
Palette matches the other machines (dark slate body, steel trim, naquadah teal accents);
accent bars sit on opposite edges (mirrored), never centred.
"""
import math
from PIL import Image

OUT = "src/main/resources/assets/wormholetech/textures/blocks/"
BG = (40, 45, 55, 255)        # machine body (#282d37)
STEEL = (120, 140, 180, 255)  # trim (#788cb4)
INNER = (55, 65, 85, 255)     # recess (#374155)
TEAL = (80, 200, 220, 255)    # naquadah accent (#50c8dc)
GLOW = (200, 245, 250, 255)
CHEVRON = (230, 140, 60, 255)
CHEVRON_LIT = (255, 190, 110, 255)


def new(c=BG):
    return Image.new("RGBA", (16, 16), c)


def ring(px, r_out, r_in, color, cx=7.5, cy=7.5):
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if r_in <= d < r_out:
                px[x, y] = color


def dialing_computer_side():
    im = new()
    px = im.load()
    ring(px, 6.2, 0, INNER)
    ring(px, 6.2, 4.9, STEEL)
    ring(px, 2.2, 0, TEAL)
    ring(px, 1.2, 0, GLOW)
    # 4 chevrons on the diagonals + mirrored top/bottom pair (symmetric both ways)
    for (x, y) in [(3, 3), (12, 3), (3, 12), (12, 12)]:
        px[x, y] = CHEVRON
    for (x, y) in [(7, 1), (8, 1), (7, 14), (8, 14)]:
        px[x, y] = CHEVRON_LIT
    # accent bars on opposite edges (left/right), mirrored
    for y in range(4, 12):
        px[0, y] = TEAL
        px[15, y] = TEAL
    return im


def dialing_computer_top():
    # Dial-home-device face: ring of glyph keys around a domed centre; bars on all four edges.
    im = new()
    px = im.load()
    ring(px, 6.5, 0, INNER)
    ring(px, 6.5, 5.5, STEEL)
    for k in range(8):
        a = k * math.pi / 4
        x = int(round(7.5 + 3.9 * math.cos(a) - 0.5)) if math.cos(a) < 0 else int(round(7.5 + 3.9 * math.cos(a) + 0.5)) - 1
        y = int(round(7.5 + 3.9 * math.sin(a) - 0.5)) if math.sin(a) < 0 else int(round(7.5 + 3.9 * math.sin(a) + 0.5)) - 1
        px[x, y] = TEAL
        px[15 - x, y] = TEAL
        px[x, 15 - y] = TEAL
        px[15 - x, 15 - y] = TEAL
    ring(px, 2.0, 0, CHEVRON)
    for (x, y) in [(7, 7)]:
        px[x, y] = CHEVRON_LIT
    for i in range(5, 11):
        px[i, 0] = TEAL; px[i, 15] = TEAL; px[0, i] = TEAL; px[15, i] = TEAL
    return im


def ancient_gate_frame():
    base = (150, 165, 172, 255)
    dark = (95, 108, 118, 255)
    light = (196, 208, 214, 255)
    im = new(base)
    px = im.load()
    for i in range(16):
        px[i, 0] = light; px[0, i] = light
        px[i, 15] = dark; px[15, i] = dark
    # engraved glyph band (symmetric) with teal inlay
    glyph = ["..##..##..",
             ".#..##..#.",
             "#..#..#..#",
             ".#..##..#.",
             "..##..##.."]
    for gy, row in enumerate(glyph):
        for gx, ch in enumerate(row):
            if ch == "#":
                px[3 + gx, 5 + gy] = dark if (gx + gy) % 3 else (60, 150, 160, 255)
    for i in range(2, 14):
        px[i, 3] = dark; px[i, 11] = light
    return im


def portal_frames(n=32):
    strip = Image.new("RGBA", (16, 16 * n))
    px = strip.load()
    tau = 2 * math.pi
    for f in range(n):
        t = f / n
        for y in range(16):
            for x in range(16):
                u, v = x / 16.0, y / 16.0
                # periodic in x/y (tiles across blocks) and in t (loops)
                w = (math.sin(tau * (u + 2 * v + t)) +
                     math.sin(tau * (2 * u - v - 2 * t)) * 0.8 +
                     math.sin(tau * (3 * u + 3 * v + t)) * 0.5 +
                     math.sin(tau * (u - 3 * v + 3 * t)) * 0.4)
                k = (w + 2.7) / 5.4
                k = max(0.0, min(1.0, k)) ** 1.4
                r = int(10 + 110 * k)
                g = int(95 + 155 * k)
                b = int(115 + 135 * k)
                a = int(150 + 80 * k)
                px[x, f * 16 + y] = (r, g, b, a)
    return strip


def main():
    dialing_computer_side().save(OUT + "dialing_computer.png")
    dialing_computer_top().save(OUT + "dialing_computer_top.png")
    ancient_gate_frame().save(OUT + "ancient_gate_frame.png")
    portal_frames().save(OUT + "stargate_portal.png")
    with open(OUT + "stargate_portal.png.mcmeta", "w") as f:
        f.write('{ "animation": { "frametime": 2, "interpolate": true } }\n')


if __name__ == "__main__":
    main()
