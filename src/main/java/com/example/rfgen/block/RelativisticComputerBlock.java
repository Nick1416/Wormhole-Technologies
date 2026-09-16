package com.example.rfgen.block;

import com.example.rfgen.registry.Registration;
import com.example.rfgen.tile.RelativisticComputerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class RelativisticComputerBlock extends Block {
    public RelativisticComputerBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(20.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new RelativisticComputerTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof RelativisticComputerTileEntity)) return false;
        RelativisticComputerTileEntity computer = (RelativisticComputerTileEntity) te;
        ItemStack held = player.getHeldItem(hand);

        if (player.isSneaking()) {
            ItemStack out = computer.ejectAetherius();
            if (!out.isEmpty()) {
                if (!player.inventory.addItemStackToInventory(out)) {
                    player.dropItem(out, false);
                }
                player.sendStatusMessage(new TextComponentTranslation("message.rfgen.computer.ejected"), true);
            }
            return true;
        }

        if (!held.isEmpty() && held.getItem() == Registration.AETHERIUS) {
            if (computer.insertAetherius(held)) {
                player.sendStatusMessage(new TextComponentTranslation("message.rfgen.computer.inserted"), true);
            } else {
                player.sendStatusMessage(new TextComponentTranslation("message.rfgen.computer.full"), true);
            }
            return true;
        }

        player.sendStatusMessage(new TextComponentTranslation(
                computer.isActive() ? "message.rfgen.computer.active" : "message.rfgen.computer.idle",
                computer.getEnergyStored()), true);
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof RelativisticComputerTileEntity) {
            ((RelativisticComputerTileEntity) te).dropContents(world, pos);
        }
        super.breakBlock(world, pos, state);
    }
}
