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
package forestry.core.worldgen;

import net.minecraft.world.World;
import forestry.api.world.ITreeGenData;

public class BlockType {

	int meta;
	int id;

	public BlockType(int id, int meta) {
		this.id = id;
		this.meta = meta;
	}

	public void setBlock(World world, ITreeGenData tree, int x, int y, int z) {
		world.setBlockAndMetadataWithUpdate(x, y, z, id, meta, true);
		if(world.getBlockTileEntity(x, y, z) != null)
			world.removeBlockTileEntity(x, y, z);
	}
}
