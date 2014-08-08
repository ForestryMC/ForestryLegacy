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

import java.util.Collection;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.utils.Vect;

public class FarmLogicPeat extends FarmLogicWatered {

	public FarmLogicPeat(IFarmHousing housing) {
		super(housing,
				new ItemStack[] { new ItemStack(ForestryBlock.soil.blockID, 1, 1) },
				new ItemStack[] { new ItemStack(ForestryBlock.soil.blockID, 1, 1) },
				new ItemStack[] { new ItemStack(Block.dirt), new ItemStack(Block.grass) });
	}

	@Override
	public String getName() {
		if(isManual)
			return "Manual Peat Bog";
		else
			return "Managed Peat Bog";
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
		
		world = housing.getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for(int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			ItemStack occupant = getAsItemStack(position);
			
			if(occupant.itemID != ForestryBlock.soil.blockID)
				continue;
			int type = occupant.getItemDamage() & 0x03;
			int maturity = occupant.getItemDamage() >> 2;

			if(type != 1)
				continue;
		
			if(maturity >= 3) {
				crops.push(new CropPeat(world, position));
			}
			
		}
		return crops;
	}

	@Override
	public int getIconIndex() {
		return 16;
	}

	@Override
	public String getTextureFile() {
		return Defaults.TEXTURE_ITEMS;
	}


}
