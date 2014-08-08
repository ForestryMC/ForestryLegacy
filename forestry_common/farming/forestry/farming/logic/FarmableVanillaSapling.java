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
package forestry.farming.logic;

import java.util.Map;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FarmableVanillaSapling extends FarmableGenericSapling {
	
	int saplingId;
	int saplingMeta;
	
	public FarmableVanillaSapling() {
		super(Block.sapling.blockID, -1, new ItemStack[] {
				new ItemStack(Item.appleRed)
		});
	}
	
	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		ITree tree = null;
		for(Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSaplings.entrySet()) {
			if(entry.getKey().isItemEqual(germling)
					&& entry.getValue() instanceof ITree) {
				tree = (ITree)entry.getValue();
				break;
			}
		}
		
		if(tree == null)
			return false;
		
		return TreeManager.treeInterface.plantSapling(world, tree, x, y, z);
	}
	
}
