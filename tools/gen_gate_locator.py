"""Generates the Gate Locator textures and item models (32 needle frames, like the vanilla compass).

Run from the repo root with Pillow installed:  python tools/gen_gate_locator.py
Frame 16 points straight up (target ahead); frame 16+k is rotated k * 11.25 degrees clockwise.
The model override thresholds match vanilla's compass.json.
"""
import json
import math
from PIL import Image

TEX = "src/main/resources/assets/wormholetech/textures/items/"
MOD = "src/main/resources/assets/wormholetech/models/item/"
CLEAR = (0, 0, 0, 0)
OUTLINE = (40, 44, 52, 255)
RIM = (150, 165, 172, 255)
RIM_HI = (196, 208, 214, 255)
FACE = (42, 51, 64, 255)
TICK = (96, 112, 130, 255)
NEEDLE = (80, 200, 220, 255)
NEEDLE_HI = (200, 245, 250, 255)
TAIL = (230, 140, 60, 255)
HUB = (230, 236, 240, 255)
CX = CY = 7.5


def body():
    im = Image.new("RGBA", (16, 16), CLEAR)
    px = im.load()
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - CX, y - CY)
            if d < 5.6:
                px[x, y] = FACE
            elif d < 6.6:
                px[x, y] = RIM_HI if (x - CX) + (y - CY) < 0 else RIM
            elif d < 7.6:
                px[x, y] = OUTLINE
    for (x, y) in [(7, 1), (8, 1), (7, 14), (8, 14), (1, 7), (1, 8), (14, 7), (14, 8)]:
        px[x, y] = NEEDLE  # teal studs on the four cardinal points of the rim
    return im


def frame(i):
    im = body()
    px = im.load()
    a = math.radians((i - 16) * 11.25)
    ux, uy = math.sin(a), -math.cos(a)  # clockwise from up, screen y grows downward
    # tail (short, orange)
    for t in [x * 0.25 for x in range(1, 11)]:
        x, y = int(round(CX - 0.5 - ux * t + 0.5)), int(round(CY - 0.5 - uy * t + 0.5))
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = TAIL
    # needle (teal, bright tip)
    for t in [x * 0.25 for x in range(0, 21)]:
        x, y = int(round(CX - 0.5 + ux * t + 0.5)), int(round(CY - 0.5 + uy * t + 0.5))
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = NEEDLE_HI if t >= 4.0 else NEEDLE
    px[7, 7] = HUB if px[7, 7] != NEEDLE_HI else px[7, 7]
    return im


def main():
    for i in range(32):
        frame(i).save(TEX + "gate_locator_%02d.png" % i)
        with open(MOD + "gate_locator_%02d.json" % i, "w") as f:
            json.dump({"parent": "item/generated", "textures": {"layer0": "wormholetech:items/gate_locator_%02d" % i}}, f)
            f.write("\n")
    overrides = [{"predicate": {"angle": 0.0}, "model": "wormholetech:item/gate_locator_16"}]
    for k in range(1, 32):
        overrides.append({"predicate": {"angle": round((2 * k - 1) / 64.0, 6)},
                          "model": "wormholetech:item/gate_locator_%02d" % ((16 + k) % 32)})
    overrides.append({"predicate": {"angle": round(63 / 64.0, 6)}, "model": "wormholetech:item/gate_locator_16"})
    with open(MOD + "gate_locator.json", "w") as f:
        json.dump({"parent": "item/generated", "textures": {"layer0": "wormholetech:items/gate_locator_16"},
                   "overrides": overrides}, f, indent=2)
        f.write("\n")


if __name__ == "__main__":
    main()
