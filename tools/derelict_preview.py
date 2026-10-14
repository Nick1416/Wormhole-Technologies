"""Isometric previews: Quantum Field Modulator, Emerald Wisp, and a ship corridor slice (from a server dump)."""
import sys, os, math
from PIL import Image, ImageDraw
T = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources', 'assets', 'wormholetech', 'textures')
C30, S30 = math.cos(math.radians(30)), math.sin(math.radians(30))

class Iso:
    def __init__(self, w, h, scale, ox, oy, bg=(18, 20, 28, 255)):
        self.im = Image.new('RGBA', (w, h), bg); self.d = ImageDraw.Draw(self.im); self.s = scale; self.ox, self.oy = ox, oy
    def p(self, x, y, z):
        return (self.ox + (x - z) * C30 * self.s, self.oy + (x + z) * S30 * self.s - y * self.s)
    def face(self, origin, du, dv, tex, shade=1.0):
        tw, th = tex.size; px = tex.load()
        for i in range(tw):
            for j in range(th):
                c = px[i, j]
                if len(c) > 3 and c[3] < 20: continue
                col = tuple(min(255, int(v * shade)) for v in c[:3]) + (255,)
                pts = []
                for (a, b) in ((i, j), (i + 1, j), (i + 1, j + 1), (i, j + 1)):
                    u, v = a / tw, b / th
                    pts.append(self.p(origin[0] + du[0] * u + dv[0] * v, origin[1] + du[1] * u + dv[1] * v, origin[2] + du[2] * u + dv[2] * v))
                self.d.polygon(pts, fill=col)
    def box(self, x0, y0, z0, x1, y1, z1, top, south, east, glow=False):
        # visible faces: east (+x), south (+z), top (+y); texture v goes downward on sides
        self.face((x1, y1, z0), (0, 0, z1 - z0), (0, -(y1 - y0), 0), east, 1.0 if glow else 0.78)
        self.face((x0, y1, z1), (x1 - x0, 0, 0), (0, -(y1 - y0), 0), south, 1.0 if glow else 0.62)
        self.face((x0, y1, z0), (x1 - x0, 0, 0), (0, 0, z1 - z0), top, 1.0)

def crop(tex, u0, v0, u1, v1): return tex.crop((int(u0), int(v0), int(u1), int(v1)))

def modulator(out):
    base = Image.open(f'{T}/blocks/modulator_base.png').convert('RGBA'); cry = Image.open(f'{T}/blocks/modulator_crystal.png').convert('RGBA')
    cr = Image.open(f'{T}/blocks/modulator_cradle.png').convert('RGBA')
    iso = Iso(560, 560, 14, 280, 330)
    # cradle base ring around (shown as 3x3 slab below at -1 block) for context
    for dx in (-1, 0, 1):
        for dz in (-1, 0, 1):
            iso.box(dx * 16, -16, dz * 16, dx * 16 + 16, 0, dz * 16 + 16, cr, cr, cr)
    for (x0, y0, z0, x1, y1, z1, t, g) in [(3, 0, 3, 13, 3, 13, base, False), (4, 3, 4, 12, 4, 12, base, False),
                                          (5.5, 4, 5.5, 10.5, 9, 10.5, cry, True), (4.5, 9, 4.5, 11.5, 10, 11.5, base, False),
                                          (5.5, 10, 5.5, 10.5, 14, 10.5, cry, True), (6.5, 14, 6.5, 9.5, 16, 9.5, cry, True)]:
        iso.box(x0, y0, z0, x1, y1, z1, crop(t, x0, z0, x1, z1) if t is base else t, t, t, glow=g)
    # amber glow halo
    glow = Image.new('RGBA', iso.im.size, (0, 0, 0, 0)); gd = ImageDraw.Draw(glow)
    cx, cy = iso.p(8, 10, 8)
    for r in range(90, 0, -6): gd.ellipse((cx - r, cy - r, cx + r, cy + r), fill=(255, 180, 60, int(3 + (90 - r) * 0.25)))
    iso.im = Image.alpha_composite(iso.im, glow)
    iso.im.save(out)

def wisp(out):
    tex = Image.open(f'{T}/entity/emerald_wisp.png').convert('RGBA'); eyes = Image.open(f'{T}/entity/emerald_wisp_eyes.png').convert('RGBA')
    iso = Iso(420, 420, 30, 210, 260)
    # model faces from UV layout (box 6x6x6 at u0 v0): top u6..12 v0..6, front(north) u6..12 v6..12, right side u12..18 v6..12
    top = crop(tex, 6, 0, 12, 6); front = crop(tex, 6, 6, 12, 12); side = crop(tex, 0, 6, 6, 12)
    f = front.copy(); f.alpha_composite(crop(eyes, 6, 6, 12, 12))
    finL = crop(tex, 0, 17, 5, 21); fin_top = crop(tex, 1, 12, 6, 17)
    iso.box(-4, 1, -2.5, -3, 5, 2.5, fin_top, crop(tex, 0, 17, 1, 21), finL)        # left fin
    iso.box(-3, 0, -3, 3, 6, 3, top, f, side)                                         # body, eyes face +z (towards viewer)
    iso.box(3, 1, -2.5, 4, 5, 2.5, fin_top, crop(tex, 12, 17, 13, 21), crop(tex, 12, 17, 17, 21))  # right fin
    iso.box(-0.5, 6, -0.5, 0.5, 9, 0.5, crop(tex, 24, 0, 26, 2), crop(tex, 24, 1, 25, 4), crop(tex, 25, 1, 26, 4))
    glow = Image.new('RGBA', iso.im.size, (0, 0, 0, 0)); gd = ImageDraw.Draw(glow); cx, cy = iso.p(0, 2.5, 3)
    for r in range(40, 0, -4): gd.ellipse((cx - r, cy - r * 0.5, cx + r, cy + r * 0.5), fill=(255, 40, 30, int(4 + (40 - r) * 0.6)))
    iso.im = Image.alpha_composite(iso.im, glow)
    iso.im.save(out)

def corridor(dump, out, x0=-8, z0=-8, n=40):
    tex = {}
    def t(name):
        if name not in tex:
            p = f'{T}/blocks/{name}.png'
            if name == 'stargate_portal': im = Image.open(p).convert('RGBA').crop((0, 0, 16, 16))
            elif os.path.exists(p): im = Image.open(p).convert('RGBA')
            else: im = Image.new('RGBA', (16, 16), (200, 120, 60, 255))
            tex[name] = im
        return tex[name]
    blocks = {}
    for line in open(dump):
        x, y, z, name = line.split(); x, y, z = int(x), int(y), int(z)
        if x0 <= x < x0 + n and z0 <= z < z0 + n and 64 <= y <= 68: blocks[(x, y, z)] = name
    iso = Iso(1400, 1000, 15, 700, 140, bg=(4, 5, 12, 255))
    for (x, y, z) in sorted(blocks, key=lambda k: (k[0] + k[2], k[1])):
        nm = blocks[(x, y, z)]
        if nm == 'quantum_field_modulator': nm = 'modulator_crystal'
        if nm == 'chest': nm = 'modulator_base'
        tt = t(nm)
        iso.box(x - x0, y - 64, z - z0, x - x0 + 1, y - 63, z - z0 + 1, tt, tt, tt, glow=nm in ('hull_lamp', 'modulator_crystal', 'stargate_portal'))
    iso.im.save(out)

if __name__ == '__main__':
    modulator('/workspace/preview_quantum_field_modulator.png')
    wisp('/workspace/preview_emerald_wisp.png')
    if len(sys.argv) > 1: corridor(sys.argv[1], '/workspace/preview_derelict_corridor.png')
    print('ok')
