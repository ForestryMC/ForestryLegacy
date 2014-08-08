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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.farming.ICrop;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class FarmableVanillaShroom extends FarmableGenericSapling {

	public FarmableVanillaShroom(int saplingId, int saplingMeta) {
		super(saplingId, saplingMeta);
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		if(Block.blocksList[blockid] == null)
			return null;
		if(blockid != Block.mushroomCapBrown.blockID && blockid != Block.mushroomCapRed.blockID)
			return null;
		
		return new CropBlock(world, blockid, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		int meta = 0;
		if(germling.itemID == Block.mushroomRed.blockID)
			meta = 1;
		
		Proxies.common.addBlockPlaceEffects(world, x, y, z, Block.mushroomBrown.blockID, 0);
		return world.setBlockAndMetadataWithNotify(x, y, z, ForestryBlock.mushroom.blockID, meta);
	}

}
