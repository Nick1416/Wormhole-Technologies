"""Regenerates the gray machine GUI backgrounds so the drawn slot boxes match the container
slot coordinates exactly (box at item_x-1, item_y-1, vanilla 18x18 style).

Run from the repo root with Pillow installed:  python tools/gen_machine_guis.py
Keep the coordinates here in sync with ContainerHighEnergyRefiner / ContainerRelativisticComputer.
"""
from PIL import Image

OUT = "src/main/resources/assets/wormholetech/textures/gui/"
FILL = (198, 198, 198, 255)
DARK = (55, 55, 55, 255)
MID = (139, 139, 139, 255)
WHITE = (255, 255, 255, 255)
BLACK = (0, 0, 0, 255)
SHADE = (85, 85, 85, 255)
W = 176


def panel(h):
    im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    px = im.load()
    for y in range(h):
        for x in range(W):
            px[x, y] = FILL
    # outer black outline with rounded corners (vanilla shape)
    for x in range(2, W - 3):
        px[x, 0] = BLACK
    for x in range(3, W - 2):
        px[x, h - 1] = BLACK
    for y in range(2, h - 3):
        px[0, y] = BLACK
    for y in range(3, h - 2):
        px[W - 1, y] = BLACK
    px[1, 1] = BLACK; px[W - 3, 1] = BLACK; px[W - 2, 2] = BLACK
    px[1, h - 3] = BLACK; px[2, h - 2] = BLACK; px[W - 2, h - 2] = BLACK
    # white bevel (top/left), dark bevel (bottom/right)
    for x in range(2, W - 3):
        px[x, 1] = WHITE
    for x in range(1, W - 3):
        px[x, 2] = WHITE
    for y in range(2, h - 3):
        px[1, y] = WHITE; px[2, y] = WHITE
    px[3, 3] = WHITE
    for y in range(3, h - 2):
        px[W - 3, y] = SHADE; px[W - 2, y] = SHADE
    for x in range(3, W - 2):
        px[x, h - 3] = SHADE; px[x, h - 2] = SHADE
    px[W - 4, h - 4] = SHADE
    px[W - 3, 2] = FILL; px[2, h - 3] = FILL
    # clear outside the rounded corners
    for (x, y) in [(0, 0), (1, 0), (0, 1), (W - 1, 0), (W - 2, 0), (W - 3, 0), (W - 1, 1), (W - 2, 1), (W - 1, 2),
                   (0, h - 1), (1, h - 1), (2, h - 1), (0, h - 2), (1, h - 2), (0, h - 3),
                   (W - 1, h - 1), (W - 2, h - 1), (W - 1, h - 2)]:
        px[x, y] = (0, 0, 0, 0)
    return im


def slot(im, item_x, item_y):
    px = im.load()
    bx, by = item_x - 1, item_y - 1
    for i in range(18):
        for j in range(18):
            px[bx + i, by + j] = MID
    for i in range(17):
        px[bx + i, by] = DARK
        px[bx, by + i] = DARK
        px[bx + 1 + i, by + 17] = WHITE
        px[bx + 17, by + 1 + i] = WHITE


def player_inv(im, top):
    for r in range(3):
        for c in range(9):
            slot(im, 8 + c * 18, top + r * 18)
    for c in range(9):
        slot(im, 8 + c * 18, top + 58)


def arrow_mask():
    """24x16 right-pointing arrow."""
    m = set()
    for x in range(1, 15):
        for y in range(6, 10):
            m.add((x, y))
    for k in range(8):
        for y in range(k, 16 - k):
            m.add((15 + k, y))
    return m


def draw_arrow(im, ox, oy, fill, edge=None):
    px = im.load()
    m = arrow_mask()
    for (x, y) in m:
        c = fill
        if edge is not None and any((x + dx, y + dy) not in m for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
            c = edge
        px[ox + x, oy + y] = c


def refiner():
    h = 180
    im = panel(h)
    slot(im, 56, 36)    # input
    slot(im, 116, 36)   # output
    draw_arrow(im, 79, 36, MID)                                          # empty arrow (background)
    draw_arrow(im, 176, 0, (96, 210, 120, 255), (38, 128, 64, 255))      # filled sprite
    draw_arrow(im, 176, 16, (170, 255, 190, 170))                        # moving highlight sprite
    player_inv(im, h - 82)
    im.save(OUT + "high_energy_refiner.png")


def computer():
    h = 166
    im = panel(h)
    slot(im, 80, 36)
    player_inv(im, h - 82)
    im.save(OUT + "relativistic_computer.png")


if __name__ == "__main__":
    refiner()
    computer()
