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
package forestry.apiculture.worldgen;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ComponentVillageBeeHut extends ComponentVillageBeeHouse {

	public ComponentVillageBeeHut(ComponentVillageStartPiece startPiece, int componentType, Random random, StructureBoundingBox boundingBox, int coordBaseMode) {
		super(startPiece, componentType, random, boundingBox, coordBaseMode);
	}

	public static ComponentVillageBeeHut buildComponent(ComponentVillageStartPiece startPiece, List par1List, Random random, int par3, int par4, int par5,
			int par6, int par7) {
		StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 9, 6, par6);
		return canVillageGoDeeper(bbox) && StructureComponent.findIntersecting(par1List, bbox) == null ? new ComponentVillageBeeHut(startPiece, par7, random,
				bbox, par6) : null;
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox structBoundingBox) {

		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, structBoundingBox);
			if (this.averageGroundLevel < 0)
				return true;

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 9 - 1, 0);
		}

		// Groundwork
		this.fillWithBlocks(world, structBoundingBox, 4, 1, 1, 6, 5, 4, 0, 0, false);
		this.fillWithBlocks(world, structBoundingBox, 3, 0, 0, 6, 0, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 4, 0, 1, 5, 0, 3, Block.dirt.blockID, Block.dirt.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 1, 0, 6, 8, 0, 10, Block.dirt.blockID, Block.dirt.blockID, false);

		// Garden fence
		this.fillWithBlocks(world, structBoundingBox, 1, 1, 6, 1, 1, 10, Block.fence.blockID, Block.fence.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 8, 1, 6, 8, 1, 10, Block.fence.blockID, Block.fence.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 2, 1, 10, 7, 1, 10, Block.fence.blockID, Block.fence.blockID, false);

		// Flowers
		plantFlowerGarden(world, structBoundingBox, 2, 1, 5, 7, 1, 9);

		// Apiaries
		buildApiaries(world, structBoundingBox, 3, 1, 4, 6, 1, 8);

		// Outer roofing
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 4, 4, 0, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 5, 4, 0, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 4, 4, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 5, 4, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 3, 4, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 3, 4, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 3, 4, 3, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 6, 4, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 6, 4, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.wood.blockID, 0, 6, 4, 3, structBoundingBox);

		// Walls
		this.fillWithBlocks(world, structBoundingBox, 3, 1, 0, 3, 3, 0, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 6, 1, 0, 6, 3, 0, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 3, 1, 4, 3, 3, 4, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 6, 1, 4, 6, 3, 4, Block.wood.blockID, Block.wood.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 3, 1, 1, 3, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 6, 1, 1, 6, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 4, 1, 0, 5, 3, 0, Block.planks.blockID, Block.planks.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 4, 1, 4, 5, 3, 4, Block.planks.blockID, Block.planks.blockID, false);
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 3, 2, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 6, 2, 2, structBoundingBox);

		this.spawnVillagers(world, boundingBox, 7, 1, 1, 1);
		return true;

	}
}
