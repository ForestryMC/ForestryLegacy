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
package forestry.arboriculture.gadgets;

import forestry.core.config.ForestryBlock;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.feature.WorldGenerator;

public class TileSapling extends TileTreeContainer {

	private int timesTicked = 0;
	
	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		timesTicked = nbttagcompound.getInteger("TT");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("TT", timesTicked);
	}

	@Override
	public void onBlockTick() {
		
		timesTicked++;
		tryGrow(false);
	}

	public boolean tryGrow(boolean bonemealed) {

		if (this.getTree() == null)
			return false;

		if(!bonemealed && timesTicked < getTree().getRequiredMaturity())
			return false;
		
		WorldGenerator generator = this.getTree().getTreeGenerator(worldObj, xCoord, yCoord, zCoord, bonemealed);
		if (generator.generate(worldObj, worldObj.rand, xCoord, yCoord, zCoord)) {
			if(worldObj.getBlockId(xCoord, yCoord - 1, zCoord) == ForestryBlock.soil.blockID)
				worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord - 1, zCoord, Block.sand.blockID, 0);
			return true;
		}

		return false;
	}

}
