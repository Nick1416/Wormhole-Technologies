"""Generates the Emerald Drone entity texture (64x32, layout of ModelEmeraldDrone).

Run from the repo root with Pillow installed:  python tools/gen_emerald_drone.py
"""
import random
from PIL import Image

OUT = "src/main/resources/assets/wormholetech/textures/entity/emerald_drone.png"
rnd = random.Random(1416)
im = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
px = im.load()

METAL = (52, 62, 60)
PLATE = (38, 160, 92)
PLATE_HI = (90, 220, 140)
SEAM = (20, 30, 28)
EYE = (120, 255, 170)


def noisy(c, a=10):
    d = rnd.randint(-a, a)
    return tuple(max(0, min(255, v + d)) for v in c) + (255,)


def fill(x0, y0, w, h, c, a=10):
    for y in range(y0, y0 + h):
        for x in range(x0, x0 + w):
            px[x, y] = noisy(c, a)


def box_uv(u, v, w, h, d):
    """Returns (top, bottom, sides) rects for a ModelRenderer box at texture offset u, v."""
    top = (u + d, v, w, d)
    bottom = (u + d + w, v, w, d)
    sides = (u, v + d, 2 * (d + w), h)
    return top, bottom, sides


# body 10x4x12
top, bottom, sides = box_uv(0, 0, 10, 4, 12)
fill(*top, PLATE)
for y in range(top[1], top[1] + top[3]):  # seams across the plates
    for x in range(top[0], top[0] + top[2]):
        if (y - top[1]) % 4 == 3 or x in (top[0], top[0] + top[2] - 1):
            px[x, y] = SEAM + (255,)
        elif (y - top[1]) % 4 == 0:
            px[x, y] = noisy(PLATE_HI, 6)
fill(*bottom, METAL)
fill(*sides, METAL)
for x in range(sides[0], sides[0] + sides[2]):  # emerald stripe and rivets on the hull sides
    px[x, sides[1] + 1] = noisy(PLATE, 6)
    if x % 3 == 0:
        px[x, sides[1] + 3] = (110, 120, 118, 255)

# legs 2x8x2
top, bottom, sides = box_uv(44, 0, 2, 8, 2)
fill(*top, METAL)
fill(*bottom, SEAM)
fill(*sides, METAL)
for x in range(sides[0], sides[0] + sides[2]):
    px[x, sides[1] + 2] = noisy(PLATE, 4)  # knee joint
    px[x, sides[1] + 7] = SEAM + (255,)

# head 6x4x4
top, bottom, sides = box_uv(0, 16, 6, 4, 4)
fill(*top, PLATE)
fill(*bottom, METAL)
fill(*sides, METAL)
for x in range(sides[0], sides[0] + sides[2]):
    px[x, sides[1]] = noisy(PLATE, 6)

# eye 2x2x1
top, bottom, sides = box_uv(20, 16, 2, 2, 1)
for r in (top, bottom, sides):
    fill(*r, EYE, 4)

im.save(OUT)
