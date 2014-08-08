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
package forestry.core.config;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import forestry.core.gadgets.BlockMachine;
import forestry.core.gadgets.BlockMill;
import forestry.core.gadgets.BlockResource;
import forestry.core.gadgets.BlockSoil;
import forestry.core.gadgets.BlockStainedGlass;
import forestry.core.items.ItemForestryBlock;

public class ConfigureBlocks {

	public static void initialize() {

		ForestryBlock.soil = new BlockSoil(Config.getOrCreateBlockIdProperty("soil", Defaults.ID_BLOCK_SOIL)).setBlockName("soil");
		Item.itemsList[ForestryBlock.soil.blockID] = null;
		Item.itemsList[ForestryBlock.soil.blockID] = new ItemForestryBlock(ForestryBlock.soil.blockID - 256, "soil");
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.soil, 0, "shovel", 0);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.soil, 1, "shovel", 0);

		ForestryBlock.resources = new BlockResource(Config.getOrCreateBlockIdProperty("resources", Defaults.ID_BLOCK_RESOURCES)).setBlockName("apatite");
		Item.itemsList[ForestryBlock.resources.blockID] = null;
		Item.itemsList[ForestryBlock.resources.blockID] = new ItemForestryBlock(ForestryBlock.resources.blockID - 256, "resource");
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.resources, 0, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.resources, 1, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(ForestryBlock.resources, 2, "pickaxe", 1);

		OreDictionary.registerOre("oreApatite", new ItemStack(ForestryBlock.resources, 1, 0));
		OreDictionary.registerOre("oreCopper", new ItemStack(ForestryBlock.resources, 1, 1));
		OreDictionary.registerOre("oreTin", new ItemStack(ForestryBlock.resources, 1, 2));

		ForestryBlock.machine = new BlockMachine(Config.getOrCreateBlockIdProperty("machine", Defaults.ID_BLOCK_MACHINE)).setBlockName("machine");
		ForestryBlock.mill = new BlockMill(Config.getOrCreateBlockIdProperty("mill", Defaults.ID_BLOCK_MILL), 44).setBlockName("mill");

		ForestryBlock.glass = new BlockStainedGlass(Config.getOrCreateBlockIdProperty("stained", Defaults.ID_BLOCK_RESOURCES), 176).setBlockName("stained");
		Item.itemsList[ForestryBlock.glass.blockID] = null;
		Item.itemsList[ForestryBlock.glass.blockID] = new ItemForestryBlock(ForestryBlock.glass.blockID - 256, "stained");
		
	}

}
