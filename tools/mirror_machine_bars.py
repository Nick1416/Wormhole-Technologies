"""Gives the machine block faces matching accent bars on opposite edges.

The machine textures (generators, duplicators) were drawn with one accent bar along the top edge
(row 2 = bar, row 3 = darker shadow, columns 4..11). On a cube that single bar points a different
way on every face. This mirrors the existing bar (same colours, thickness, length):
  * front/back and side faces: top + bottom edge (bar row 13, shadow row 12)
  * top/bottom face: all four edges (left/right bars in columns 2/13, shadows in 3/12)
so every horizontal cube edge is trimmed the same way.

Idempotent: it only copies the bar from rows 2-3, so running it again changes nothing.

Single-texture machines (High Energy Refiner, Relativistic Computer) get a generated
<name>_top.png for their top/bottom faces: the accent element (refiner orange plate, computer
cyan bar) is rotated onto all four edges in the same style; pixels the rotations disagree on
fall back to the face background. The side texture is left untouched and the _top file is
always rebuilt from it, so this is idempotent too.
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


# name -> (accent colours to copy onto every edge, background colour for disputed pixels)
TOP_FROM_SIDE = {
    "high_energy_refiner": ([(180, 100, 40, 255)], (50, 45, 40, 255)),
    "relativistic_computer": ([(80, 200, 220, 255)], (40, 45, 55, 255)),
}


def top_from_side(name, accents, background):
    src = Image.open(BLOCKS + name + ".png").convert("RGBA")
    w, h = src.size
    rots = [src, src.rotate(90), src.rotate(180), src.rotate(270)]
    pxs = [r.load() for r in rots]
    out = Image.new("RGBA", (w, h))
    po = out.load()
    for y in range(h):
        for x in range(w):
            vals = [p[x, y] for p in pxs]
            hit = [v for v in vals if v in accents]
            if hit:
                po[x, y] = hit[0]
            elif all(v == vals[0] for v in vals):
                po[x, y] = vals[0]
            else:
                po[x, y] = background
    out.save(BLOCKS + name + "_top.png")


if __name__ == "__main__":
    for name, (accents, background) in TOP_FROM_SIDE.items():
        top_from_side(name, accents, background)
    for fam in FAMILIES:
        mirror(BLOCKS + fam + ".png", False)
        mirror(BLOCKS + fam + "_side.png", False)
        mirror(BLOCKS + fam + "_top.png", True)
