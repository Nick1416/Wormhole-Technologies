from PIL import Image
T=(0,0,0,0)
def icon(lit, right):
    im=Image.new("RGBA",(16,16),T); p=im.load()
    dust=(255,40,30,255) if lit else (110,10,8,255)
    dhi=(255,140,120,255) if lit else (150,30,25,255)
    # dust loop ring x2..13, y4..13
    for x in range(2,14):
        for y in (4,13): p[x,y]=dust
    for y in range(4,14):
        for x in (2,13): p[x,y]=dust
    for (x,y) in [(5,13),(9,13),(2,8),(13,9)]: p[x,y]=dhi
    # repeater on top edge x5..10, y2..6
    slab=(170,170,170,255); dark=(90,90,90,255)
    for x in range(5,11):
        for y in range(2,7): p[x,y]=slab
    for x in range(5,11): p[x,6]=dark
    tlit=(255,60,40,255) if lit else (120,20,15,255); stick=(110,80,40,255)
    a,b=(6,9) if right else (9,6)   # input torch, output torch (output toward arrow)
    p[a,4]=stick; p[a,3]=tlit; p[b,4]=stick; p[b,3]=tlit
    # chevron showing signal direction
    if right: pts=[(7,4),(8,5),(7,6)]
    else: pts=[(8,4),(7,5),(8,6)]
    for q in pts: p[q]=dark
    p[7 if right else 8,5]=dark
    # torch at left inside the loop, at (2,9) side: standing torch
    for y in (9,10,11): p[4,y]=stick
    p[4,8]=(255,220,90,255) if lit else (130,30,20,255)
    p[3,8]=tlit
    return im
sheet=Image.new("RGBA",(32,16),T)
sheet.paste(icon(False,False),(0,0)); sheet.paste(icon(True,True),(16,0))
sheet.save("src/main/resources/assets/wormholetech/textures/gui/redstone_clock.png")
# preview: 8x, on GUI grey with labels
pv=Image.new("RGBA",(330,150),(198,198,198,255))
pv.paste(icon(False,False).resize((128,128),Image.NEAREST),(10,11),icon(False,False).resize((128,128),Image.NEAREST))
pv.paste(icon(True,True).resize((128,128),Image.NEAREST),(162,11),icon(True,True).resize((128,128),Image.NEAREST))
from PIL import ImageDraw; d=ImageDraw.Draw(pv); d.text((60,2),"-1",fill=(0,0,0)); d.text((215,2),"+1",fill=(0,0,0))
for i,x in ((icon(False,False),10),(icon(True,True),162)): pv.paste(i,(x+132,120),i)
pv.save("/workspace/redstone_clock_preview.png")
