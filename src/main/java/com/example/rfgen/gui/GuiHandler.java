package com.example.rfgen.gui;

import com.example.rfgen.RFGen;
import com.example.rfgen.tile.AetheriusRefinerTileEntity;
import com.example.rfgen.tile.RelativisticComputerTileEntity;
import com.example.rfgen.tile.UnstableWormholeDuplicatorTileEntity;
import com.example.rfgen.tile.WormholeDuplicatorTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (id == RFGen.GUI_RELATIVISTIC_COMPUTER || te instanceof RelativisticComputerTileEntity) {
            if (te instanceof RelativisticComputerTileEntity) {
                return new ContainerRelativisticComputer(player.inventory, (RelativisticComputerTileEntity) te);
            }
        }
        if (id == RFGen.GUI_HIGH_ENERGY_REFINER || te instanceof AetheriusRefinerTileEntity) {
            if (te instanceof AetheriusRefinerTileEntity) {
                return new ContainerHighEnergyRefiner(player.inventory, (AetheriusRefinerTileEntity) te);
            }
        }
        if (te instanceof WormholeDuplicatorTileEntity) {
            return new ContainerWormholeDuplicator(player.inventory, (WormholeDuplicatorTileEntity) te);
        }
        if (te instanceof UnstableWormholeDuplicatorTileEntity) {
            return new ContainerWormholeDuplicator(player.inventory, (UnstableWormholeDuplicatorTileEntity) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return getClientGui(id, player, world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    private Object getClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (id == RFGen.GUI_RELATIVISTIC_COMPUTER || te instanceof RelativisticComputerTileEntity) {
            if (te instanceof RelativisticComputerTileEntity) {
                return new GuiRelativisticComputer(player.inventory, (RelativisticComputerTileEntity) te);
            }
        }
        if (id == RFGen.GUI_HIGH_ENERGY_REFINER || te instanceof AetheriusRefinerTileEntity) {
            if (te instanceof AetheriusRefinerTileEntity) {
                return new GuiHighEnergyRefiner(player.inventory, (AetheriusRefinerTileEntity) te);
            }
        }
        if (te instanceof WormholeDuplicatorTileEntity) {
            return new GuiWormholeDuplicator(player.inventory, (WormholeDuplicatorTileEntity) te);
        }
        if (te instanceof UnstableWormholeDuplicatorTileEntity) {
            return new GuiWormholeDuplicator(player.inventory, (UnstableWormholeDuplicatorTileEntity) te);
        }
        return null;
    }
}
