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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.ComponentVillage;
import net.minecraft.world.gen.structure.ComponentVillageStartPiece;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.EnumTolerance;
import forestry.apiculture.gadgets.MachineApiary;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.TileMachine;
import forestry.core.genetics.ClimateHelper;

public class ComponentVillageBeeHouse extends ComponentVillage {

	protected ItemStack[] buildingBlocks = new ItemStack[] { new ItemStack(ForestryBlock.planks, 1, 15), new ItemStack(Block.wood, 1, 0), };
	protected int averageGroundLevel = -1;

	public ComponentVillageBeeHouse(ComponentVillageStartPiece startPiece, int componentType, Random random, StructureBoundingBox boundingBox, int coordBaseMode) {
		super(startPiece, componentType);
		this.coordBaseMode = coordBaseMode;
		this.boundingBox = boundingBox;
	}

	public static ComponentVillageBeeHouse buildComponent(ComponentVillageStartPiece startPiece, List par1List, Random random, int par3, int par4, int par5,
			int par6, int par7) {
		StructureBoundingBox bbox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 9, 9, 6, par6);
		return canVillageGoDeeper(bbox) && StructureComponent.findIntersecting(par1List, bbox) == null ? new ComponentVillageBeeHouse(startPiece, par7, random,
				bbox, par6) : null;
	}

	protected boolean isDesertVillage() {
		return this.startPiece.inDesert;
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox structBoundingBox) {

		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, structBoundingBox);
			if (this.averageGroundLevel < 0)
				return true;

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 8 - 1, 0);
		}

		this.fillWithBlocks(world, structBoundingBox, 1, 1, 1, 7, 4, 4, 0, 0, false);
		this.fillWithBlocks(world, structBoundingBox, 2, 1, 6, 8, 4, 10, 0, 0, false);

		// Garden
		buildGarden(world, structBoundingBox);

		// Garden fence
		this.fillWithBlocks(world, structBoundingBox, 1, 1, 6, 1, 1, 10, Block.fence.blockID, Block.fence.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 8, 1, 6, 8, 1, 10, Block.fence.blockID, Block.fence.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 2, 1, 10, 7, 1, 10, Block.fence.blockID, Block.fence.blockID, false);

		// Flowers
		plantFlowerGarden(world, structBoundingBox, 2, 1, 5, 7, 1, 9);

		// Apiaries
		buildApiaries(world, structBoundingBox, 3, 1, 4, 6, 1, 8);

		// Floor
		this.fillWithBlocks(world, structBoundingBox, 1, 0, 1, 7, 0, 4, Block.planks.blockID, Block.planks.blockID, false);

		this.fillWithBlocks(world, structBoundingBox, 0, 0, 0, 0, 3, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 8, 0, 0, 8, 3, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 1, 0, 0, 7, 1, 0, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
		this.fillWithBlocks(world, structBoundingBox, 1, 0, 5, 7, 1, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);

		this.fillBoxWith(world, structBoundingBox, 1, 2, 0, 7, 3, 0, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 1, 2, 5, 7, 3, 5, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 0, 4, 1, 8, 4, 1, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 0, 4, 4, 8, 4, 4, buildingBlocks[0], false);
		this.fillBoxWith(world, structBoundingBox, 0, 5, 2, 8, 5, 3, buildingBlocks[0], false);

		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 0, 4, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 0, 4, 3, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 8, 4, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 8, 4, 3, structBoundingBox);

		buildRoof(world, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 0, 2, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 0, 2, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 8, 2, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, buildingBlocks[1], 8, 2, 4, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 0, 2, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 0, 2, 3, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 8, 2, 2, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 8, 2, 3, structBoundingBox);

		// Windows garden side
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 2, 2, 5, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 3, 2, 5, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 4, 2, 5, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 5, 2, 0, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, Block.thinGlass.blockID, 0, 6, 2, 5, structBoundingBox);

		// Table/Bench
		this.placeBlockAtCurrentPosition(world, buildingBlocks[0], 1, 1, 3, structBoundingBox);

		this.placeBlockAtCurrentPosition(world, 0, 0, 2, 1, 0, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, 0, 0, 2, 2, 0, structBoundingBox);
		this.placeDoorAtCurrentPosition(world, structBoundingBox, random, 2, 1, 0, this.getMetadataWithOffset(Block.doorWood.blockID, 1));

		if (this.getBlockIdAtCurrentPosition(world, 2, 0, -1, structBoundingBox) == 0
				&& this.getBlockIdAtCurrentPosition(world, 2, -1, -1, structBoundingBox) != 0) {
			this.placeBlockAtCurrentPosition(world, Block.stairCompactCobblestone.blockID,
					this.getMetadataWithOffset(Block.stairCompactCobblestone.blockID, 3), 2, 0, -1, structBoundingBox);
		}

		this.placeBlockAtCurrentPosition(world, 0, 0, 6, 1, 5, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, 0, 0, 6, 2, 5, structBoundingBox);

		// Candles / Lighting
		this.placeBlockAtCurrentPosition(world, ForestryBlock.candle.blockID, 0, 2, 3, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, ForestryBlock.candle.blockID, 0, 6, 3, 4, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, ForestryBlock.candle.blockID, 0, 2, 3, 1, structBoundingBox);
		this.placeBlockAtCurrentPosition(world, ForestryBlock.candle.blockID, 0, 6, 3, 1, structBoundingBox);

		this.placeDoorAtCurrentPosition(world, structBoundingBox, random, 6, 1, 5, this.getMetadataWithOffset(Block.doorWood.blockID, 1));

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.clearCurrentPositionBlocksUpwards(world, j, 7, i, structBoundingBox);
				this.fillCurrentPositionBlocksDownwards(world, Block.cobblestone.blockID, 0, j, -1, i, structBoundingBox);
			}
		}

		this.spawnVillagers(world, boundingBox, 7, 1, 1, 1);
		return true;
	}

	private void buildRoof(World world, StructureBoundingBox structBoundingBox) {
		int rotatedMetaDoor = this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 3);
		int rotatedMetaGarden = this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 2);

		for (int i = -1; i <= 2; ++i) {
			for (int j = 0; j <= 8; ++j) {
				this.placeBlockAtCurrentPosition(world, Block.stairCompactPlanks.blockID, rotatedMetaDoor, j, 4 + i, i, structBoundingBox);
				this.placeBlockAtCurrentPosition(world, Block.stairCompactPlanks.blockID, rotatedMetaGarden, j, 4 + i, 5 - i, structBoundingBox);
			}
		}
	}

	protected void buildGarden(World world, StructureBoundingBox box) {

		int groundId = Block.dirt.blockID;
		if (isDesertVillage()) {
			groundId = Block.sand.blockID;
		}

		for (int i = 1; i <= 8; i++) {
			for (int j = 6; j <= 10; j++) {
				fillCurrentPositionBlocksDownwards(world, groundId, 0, i, 0, j, box);
			}
		}
	}

	protected void plantFlowerGarden(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

		if (isDesertVillage()) {
			placeBlockAtCurrentPosition(world, Block.cactus.blockID, 0, 4, 1, 7, box);
			return;
		}

		for (int i = minY; i <= maxY; ++i) {
			for (int j = minX; j <= maxX; ++j) {
				for (int k = minZ; k <= maxZ; ++k) {
					if (world.rand.nextBoolean()) {
						int xCoord = this.getXWithOffset(j, k);
						int yCoord = this.getYWithOffset(i);
						int zCoord = this.getZWithOffset(j, k);

						if (!Block.plantRed.canBlockStay(world, xCoord, yCoord, zCoord)) {
							continue;
						}

						ItemStack flower = FlowerManager.plainFlowers.get(world.rand.nextInt(FlowerManager.plainFlowers.size()));
						this.placeBlockAtCurrentPosition(world, flower.itemID, flower.getItemDamage(), j, i, k, box);
					}
				}
			}
		}
	}

	protected void buildApiaries(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		populateApiary(world, box, 3, 1, 8);
		populateApiary(world, box, 6, 1, 8);
	}

	private void populateApiary(World world, StructureBoundingBox box, int x, int y, int z) {
		int xCoord = this.getXWithOffset(x, z);
		int yCoord = this.getYWithOffset(y);
		int zCoord = this.getZWithOffset(x, z);

		if (box.isVecInside(xCoord, yCoord, zCoord) && world.getBlockId(xCoord, yCoord, zCoord) != ForestryBlock.machine.blockID
				&& world.blockExists(xCoord, yCoord - 1, zCoord)) {

			world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, ForestryBlock.machine.blockID, Defaults.ID_PACKAGE_MACHINE_APIARY);
			ForestryBlock.machine.onBlockAdded(world, xCoord, yCoord, zCoord);

			TileEntity tile = world.getBlockTileEntity(xCoord, yCoord, zCoord);
			if (tile instanceof TileMachine) {
				TileMachine apiary = ((TileMachine) tile);
				apiary.initialize();
				apiary.setInventorySlotContents(MachineApiary.SLOT_QUEEN,
						BeeManager.beeInterface.getBeeStack(getVillageBee(world, xCoord, yCoord, zCoord), EnumBeeType.PRINCESS));
				apiary.setInventorySlotContents(MachineApiary.SLOT_DRONE,
						BeeManager.beeInterface.getBeeStack(getVillageBee(world, xCoord, yCoord, zCoord), EnumBeeType.DRONE));

				for (int i = MachineApiary.SLOT_FRAMES_1; i < MachineApiary.SLOT_FRAMES_1 + MachineApiary.SLOT_FRAMES_COUNT; i++) {
					float roll = world.rand.nextFloat();
					if (roll < 0.2f) {
						apiary.setInventorySlotContents(i, new ItemStack(ForestryItem.frameUntreated));
					} else if (roll < 0.4f) {
						apiary.setInventorySlotContents(i, new ItemStack(ForestryItem.frameImpregnated));
					} else if (roll < 0.6) {
						apiary.setInventorySlotContents(i, new ItemStack(ForestryItem.frameProven));
					}
				}

			}
		}
	}

	private IBee getVillageBee(World world, int xCoord, int yCoord, int zCoord) {

		// Get current biome
		BiomeGenBase biome = world.getBiomeGenForCoords(xCoord, zCoord);

		ArrayList<IBeeGenome> candidates;
		if (BeeManager.villageBees[1] != null && BeeManager.villageBees[1].size() > 0 && world.rand.nextDouble() < 0.2) {
			candidates = BeeManager.villageBees[1];
		} else {
			candidates = BeeManager.villageBees[0];
		}

		// Add bees that can live in this environment
		ArrayList<IBeeGenome> valid = new ArrayList<IBeeGenome>();
		for (IBeeGenome genome : candidates)
			if (checkBiomeHazard(genome, biome.temperature, biome.rainfall)) {
				valid.add(genome);
			}

		// No valid ones found, return any of the common ones.
		if (valid.isEmpty())
			return BeeManager.beeInterface.getBee(world, BeeManager.villageBees[0].get(world.rand.nextInt(BeeManager.villageBees[0].size())));

		return BeeManager.beeInterface.getBee(world, valid.get(world.rand.nextInt(valid.size())));
	}

	private boolean checkBiomeHazard(IBeeGenome genome, float temperature, float humidity) {

		EnumTemperature beeTemperature = genome.getPrimaryAsBee().getTemperature();
		EnumTolerance temperatureTolerance = genome.getToleranceTemp();

		ArrayList<EnumTemperature> toleratedTemperatures = ClimateHelper.getToleratedTemperature(beeTemperature, temperatureTolerance);
		boolean validTemp = false;

		validTemp = toleratedTemperatures.contains(ClimateHelper.getTemperature(temperature));

		if (!validTemp)
			return false;

		EnumHumidity beeHumidity = genome.getPrimaryAsBee().getHumidity();
		EnumTolerance humidityTolerance = genome.getToleranceHumid();

		ArrayList<EnumHumidity> toleratedHumidity = ClimateHelper.getToleratedHumidity(beeHumidity, humidityTolerance);

		boolean validHumidity = false;

		validHumidity = toleratedHumidity.contains(ClimateHelper.getHumidity(humidity));

		return validHumidity;
	}

	protected void fillBoxWith(World world, StructureBoundingBox box, int par3, int par4, int par5, int par6, int par7, int par8, ItemStack buildingBlock,
			boolean replace) {

		for (int var14 = par4; var14 <= par7; ++var14) {
			for (int var15 = par3; var15 <= par6; ++var15) {
				for (int var16 = par5; var16 <= par8; ++var16) {
					if (!replace || this.getBlockIdAtCurrentPosition(world, var15, var14, var16, box) != 0) {
						this.placeBlockAtCurrentPosition(world, buildingBlock.itemID, buildingBlock.getItemDamage(), var15, var14, var16, box);
					}
				}
			}
		}
	}

	protected void placeBlockAtCurrentPosition(World world, ItemStack buildingBlock, int par4, int par5, int par6, StructureBoundingBox par7StructureBoundingBox) {
		int var8 = this.getXWithOffset(par4, par6);
		int var9 = this.getYWithOffset(par5);
		int var10 = this.getZWithOffset(par4, par6);

		if (par7StructureBoundingBox.isVecInside(var8, var9, var10)) {
			world.setBlockAndMetadata(var8, var9, var10, buildingBlock.itemID, buildingBlock.getItemDamage());
		}
	}

	@Override
	protected int getVillagerType(int villagerCount) {
		return Defaults.ID_VILLAGER_BEEKEEPER;
	}

}
