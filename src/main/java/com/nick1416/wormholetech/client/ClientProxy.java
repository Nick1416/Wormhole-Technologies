package com.nick1416.wormholetech.client;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.client.gui.GuiAncientTablet;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = WormholeTech.MODID, value = Side.CLIENT)
public class ClientProxy extends com.nick1416.wormholetech.common.CommonProxy {

	@SubscribeEvent
	public static void onItemColors(net.minecraftforge.client.event.ColorHandlerEvent.Item e) {
		e.getItemColors().registerItemColorHandler((stack, layer) -> layer == 1
				? com.nick1416.wormholetech.drone.DroneProgram.byMeta(stack.getMetadata()).color : 0xFFFFFF,
				Registration.XENOLITH_CHIP, Registration.ITEM_MINER_DRONE);
	}

	@SubscribeEvent
	public static void onModels(ModelRegistryEvent e) {
	    for (Item it : new Item[] { Registration.ITEM_XENOLITH_ORE, Registration.RAW_XENOLITH, Registration.INACTIVE_DRONE })
	        ModelLoader.setCustomModelResourceLocation(it, 0, new ModelResourceLocation(it.getRegistryName(), "inventory"));
	    for (int m = 0; m < com.nick1416.wormholetech.drone.DroneProgram.VALUES.length; m++)
	        ModelLoader.setCustomModelResourceLocation(Registration.XENOLITH_CHIP, m, new ModelResourceLocation(Registration.XENOLITH_CHIP.getRegistryName(), "inventory"));
	    for (com.nick1416.wormholetech.drone.DroneProgram p : com.nick1416.wormholetech.drone.DroneProgram.MINER_PROGRAMS)
	        ModelLoader.setCustomModelResourceLocation(Registration.ITEM_MINER_DRONE, p.ordinal(), new ModelResourceLocation(Registration.ITEM_MINER_DRONE.getRegistryName(), "inventory"));
	    ModelLoader.setCustomModelResourceLocation(Registration.COMPANION_DRONE, 0, new ModelResourceLocation(WormholeTech.MODID + ":defensive_drone", "inventory"));
	    ModelLoader.setCustomModelResourceLocation(Registration.COMPANION_DRONE, 1, new ModelResourceLocation(WormholeTech.MODID + ":research_drone", "inventory"));

	    Item genItem = Item.getItemFromBlock(Registration.RF_GENERATOR);
	    ModelLoader.setCustomModelResourceLocation(
	        genItem, 0, new ModelResourceLocation(genItem.getRegistryName(), "inventory"));
	    
	    Item naquadahGen = Item.getItemFromBlock(Registration.NAQUADAH_GENERATOR);
	    ModelLoader.setCustomModelResourceLocation(
	    		naquadahGen, 0, new ModelResourceLocation(naquadahGen.getRegistryName(), "inventory"));
	    
	    Item dupItem = Item.getItemFromBlock(Registration.WORMHOLE_DUPLICATOR);
	    ModelLoader.setCustomModelResourceLocation(dupItem, 0,
	        new ModelResourceLocation(dupItem.getRegistryName(), "inventory"));


	    Item naqOreItem = Item.getItemFromBlock(Registration.NAQUADAH_ORE);
	    ModelLoader.setCustomModelResourceLocation(
	        naqOreItem, 0, new ModelResourceLocation(naqOreItem.getRegistryName(), "inventory"));
	    
	    Item refinedBlock = Registration.ITEM_REFINED_NAQUADAH_BLOCK;
	    ModelLoader.setCustomModelResourceLocation(
	    		refinedBlock, 0, new ModelResourceLocation(refinedBlock.getRegistryName(), "inventory"));

	    Item naqBlockUnstable = Item.getItemFromBlock(Registration.UNSTABLE_NAQUADAH_BLOCK);
	    ModelLoader.setCustomModelResourceLocation(
	    		naqBlockUnstable, 0, new ModelResourceLocation(naqBlockUnstable.getRegistryName(), "inventory"));
	    
	    Item prysBlockItem = Item.getItemFromBlock(Registration.PRYSMIAN_BLOCK);
	    ModelLoader.setCustomModelResourceLocation(
	    		prysBlockItem, 0, new ModelResourceLocation(prysBlockItem.getRegistryName(), "inventory"));
	    
	    Item prysBlockItemUnstable = Item.getItemFromBlock(Registration.UNSTABLE_PRYSMIAN_BLOCK);
	    ModelLoader.setCustomModelResourceLocation(
	    		prysBlockItemUnstable, 0, new ModelResourceLocation(prysBlockItemUnstable.getRegistryName(), "inventory"));

	    Item logicFrame = Item.getItemFromBlock(Registration.PRYSMIAN_LOGIC_FRAME);
	    ModelLoader.setCustomModelResourceLocation(
	    		logicFrame, 0, new ModelResourceLocation(logicFrame.getRegistryName(), "inventory"));

	    Item raw = Registration.RAW_NAQUADAH;
	    ModelLoader.setCustomModelResourceLocation(
	        raw, 0, new ModelResourceLocation(raw.getRegistryName(), "inventory"));

	    // refined_naquadah
	    Item refined = Registration.REFINED_NAQUADAH;
	    ModelLoader.setCustomModelResourceLocation(
	        refined, 0, new ModelResourceLocation(refined.getRegistryName(), "inventory"));
	    
	    // quantum circuit
	    Item circuit = Registration.QUANTUM_CIRCUIT;
	    ModelLoader.setCustomModelResourceLocation(
	    		circuit, 0, new ModelResourceLocation(circuit.getRegistryName(), "inventory"));

	    // unstable naquadah
	    Item unstableNaquada = Registration.UNSTABLE_NAQUADAH;
	    ModelLoader.setCustomModelResourceLocation(
	    		unstableNaquada, 0, new ModelResourceLocation(unstableNaquada.getRegistryName(), "inventory"));
	    
	    // naquadah reactor
	    Item reactorNaquadah = Registration.NAQUADAH_REACTOR;
	    ModelLoader.setCustomModelResourceLocation(
	    		reactorNaquadah, 0, new ModelResourceLocation(reactorNaquadah.getRegistryName(), "inventory"));
	    
	    // mineral tuners
	    for (Item tuner : Registration.MINERAL_TUNERS) {
	        ModelLoader.setCustomModelResourceLocation(
	                tuner, 0, new ModelResourceLocation(tuner.getRegistryName(), "inventory"));
	    }
	    
	    // unstable naquadah reactor
	    Item reactorNaquadahUnstable = Registration.UNSTABLE_NAQUADAH_REACTOR;
	    ModelLoader.setCustomModelResourceLocation(
	    		reactorNaquadahUnstable, 0, new ModelResourceLocation(reactorNaquadahUnstable.getRegistryName(), "inventory"));
	    
	    // unstable wormhole duplicator
	    Item udup = Item.getItemFromBlock(Registration.UNSTABLE_WORMHOLE_DUPLICATOR);
	    ModelLoader.setCustomModelResourceLocation(udup, 0,
	        new ModelResourceLocation(udup.getRegistryName(), "inventory"));

	    
	    // prysmian
	    Item prys = Registration.PRYSMIAN;
	    ModelLoader.setCustomModelResourceLocation(
	    		prys, 0, new ModelResourceLocation(prys.getRegistryName(), "inventory"));

	    Item tablet = Registration.ANCIENT_TABLET;
	    ModelLoader.setCustomModelResourceLocation(
	    		tablet, 0, new ModelResourceLocation(tablet.getRegistryName(), "inventory"));

	    Item padItem = Item.getItemFromBlock(Registration.WORMHOLE_PAD);
	    ModelLoader.setCustomModelResourceLocation(
	    		padItem, 0, new ModelResourceLocation(padItem.getRegistryName(), "inventory"));

	    Item linker = Registration.WORMHOLE_LINKER;
	    ModelLoader.setCustomModelResourceLocation(
	    		linker, 0, new ModelResourceLocation(linker.getRegistryName(), "inventory"));

	    for (Item hd : com.nick1416.wormholetech.item.HardDiamond.ALL)
	        ModelLoader.setCustomModelResourceLocation(hd, 0, new ModelResourceLocation(hd.getRegistryName(), "inventory"));

	    Item aetherius = Registration.AETHERIUS;
	    ModelLoader.setCustomModelResourceLocation(
	    		aetherius, 0, new ModelResourceLocation(aetherius.getRegistryName(), "inventory"));

	    Item compressed = Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH);
	    ModelLoader.setCustomModelResourceLocation(
	    		compressed, 0, new ModelResourceLocation(compressed.getRegistryName(), "inventory"));

	    Item computer = Item.getItemFromBlock(Registration.RELATIVISTIC_COMPUTER);
	    ModelLoader.setCustomModelResourceLocation(
	    		computer, 0, new ModelResourceLocation(computer.getRegistryName(), "inventory"));

	    Item refiner = Item.getItemFromBlock(Registration.HIGH_ENERGY_REFINER);
	    ModelLoader.setCustomModelResourceLocation(
	    		refiner, 0, new ModelResourceLocation(refiner.getRegistryName(), "inventory"));

	    for (Item it : new Item[] { Registration.ITEM_DIALING_COMPUTER, Registration.ITEM_ANCIENT_GATE_FRAME, Registration.GATE_LOCATOR,
	            Registration.ITEM_NATURAL_NAQUADAH_ORE, Registration.ITEM_ELYNDRA_NAQUADAH_ORE, Registration.ITEM_UTOPIS_CONSOLE, Registration.ITEM_PRYSMIAN_ORE, Registration.DIAMOND_NUGGET, Registration.ITEM_REINFORCED_GATE_FRAME, Registration.ITEM_HULL_PLATING, Registration.ITEM_HULL_BULKHEAD, Registration.ITEM_HULL_LAMP, Registration.ITEM_HULL_LAMP_GOLD, Registration.ITEM_HULL_WINDOW, Registration.ITEM_MODULATOR_CRADLE, Registration.ITEM_QUANTUM_FIELD_MODULATOR, Registration.DERELICT_COORDINATES, Registration.INTERGALACTIC_COORDINATES, Registration.NANITE_CORE, Registration.ITEM_GATE_NETWORK_COMPUTER, Registration.ITEM_XENOLITH, Registration.ITEM_XENOLITH_TILES, Registration.ITEM_REPOSITORY, Registration.ITEM_XENO_LAMP, Registration.TITANIUM_INGOT, Registration.ITEM_TITANIUM_BLOCK, Registration.ITEM_NAQUADRIC_STONE, Registration.ITEM_ASHEN_REGOLITH, Registration.ITEM_MOON_REGOLITH, Registration.ITEM_TITANIUM_ORE, Registration.ITEM_NEUTRONIUM_ORE, Registration.NEUTRONIUM_INGOT, Registration.NEUTRONIUM_SUPERCONDUCTOR, Registration.ITEM_MINIATURE_FACTORY,
	            Registration.ITEM_NAQUADAH_CRYSTAL, Registration.ITEM_ANCIENT_STONE }) {
	        ModelLoader.setCustomModelResourceLocation(it, 0, new ModelResourceLocation(it.getRegistryName(), "inventory"));
	    }

	}

	@Override
	public void preInit() {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(P3XStormClient.class);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.entity.EntityEmeraldSentinel.class,
				com.nick1416.wormholetech.client.render.RenderEmeraldSentinel::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.entity.EntityGiantEmeraldSentinel.class,
				com.nick1416.wormholetech.client.render.RenderGiantEmeraldSentinel::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.entity.EntityEmeraldWisp.class,
				com.nick1416.wormholetech.client.render.RenderEmeraldWisp::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.entity.EntityEmeraldDrone.class,
				com.nick1416.wormholetech.client.render.RenderEmeraldDrone::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.talvora.EntityAlienVillager.class,
				com.nick1416.wormholetech.client.render.RenderAlienVillager::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.talvora.EntityXenolithDrone.class,
				com.nick1416.wormholetech.client.render.RenderXenolithDrone::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.drone.EntityCompanionDrone.class,
				com.nick1416.wormholetech.client.render.RenderCompanionDrone::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
				com.nick1416.wormholetech.moon.EntityRegolithCrawler.class,
				com.nick1416.wormholetech.client.render.RenderRegolithCrawler::new);
		net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.nick1416.wormholetech.elyndra.EntityAncientSpirit.class,
				com.nick1416.wormholetech.elyndra.RenderAncientSpirit::new);
		net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
				com.nick1416.wormholetech.knowledge.RepositoryTileEntity.class, new com.nick1416.wormholetech.client.render.RenderRepository());
	}

	@Override
	public void openRepositoryGui(net.minecraft.nbt.NBTTagCompound data) {
	    Minecraft mc = Minecraft.getMinecraft();
	    mc.addScheduledTask(() -> {
	        if (mc.currentScreen instanceof com.nick1416.wormholetech.client.gui.GuiRepository)
	            ((com.nick1416.wormholetech.client.gui.GuiRepository) mc.currentScreen).setData(data);
	        else mc.displayGuiScreen(new com.nick1416.wormholetech.client.gui.GuiRepository(data));
	    });
	}

	@Override
	public void openAncientTabletGui() {
	    Minecraft.getMinecraft().displayGuiScreen(new GuiAncientTablet());
	}

}
