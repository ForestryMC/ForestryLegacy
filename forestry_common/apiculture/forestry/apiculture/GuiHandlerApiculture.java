/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.apiculture.BeeManager;
import forestry.apiculture.gadgets.MachineApiaristChest;
import forestry.apiculture.gadgets.MachineApiary;
import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.apiculture.gadgets.TileAlvearyPlain;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.apiculture.gui.ContainerAlveary;
import forestry.apiculture.gui.ContainerAlvearyHygroregulator;
import forestry.apiculture.gui.ContainerAlvearySwarmer;
import forestry.apiculture.gui.ContainerApiaristInventory;
import forestry.apiculture.gui.ContainerApiary;
import forestry.apiculture.gui.ContainerBeealyzer;
import forestry.apiculture.gui.ContainerHabitatLocator;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.gui.GuiAlveary;
import forestry.apiculture.gui.GuiAlvearyHygroregulator;
import forestry.apiculture.gui.GuiAlvearySwarmer;
import forestry.apiculture.gui.GuiApiaristInventory;
import forestry.apiculture.gui.GuiApiary;
import forestry.apiculture.gui.GuiBeealyzer;
import forestry.apiculture.gui.GuiHabitatLocator;
import forestry.apiculture.gui.GuiImprinter;
import forestry.apiculture.items.ItemBeealyzer.BeealyzerInventory;
import forestry.apiculture.items.ItemHabitatLocator.HabitatLocatorInventory;
import forestry.apiculture.items.ItemImprinter.ImprinterInventory;
import forestry.core.GuiHandlerBase;
import forestry.core.gadgets.MachineAnalyzer;
import forestry.core.gui.ContainerAnalyzer;
import forestry.core.gui.GuiAnalyzer;
import forestry.core.network.GuiId;

public class GuiHandlerApiculture extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		ItemStack equipped;
		switch (GuiId.values()[id]) {

		case AlvearyGUI:
			return new GuiAlveary(player.inventory, (TileAlvearyPlain) getTileForestry(world, x, y, z));

		case AlvearySwarmerGUI:
			return new GuiAlvearySwarmer(player.inventory, (TileAlvearySwarmer) getTileForestry(world, x, y, z));

		case AnalyzerGUI:
			return new GuiAnalyzer(player.inventory, (MachineAnalyzer)getTileForestry(world, x, y, z));

		case ApiaristChestGUI:
			MachineApiaristChest tile = (MachineApiaristChest)getTileForestry(world, x, y, z);
			return new GuiApiaristInventory(player, new ContainerApiaristInventory(player.inventory, tile, 5, 25), tile);

		case ApiaryGUI:
			return new GuiApiary(player.inventory, (MachineApiary)getTileForestry(world, x, y, z));

		case BeealyzerGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;

			return new GuiBeealyzer(player, new BeealyzerInventory(player, equipped));

		case HabitatLocatorGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;

			return new GuiHabitatLocator(player.inventory, new HabitatLocatorInventory(equipped));

		case HygroregulatorGUI:
			return new GuiAlvearyHygroregulator(player.inventory, (TileAlvearyHygroregulator)getTileForestry(world, x, y, z));
			
		case ImprinterGUI:
			return new GuiImprinter(player.inventory, new ImprinterInventory(player));

		default:
			return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		ItemStack equipped;
		switch (GuiId.values()[id]) {

		case AlvearyGUI:
			synchApiaristTracker(world, player);
			return new ContainerAlveary(player.inventory, (TileAlvearyPlain) getTileForestry(world, x, y, z));

		case AlvearySwarmerGUI:
			return new ContainerAlvearySwarmer(player.inventory, (TileAlvearySwarmer) getTileForestry(world, x, y, z));

		case AnalyzerGUI:
			synchApiaristTracker(world, player);
			return new ContainerAnalyzer(player.inventory, (MachineAnalyzer)getTileForestry(world, x, y, z));

		case ApiaristChestGUI:
			synchApiaristTracker(world, player);
			return new ContainerApiaristInventory(player.inventory, (MachineApiaristChest)getTileForestry(world, x, y, z), 5, 25);

		case ApiaryGUI:
			synchApiaristTracker(world, player);
			return new ContainerApiary(player.inventory, (MachineApiary)getTileForestry(world, x, y, z));

		case BeealyzerGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;

			synchApiaristTracker(world, player);
			return new ContainerBeealyzer(player.inventory, new BeealyzerInventory(player, equipped));

		case HabitatLocatorGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;

			return new ContainerHabitatLocator(player.inventory, new HabitatLocatorInventory(equipped));

		case HygroregulatorGUI:
			return new ContainerAlvearyHygroregulator(player.inventory, (TileAlvearyHygroregulator)getTileForestry(world, x, y, z));
			
		case ImprinterGUI:
			synchApiaristTracker(world, player);
			return new ContainerImprinter(player.inventory, new ImprinterInventory(player));

		default:
			return null;

		}
	}

	private void synchApiaristTracker(World world, EntityPlayer player) {
		BeeManager.breedingManager.getApiaristTracker(world, player.username).synchToPlayer(player);
	}
}
