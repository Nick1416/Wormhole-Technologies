"""Generates textures/blocks/refined_naquadah_block.png from the Refined Naquadah item palette.

Vanilla storage-block look: light top/left edge, darker bottom/right edge, a seam across the
middle (stacked ingots) and a little deterministic noise. Run from the repo root:
    python tools/gen_refined_naquadah_block.py
"""
import random
from PIL import Image

OUT = "src/main/resources/assets/wormholetech/textures/blocks/refined_naquadah_block.png"
# palette taken from textures/items/refined_naquadah.png
WHITE = (255, 255, 255)
PALE = (204, 204, 255)
ICE = (199, 231, 254)
SKY = (152, 209, 249)
LILAC = (163, 160, 255)
VIOLET = (127, 124, 255)


def mix(a, b, t):
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))


def main():
    rnd = random.Random(1416)
    im = Image.new("RGBA", (16, 16))
    px = im.load()
    for y in range(16):
        for x in range(16):
            # diagonal light: top-left ICE -> bottom-right LILAC, with noise
            t = (x + y) / 30.0 + rnd.uniform(-0.12, 0.12)
            c = mix(ICE, LILAC, max(0.0, min(1.0, t)))
            if rnd.random() < 0.12:
                c = mix(c, SKY, 0.6)
            px[x, y] = c + (255,)
    for i in range(16):
        px[i, 0] = PALE + (255,)
        px[0, i] = PALE + (255,)
        px[i, 15] = VIOLET + (255,)
        px[15, i] = VIOLET + (255,)
    px[0, 15] = mix(PALE, VIOLET, 0.5) + (255,)
    px[15, 0] = mix(PALE, VIOLET, 0.5) + (255,)
    # seam between the two "ingot" rows
    for x in range(1, 15):
        px[x, 7] = mix(LILAC, VIOLET, 0.6) + (255,)
        px[x, 8] = mix(PALE, WHITE, 0.4) + (255,)
    # specular glints
    for (x, y) in [(2, 2), (3, 2), (2, 3), (9, 10), (10, 10), (9, 11)]:
        px[x, y] = WHITE + (255,)
    im.save(OUT)


if __name__ == "__main__":
    main()
