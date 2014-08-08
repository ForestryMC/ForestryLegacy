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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import forestry.core.config.ForestryBlock;

public class ItemForestryPickaxe extends ItemForestryTool {

	private static Block blocksEffectiveAgainst[];

	static {
		blocksEffectiveAgainst = (new Block[] { Block.cobblestone, Block.stone, Block.sandStone, Block.cobblestoneMossy, Block.oreIron, Block.blockSteel,
				Block.oreCoal, Block.blockGold, Block.brick, Block.netherBrick, Block.netherrack, Block.oreGold, Block.oreDiamond, Block.blockDiamond,
				Block.ice, Block.netherrack, Block.oreLapis, Block.blockLapis, ForestryBlock.resources, ForestryBlock.beehives, ForestryBlock.engine,
				ForestryBlock.machine, ForestryBlock.harvester, ForestryBlock.planter, ForestryBlock.mill });
	}

	public ItemForestryPickaxe(int i, ItemStack remnants) {
		super(i, blocksEffectiveAgainst, remnants);
	}

}
