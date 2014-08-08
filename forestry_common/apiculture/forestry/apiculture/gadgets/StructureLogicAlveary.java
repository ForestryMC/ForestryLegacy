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
package forestry.apiculture.gadgets;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.core.ITileStructure;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.BlockStructure.EnumStructureState;
import forestry.core.gadgets.StructureLogic;
import forestry.core.utils.Schemata;
import forestry.core.utils.Vect;
import forestry.core.utils.Schemata.EnumStructureBlock;

public class StructureLogicAlveary extends StructureLogic {

	/* CONSTANTS */
	public static final String UID_ALVEARY = "alveary";
	public static final Schemata SCHEMATA_ALVEARY = new Schemata("alveary3x3", 5, 6, 5, "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF", "FAAAF", "FAAAF",
			"FABAF", "FCCCF", "FFFFF", "FFFFF", "FAAAF", "FAAAF", "FBMBF", "FCCCF", "FFFFF", "FFFFF", "FAAAF", "FAAAF", "FABAF", "FCCCF", "FFFFF", "FFFFF",
			"FFFFF", "FFFFF", "FFFFF", "FFFFF", "FFFFF").setOffsets(-2, -3, -2);

	public static final ArrayList<Integer> slabIds = new ArrayList(Arrays.asList((new int[] { Block.stoneSingleSlab.blockID, Block.woodSingleSlab.blockID })));

	public StructureLogicAlveary(ITileStructure structure) {
		super(UID_ALVEARY, structure);
		schematas = new Schemata[] { SCHEMATA_ALVEARY };
		metaOnValid.put(EnumStructureBlock.BLOCK_B, 1);
	}

	@Override
	protected EnumStructureState determineMasterState(Schemata schemata, boolean rotate) {
		
		Vect dimensions = schemata.getDimensions(rotate);
		int offsetX = schemata.getxOffset();
		int offsetZ = schemata.getzOffset();
		if(rotate) {
			offsetX = schemata.getzOffset();
			offsetZ = schemata.getxOffset();
		}
		
		for(int i = 0; i < dimensions.x; i++)
			for(int j = 0; j < schemata.getHeight(); j++)
				for(int k = 0; k < dimensions.z; k++) {
					int x = structureTile.xCoord + i + offsetX;
					int y = structureTile.yCoord + j + schemata.getyOffset();
					int z = structureTile.zCoord + k + offsetZ;

					if (!structureTile.worldObj.blockExists(x, y, z))
						return EnumStructureState.INDETERMINATE;
					
					EnumStructureBlock required = schemata.getAt(i, j, k, rotate);
					if(required == EnumStructureBlock.ANY)
						continue;

					TileEntity tile = structureTile.worldObj.getBlockTileEntity(x, y, z);
					int blockid = structureTile.worldObj.getBlockId(x, y, z);

					switch (required) {
					case AIR:
						if (blockid != 0)
							return EnumStructureState.INVALID;
						break;
					case BLOCK_A:
						if (tile == null || !(tile instanceof IAlvearyComponent))
							return EnumStructureState.INVALID;
						if (!((ITileStructure) tile).getTypeUID().equals(UID_ALVEARY))
							return EnumStructureState.INVALID;
						break;
					case MASTER:
					case BLOCK_B:
						if (tile == null || !(tile instanceof TileAlvearyPlain))
							return EnumStructureState.INVALID;
						break;
					case BLOCK_C:
						if (blockid != Block.stoneSingleSlab.blockID && blockid != Block.woodSingleSlab.blockID && blockid != ForestryBlock.slabs1.blockID
								&& blockid != ForestryBlock.slabs2.blockID)
							return EnumStructureState.INVALID;
						if ((structureTile.worldObj.getBlockMetadata(x, y, z) & 8) != 0)
							return EnumStructureState.INVALID;
						break;
					case BLOCK_D:
						if (blockid != Block.stairsWoodSpruce.blockID)
							return EnumStructureState.INVALID;
						break;
					case FOREIGN:
						if (tile instanceof ITileStructure)
							return EnumStructureState.INVALID;
						break;
					default:
						return EnumStructureState.INDETERMINATE;
					}
				}

		return EnumStructureState.VALID;
	}

}
