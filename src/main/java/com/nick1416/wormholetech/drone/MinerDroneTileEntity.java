package com.nick1416.wormholetech.drone;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Produces one ore of its mineral every {@link ModConfig#minerDroneTicksPerItem} ticks into an adjacent
 * IItemHandler (chest, cache, barrel...). Needs no power. Naquadah works only on the naquadah planet;
 * IE minerals only in the Overworld and only with Immersive Engineering installed.
 */
public class MinerDroneTileEntity extends TileEntity implements ITickable {
    public static final int WORKING = 0, WRONG_DIM = 1, NO_STORAGE = 2, FULL = 3, NO_MINERAL = 4, NO_MODULATOR = 5;
    private static final String[] KEYS = { "working", "wrong_dim", "no_storage", "full", "no_mineral", "no_modulator" };

    public int program;
    public int status = WORKING;
    private int progress;
    private boolean powered;
    public int charge = DroneCharge.max();
    private long nextModCheck;
    private ItemStack pending = ItemStack.EMPTY;

    @Override
    public void update() {
        if (world.isRemote) return;
        if (world.getTotalWorldTime() >= nextModCheck) {        // re-check every 3 s
            nextModCheck = world.getTotalWorldTime() + 60;
            powered = ModulatorTracker.near(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
        int before = DroneCharge.percent(charge);
        int s = check();
        if (powered || s == WORKING) charge = DroneCharge.tick(charge, powered);
        if (DroneCharge.percent(charge) != before) markDirty();
        if (s == WORKING && ++progress >= Math.max(1, ModConfig.minerDroneTicksPerItem)) {
            progress = 0;
            if (pending.isEmpty()) pending = produce();
            if (pending.isEmpty()) s = NO_MINERAL;
            else {
                IItemHandler h = storage();
                pending = h == null ? pending : ItemHandlerHelper.insertItemStacked(h, pending, false);
                if (!pending.isEmpty()) s = FULL;
            }
        }
        if (s != status) {
            status = s;
            markDirty();
            net.minecraft.block.state.IBlockState st = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, st, st, 3);
        }
    }

    private DroneProgram prog() { return DroneProgram.byMeta(program); }

    private int check() {
        int dim = world.provider.getDimension();
        if (prog() == DroneProgram.NAQUADAH) {
            if (!com.nick1416.wormholetech.world.ModDimensions.isRegistered() || dim != com.nick1416.wormholetech.world.ModDimensions.id()) return WRONG_DIM;
        } else if (prog() == DroneProgram.XENOLITH) {
            if (!com.nick1416.wormholetech.world.ModDimensions.isTalVoraRegistered() || dim != com.nick1416.wormholetech.world.ModDimensions.talVoraId()) return WRONG_DIM;
        } else if (prog() == DroneProgram.TITANIUM) {
            if (!com.nick1416.wormholetech.world.ModDimensions.isSelVoraRegistered() || dim != com.nick1416.wormholetech.world.ModDimensions.selVoraId()) return WRONG_DIM;
        } else {
            if (dim != 0) return WRONG_DIM;
            if (!net.minecraftforge.fml.common.Loader.isModLoaded("immersiveengineering")) return NO_MINERAL;
        }
        if (!powered && charge <= 0) return NO_MODULATOR;
        if (storage() == null) return NO_STORAGE;
        if (!pending.isEmpty()) {
            ItemStack left = ItemHandlerHelper.insertItemStacked(storage(), pending, true);
            if (left.getCount() == pending.getCount()) return FULL;
        }
        return WORKING;
    }

    private IItemHandler storage() {
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = world.getTileEntity(pos.offset(f));
            if (te == null || te instanceof MinerDroneTileEntity) continue;
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, f.getOpposite()))
                return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, f.getOpposite());
        }
        return null;
    }

    private ItemStack produce() {
        if (prog() == DroneProgram.NAQUADAH) return new ItemStack(Registration.ITEM_NATURAL_NAQUADAH_ORE);
        if (prog() == DroneProgram.XENOLITH) return new ItemStack(Registration.RAW_XENOLITH);
        if (prog() == DroneProgram.TITANIUM) return new ItemStack(Registration.ITEM_TITANIUM_ORE);
        try { return IEMinerals.randomOre(prog().mineral, world.rand); } catch (Throwable t) { return ItemStack.EMPTY; }
    }

    public ITextComponent statusText() {
        TextComponentTranslation t = new TextComponentTranslation("status.wormholetech.miner_drone." + KEYS[Math.min(KEYS.length - 1, status)]);
        if (status == WRONG_DIM) t = new TextComponentTranslation(prog() == DroneProgram.NAQUADAH ? "status.wormholetech.miner_drone.wrong_dim_naquadah"
                : prog() == DroneProgram.XENOLITH ? "status.wormholetech.miner_drone.wrong_dim_tal_vora"
                : prog() == DroneProgram.TITANIUM ? "status.wormholetech.miner_drone.wrong_dim_sel_vora" : "status.wormholetech.miner_drone.wrong_dim");
        t.appendText(" (" + DroneCharge.percent(charge) + "% charge" + (powered ? ", charging)" : ")"));
        return t;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        super.writeToNBT(c);
        c.setInteger("Program", program); c.setInteger("Charge", charge); c.setInteger("Status", status); c.setInteger("Progress", progress);
        if (!pending.isEmpty()) c.setTag("Pending", pending.writeToNBT(new NBTTagCompound()));
        return c;
    }

    @Override
    public void readFromNBT(NBTTagCompound c) {
        super.readFromNBT(c);
        program = c.getInteger("Program"); charge = c.hasKey("Charge") ? c.getInteger("Charge") : DroneCharge.max(); status = c.getInteger("Status"); progress = c.getInteger("Progress");
        pending = c.hasKey("Pending") ? new ItemStack(c.getCompoundTag("Pending")) : ItemStack.EMPTY;
    }

    @Override public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 0, getUpdateTag()); }
    @Override public void onDataPacket(NetworkManager n, SPacketUpdateTileEntity p) { readFromNBT(p.getNbtCompound()); }
}
