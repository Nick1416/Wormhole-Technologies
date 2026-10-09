"""Gives the machine block faces matching accent bars on opposite edges.

The machine textures (generators, duplicators) were drawn with one accent bar along the top edge
(row 2 = bar, row 3 = darker shadow, columns 4..11). On a cube that single bar points a different
way on every face. This mirrors the existing bar (same colours, thickness, length):
  * front/back and side faces: top + bottom edge (bar row 13, shadow row 12)
  * top/bottom face: all four edges (left/right bars in columns 2/13, shadows in 3/12)
so every horizontal cube edge is trimmed the same way.

Idempotent: it only copies the bar from rows 2-3, so running it again changes nothing.
Run from the repo root:  python tools/mirror_machine_bars.py
"""
from PIL import Image

BLOCKS = "src/main/resources/assets/wormholetech/textures/blocks/"
FAMILIES = ["naquadah_generator", "rf_generator", "wormhole_duplicator", "unstable_wormhole_duplicator"]
X0, X1 = 4, 11  # bar span (inclusive)


def mirror(path, four_edges):
    im = Image.open(path).convert("RGBA")
    px = im.load()
    bar = [px[x, 2] for x in range(X0, X1 + 1)]
    shade = [px[x, 3] for x in range(X0, X1 + 1)]
    for i, x in enumerate(range(X0, X1 + 1)):
        px[x, 13] = bar[i]
        px[x, 12] = shade[i]
    if four_edges:
        for i, y in enumerate(range(X0, X1 + 1)):
            px[2, y] = bar[i]
            px[3, y] = shade[i]
            px[13, y] = bar[i]
            px[12, y] = shade[i]
    im.save(path)


if __name__ == "__main__":
    for fam in FAMILIES:
        mirror(BLOCKS + fam + ".png", False)
        mirror(BLOCKS + fam + "_side.png", False)
        mirror(BLOCKS + fam + "_top.png", True)
