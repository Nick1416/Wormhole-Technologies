package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.DialingComputerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DialingComputerBlock extends Block {
    public DialingComputerBlock() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return newTile(); }
    protected DialingComputerTileEntity newTile() { return new DialingComputerTileEntity(); }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (!(world.getTileEntity(pos) instanceof DialingComputerTileEntity)) return false;
        player.openGui(WormholeTech.INSTANCE, WormholeTech.GUI_DIALING_COMPUTER, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof DialingComputerTileEntity) ((DialingComputerTileEntity) te).requestCheck();
    }

    /** Set while {@link #swap} replaces the block, so breakBlock keeps the contents. */
    private static boolean swapping;

    /**
     * Turns a Dialing Computer with a Time Dilated Chip into a Gate Network Computer, or a Gate Network
     * Computer without one back into a Dialing Computer, keeping RF and slot contents, and reopens the
     * GUI for anyone who had it open. Aetherius has no slot in a plain Dialing Computer: it goes to the
     * player who took the chip out (or drops).
     */
    public static void swap(DialingComputerTileEntity old) {
        World world = old.getWorld();
        BlockPos pos = old.getPos();
        boolean toNetwork = old.hasUpgrade();
        old.close(true);
        int energy = old.getEnergyStored();
        ItemStack[] items = new ItemStack[DialingComputerTileEntity.SLOTS];
        for (int i = 0; i < items.length; i++) items[i] = old.items().getStackInSlot(i).copy();
        java.util.List<EntityPlayer> viewers = new java.util.ArrayList<EntityPlayer>();
        for (EntityPlayer p : world.playerEntities)
            if (p.openContainer instanceof com.nick1416.wormholetech.gui.ContainerDialingComputer
                    && ((com.nick1416.wormholetech.gui.ContainerDialingComputer) p.openContainer).getTe() == old) viewers.add(p);
        Block target = toNetwork ? com.nick1416.wormholetech.registry.Registration.GATE_NETWORK_COMPUTER
                : com.nick1416.wormholetech.registry.Registration.DIALING_COMPUTER;
        swapping = true;
        try {
            world.setBlockState(pos, target.getDefaultState(), 3);
        } finally {
            swapping = false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof DialingComputerTileEntity)) return;
        DialingComputerTileEntity dc = (DialingComputerTileEntity) te;
        dc.setEnergyStored(energy);
        for (int i = 0; i < items.length; i++) {
            if (i == DialingComputerTileEntity.SLOT_AETHERIUS && !toNetwork) {
                if (items[i].isEmpty()) continue;
                EntityPlayer to = viewers.isEmpty() ? null : viewers.get(0);
                if (to == null || !to.inventory.addItemStackToInventory(items[i]))
                    InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, items[i]);
                continue;
            }
            dc.items().setStackInSlot(i, items[i]);
        }
        dc.requestCheck();
        world.playSound(null, pos, toNetwork ? net.minecraft.init.SoundEvents.BLOCK_END_PORTAL_FRAME_FILL : net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH,
                net.minecraft.util.SoundCategory.BLOCKS, 0.6F, 1.4F);
        for (EntityPlayer p : viewers)
            p.openGui(WormholeTech.INSTANCE, WormholeTech.GUI_DIALING_COMPUTER, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (!swapping && te instanceof DialingComputerTileEntity) {
            DialingComputerTileEntity dc = (DialingComputerTileEntity) te;
            dc.close(false);
            for (int i = 0; i < dc.items().getSlots(); i++) {
                ItemStack s = dc.items().getStackInSlot(i);
                if (!s.isEmpty()) InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, s.copy());
            }
        }
        super.breakBlock(world, pos, state);
    }
}
