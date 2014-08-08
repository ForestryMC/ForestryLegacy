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

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginForestryArboriculture;

public class BlockLeaves extends BlockTreeContainer {

	int[] adjacentTreeBlocks;

	public BlockLeaves(int id) {
		super(id, Material.leaves);
		this.setTextureFile(Defaults.TEXTURE_ARBORICULTURE);
		this.setTickRandomly(true);
		this.setRequiresSelfNotify();
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.setStepSound(soundGrassFootstep);
	}

	public static TileLeaves getLeafTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (!(tile instanceof TileLeaves))
			return null;

		return (TileLeaves) tile;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileLeaves();
	}

	/* DROP HANDLING */
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, metadata)) {
			
			ItemStack held = player.inventory.getCurrentItem();
			float saplingModifier = 1.0f;
			if(held != null && held.getItem() instanceof IToolGrafter) {
				saplingModifier = ((IToolGrafter)held.getItem()).getSaplingModifier(held, world, x, y, z);
			}
			
			spawnLeafDrops(world, x, y, z, metadata, saplingModifier);
		}
		
		return world.setBlockWithNotify(x, y, z, 0);
	}
	
	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		// Disabled.
		return new ArrayList<ItemStack>();
	}

	private void removeLeaves(World world, int x, int y, int z) {
		this.spawnLeafDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 1.0f);
		world.setBlockWithNotify(x, y, z, 0);
	}

	private void spawnLeafDrops(World world, int x, int y, int z, int metadata, float saplingModifier) {
		for(ItemStack drop : getLeafDrop(world, x, y, z, metadata, saplingModifier)) {
			if(drop != null)
				StackUtils.dropItemStackAsEntity(drop, world, x, y, z);
		}
	}
	
	private ArrayList<ItemStack> getLeafDrop(World world, int x, int y, int z, int metadata, float saplingModifier) {
		ArrayList<ItemStack> prod = new ArrayList<ItemStack>();

		TileLeaves tile = getLeafTile(world, x, y, z);
		if (tile == null)
			return prod;

		if (tile.getTree() == null)
			return prod;

		// Add saplings
		ITree[] saplings = tile.getTree().getSaplings(world, x, y, z, saplingModifier);
		for (ITree sapling : saplings) {
			if(sapling != null)
				prod.add(TreeManager.treeInterface.getGermlingStack(sapling, EnumGermlingType.SAPLING));
		}

		// Add fruits
		if(tile.hasFruit()) {
			for(ItemStack stack : tile.getTree().produceStacks(world, x, y, z, tile.getRipeningTime()))
				prod.add(stack);
		}
		
		return prod;		
	}
	
	/* RENDERING */
	@Override
	public boolean isOpaqueCube() {
        return !Proxies.render.fancyGraphicsEnabled();
	}

	@Override
	public int getRenderType() {
		return PluginForestryArboriculture.modelIdLeaves;
	}

	@SideOnly(Side.CLIENT)
	public int getBlockColor() {
		double var1 = 0.5D;
		double var3 = 1.0D;
		return ColorizerFoliage.getFoliageColor(var1, var3);
	}

	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {

		TileLeaves leaves = getLeafTile(world, x, y, z);
		if (leaves == null)
			return ColorizerFoliage.getFoliageColorBasic();

		int colour = leaves.getFoliageColour();
		if(colour == PluginForestryArboriculture.proxy.getFoliageColorBasic())
			colour = world.getBiomeGenForCoords(x, z).getBiomeFoliageColor();

		return colour;
	}

	@Override
	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileLeaves leaves = getLeafTile(world, x, y, z);
		if (leaves != null)
			return leaves.getTextureIndex(Proxies.render.fancyGraphicsEnabled());

		return 48;
	}

	/* BREAKING, LEAF DECAY */
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {

		super.updateTick(world, x, y, z, random);
		if (!Proxies.common.isSimulating(world))
			return;

		int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) != 0 && (meta & 4) == 0) {
			byte offset = 4;
			int shift = offset + 1;
			byte var9 = 32;
			int var10 = var9 * var9;
			int var11 = var9 / 2;

			if (this.adjacentTreeBlocks == null) {
				this.adjacentTreeBlocks = new int[var9 * var9 * var9];
			}

			int var12;

			if (world.checkChunksExist(x - shift, y - shift, z - shift, x + shift, y + shift, z + shift)) {

				int var13;
				int var14;
				int var15;

				for (var12 = -offset; var12 <= offset; ++var12) {
					for (var13 = -offset; var13 <= offset; ++var13) {
						for (var14 = -offset; var14 <= offset; ++var14) {
							var15 = world.getBlockId(x + var12, y + var13, z + var14);

							Block block = Block.blocksList[var15];

							if (block != null && block.canSustainLeaves(world, x + var12, y + var13, z + var14)) {
								this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = 0;
							} else if (block != null && block.isLeaves(world, x + var12, y + var13, z + var14)) {
								this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -2;
							} else {
								this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -1;
							}
						}
					}
				}

				for (var12 = 1; var12 <= 4; ++var12) {
					for (var13 = -offset; var13 <= offset; ++var13) {
						for (var14 = -offset; var14 <= offset; ++var14) {
							for (var15 = -offset; var15 <= offset; ++var15) {
								if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11] == var12 - 1) {

									if (this.adjacentTreeBlocks[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2) {
										this.adjacentTreeBlocks[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;
									}

									if (this.adjacentTreeBlocks[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2) {
										this.adjacentTreeBlocks[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;
									}

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] == -2) {
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] = var12;
									}

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] == -2) {
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] = var12;
									}

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] == -2) {
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] = var12;
									}

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] == -2) {
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] = var12;
									}
								}
							}
						}
					}
				}
			}

			var12 = this.adjacentTreeBlocks[var11 * var10 + var11 * var9 + var11];

			if (var12 >= 0) {
				world.setBlockMetadata(x, y, z, meta & -9);
			} else {
				this.removeLeaves(world, x, y, z);
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {

		byte offset = 1;
		int shift = offset + 1;

		if (world.checkChunksExist(x - shift, y - shift, z - shift, x + shift, y + shift, z + shift)) {
			for (int i = -offset; i <= offset; ++i) {
				for (int j = -offset; j <= offset; ++j) {
					for (int k = -offset; k <= offset; ++k) {
						int block = world.getBlockId(x + i, y + j, z + k);
						if (Block.blocksList[block] != null) {
							Block.blocksList[block].beginLeavesDecay(world, x + i, y + j, z + k);
						}
					}
				}
			}
		}
		
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public void beginLeavesDecay(World world, int x, int y, int z) {
		world.setBlockMetadata(x, y, z, world.getBlockMetadata(x, y, z) | 8);
	}

	/* PROPERTIES */
	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return 60;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face) {
		if (face == ForgeDirection.DOWN)
			return 20;
		else if (face != ForgeDirection.UP)
			return 10;
		else
			return 5;
	}

	@Override
	public boolean isLeaves(World world, int x, int y, int z) {
		return true;
	}

}
