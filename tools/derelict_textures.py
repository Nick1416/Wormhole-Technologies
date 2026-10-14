"""Generates the derelict vessel textures (hull, window, lamp, cradle, modulator, wisp, giant sentinel, coordinates)."""
import random, os, sys
from PIL import Image
A = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources', 'assets', 'wormholetech', 'textures')
R = random.Random(1417)

def clamp(v): return max(0, min(255, int(v)))
def jitter(c, n=8): return tuple(clamp(x + R.randint(-n, n)) for x in c[:3]) + ((c[3],) if len(c) > 3 else (255,))

def hull_plating():
    im = Image.new('RGBA', (16, 16)); px = im.load()
    for x in range(16):
        for y in range(16):
            base = (78, 82, 90)
            if x in (0, 15) or y in (0, 15): base = (52, 55, 61)
            elif x in (1,) or y in (1,): base = (98, 102, 110)
            if y == 8 and 1 <= x <= 14: base = (60, 63, 70)
            px[x, y] = jitter(base, 5)
    for (x, y) in [(2, 2), (13, 2), (2, 13), (13, 13), (2, 6), (13, 10)]: px[x, y] = (130, 134, 142, 255)
    return im

def hull_bulkhead():
    im = Image.new('RGBA', (16, 16)); px = im.load()
    for x in range(16):
        for y in range(16):
            base = (44, 47, 53)
            if x in (0, 15): base = (30, 32, 36)
            if 6 <= y <= 9: base = (58, 61, 68)
            if y in (6, 9): base = (36, 38, 43)
            if 6 < y < 9 and (x // 2) % 2 == 0: base = (150, 120, 40)  # hazard stripe
            px[x, y] = jitter(base, 4)
    return im

def hull_window():
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
    for x in range(16):
        for y in range(16):
            if x in (0, 15) or y in (0, 15): px[x, y] = jitter((60, 64, 72), 4)
            elif x in (1, 14) or y in (1, 14): px[x, y] = (40, 43, 50, 255)
            elif (x + y) in (9, 10) and 3 <= x <= 8: px[x, y] = (170, 200, 230, 90)
            else: px[x, y] = (0, 0, 0, 0)
    return im

def hull_lamp():
    im = hull_plating(); px = im.load()
    for x in range(4, 12):
        for y in range(4, 12):
            edge = x in (4, 11) or y in (4, 11)
            px[x, y] = (150, 170, 175, 255) if edge else jitter((225, 245, 250), 6)
    return im

def cradle():
    im = Image.new('RGBA', (16, 16)); px = im.load()
    for x in range(16):
        for y in range(16):
            base = (70, 66, 60)
            if x in (0, 15) or y in (0, 15): base = (45, 42, 38)
            if x in (4, 11) or y in (4, 11): base = (205, 150, 50)   # amber conduits
            if (x in (4, 11) and y in (4, 11)): base = (255, 205, 90)
            px[x, y] = jitter(base, 5)
    return im

def modulator_base():
    im = Image.new('RGBA', (16, 16)); px = im.load()
    for x in range(16):
        for y in range(16):
            base = (112, 116, 124)
            if y >= 13: base = (86, 90, 98)
            if y == 13: base = (190, 150, 60)
            if (x + y) % 5 == 0 and y < 12: base = (100, 104, 112)
            px[x, y] = jitter(base, 4)
    return im

def modulator_crystal():
    im = Image.new('RGBA', (16, 16)); px = im.load()
    for x in range(16):
        for y in range(16):
            t = abs(x - 7.5) / 8.0
            c = (255 - 40 * t, 200 - 70 * t, 70 - 40 * t)
            if x in (3, 4): c = (255, 238, 160)            # facet highlight
            if x in (11, 12): c = (200, 120, 30)           # facet shadow
            if y in (0, 15): c = (255, 225, 120)
            px[x, y] = jitter(c, 6)
    return im

def coordinates():
    im = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); px = im.load()
    shape = ["................", "....########....", "...##########...", "..############..", "..############..",
             "..############..", "..###########...", "..##########....", "..#########.....", "..##########....",
             "..###########...", "..############..", "..############..", "...##########...", "....########....", "................"]
    for y, row in enumerate(shape):
        for x, ch in enumerate(row):
            if ch == '#':
                edge = any(shape[y + dy][x + dx] != '#' for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
                px[x, y] = (70, 90, 96, 255) if edge else jitter((120, 150, 155), 6)
    for (x, y) in [(5, 4), (7, 4), (9, 4), (5, 6), (8, 6), (5, 8), (7, 8), (5, 10), (9, 10), (6, 12), (10, 12)]:
        px[x, y] = (255, 190, 70, 255)
    return im

def wisp():
    im = Image.new('RGBA', (32, 32), (0, 0, 0, 0)); px = im.load()
    for x in range(24):
        for y in range(12):
            px[x, y] = jitter((48, 62, 56), 6)
    for x in range(30):
        for y in range(12, 21):
            px[x, y] = jitter((70, 150, 100), 8)
    for x in range(24, 28):
        for y in range(0, 4): px[x, y] = jitter((150, 150, 160), 5)
    # front face of body (u 6..12, v 6..12): eye strip
    eyes = Image.new('RGBA', (32, 32), (0, 0, 0, 0)); ep = eyes.load()
    for x in (7, 10):
        for y in range(8, 10): px[x, y] = (230, 40, 40, 255); ep[x, y] = (255, 50, 40, 255)
    for x in (8, 9):
        for y in range(8, 10): px[x, y] = (20, 24, 22, 255)
    return im, eyes

def giant(src):
    im = Image.open(src).convert('RGBA'); px = im.load()
    for x in range(im.width):
        for y in range(im.height):
            r, g, b, a = px[x, y]
            if a and g > r + 25 and g > b + 10:  # emerald accents -> amber
                px[x, y] = (clamp(g * 1.05), clamp(g * 0.62), clamp(b * 0.3), a)
            elif a:
                px[x, y] = (clamp(r * 0.9), clamp(g * 0.9), clamp(b * 0.95), a)
    return im

if __name__ == '__main__':
    hull_plating().save(f'{A}/blocks/hull_plating.png')
    hull_bulkhead().save(f'{A}/blocks/hull_bulkhead.png')
    hull_window().save(f'{A}/blocks/hull_window.png')
    hull_lamp().save(f'{A}/blocks/hull_lamp.png')
    cradle().save(f'{A}/blocks/modulator_cradle.png')
    modulator_base().save(f'{A}/blocks/modulator_base.png')
    modulator_crystal().save(f'{A}/blocks/modulator_crystal.png')
    coordinates().save(f'{A}/items/derelict_coordinates.png')
    w, e = wisp(); w.save(f'{A}/entity/emerald_wisp.png'); e.save(f'{A}/entity/emerald_wisp_eyes.png')
    giant(f'{A}/entity/emerald_sentinel.png').save(f'{A}/entity/giant_emerald_sentinel.png')
    print('ok')
