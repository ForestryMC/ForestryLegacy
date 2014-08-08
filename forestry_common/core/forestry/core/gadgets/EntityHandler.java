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
package forestry.core.gadgets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import forestry.core.config.ForestryBlock;
import forestry.core.interfaces.IEntityHandler;
import forestry.core.items.ItemForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.CraftingIngredients;

public class EntityHandler implements IEntityHandler {

	@Override
	public void registerMachine(int meta, MachinePackage pack) {
		Proxies.log.finest("Registering boiler package: " + pack.itemName + " (Meta: " + meta + ")");
		for (CraftingIngredients recipe : pack.recipes)
			if (recipe != null) {
				Proxies.common.addRecipe(new ItemStack(ForestryBlock.machine, recipe.stackSize, meta), recipe.aobj);
				// ForestryCore.oreHandler.registerDictionaryRecipe(new CraftingRecipe(new ItemStack(ForestryBlock.machine, recipe.stackSize, meta),
				// recipe.aobj));
			}

		// Add the renderer by Proxy
		Proxies.common.registerMachineRenderer(meta, pack);
	}

	@Override
	public void registerHarvester(int meta, MachinePackage pack) {
		Proxies.log.finest("Registering harvester package: " + pack.itemName + " (Meta: " + meta + ")");
		for (CraftingIngredients recipe : pack.recipes)
			if (recipe != null) {
				Proxies.common.addRecipe(new ItemStack(ForestryBlock.harvester, recipe.stackSize, meta), recipe.aobj);
				// ForestryCore.oreHandler
				// .registerDictionaryRecipe(new CraftingRecipe(new ItemStack(ForestryBlock.harvester, recipe.stackSize, meta), recipe.aobj));
			}
	}

	@Override
	public void registerPlanter(int meta, MachinePackage pack) {
		Proxies.log.finest("Registering planter package: " + pack.itemName + " (Meta: " + meta + ")");
		for (CraftingIngredients recipe : pack.recipes)
			if (recipe != null) {
				Proxies.common.addRecipe(new ItemStack(ForestryBlock.planter, recipe.stackSize, meta), recipe.aobj);
				// ForestryCore.oreHandler.registerDictionaryRecipe(new CraftingRecipe(new ItemStack(ForestryBlock.planter, recipe.stackSize, meta),
				// recipe.aobj));
			}

		// Add the renderer
		Proxies.common.registerPlanterRenderer(meta, pack);
	}

	@Override
	public void registerMill(int meta, MachinePackage pack) {
		Proxies.log.finest("Registering mill package: " + pack.itemName + " (Meta: " + meta + ")");
		for (CraftingIngredients recipe : pack.recipes)
			if (recipe != null) {
				Proxies.common.addRecipe(new ItemStack(ForestryBlock.mill, recipe.stackSize, meta), recipe.aobj);
				// ForestryCore.oreHandler.registerDictionaryRecipe(new CraftingRecipe(new ItemStack(ForestryBlock.mill, recipe.stackSize, meta), recipe.aobj));
			}

		// Add the renderer by Proxy
		Proxies.common.registerMillRenderer(meta, pack);
	}

	/**
	 * Package names have to be registered at the end in one go.
	 */
	@Override
	public void registerAllPackageNames() {

		Item.itemsList[ForestryBlock.machine.blockID] = null;
		Item.itemsList[ForestryBlock.machine.blockID] = new ItemForestryBlock(ForestryBlock.machine.blockID - 256, "machine");

		if (ForestryBlock.engine != null) {
			Item.itemsList[ForestryBlock.engine.blockID] = null;
			Item.itemsList[ForestryBlock.engine.blockID] = new ItemForestryBlock(ForestryBlock.engine.blockID - 256, "engine");
		}

		if (ForestryBlock.harvester != null) {
			Item.itemsList[ForestryBlock.harvester.blockID] = null;
			Item.itemsList[ForestryBlock.harvester.blockID] = new ItemForestryBlock(ForestryBlock.harvester.blockID - 256, "harvester");
		}

		if (ForestryBlock.planter != null) {
			Item.itemsList[ForestryBlock.planter.blockID] = null;
			Item.itemsList[ForestryBlock.planter.blockID] = new ItemForestryBlock(ForestryBlock.planter.blockID - 256, "planter");
		}

		if (ForestryBlock.mill != null) {
			Item.itemsList[ForestryBlock.mill.blockID] = null;
			Item.itemsList[ForestryBlock.mill.blockID] = new ItemForestryBlock(ForestryBlock.mill.blockID - 256, "mill");
		}

	}
}
