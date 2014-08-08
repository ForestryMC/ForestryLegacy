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
package forestry.core.gadgets;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.core.ITileStructure;
import forestry.core.CreativeTabForestry;
import forestry.core.interfaces.IOwnable;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;

public abstract class BlockForestry extends BlockContainer {

	protected static boolean keepInventory = false;
	protected Random furnaceRand;

	public BlockForestry(int i, Material material) {
		super(i, material);
		setHardness(1.5f);
		furnaceRand = new Random();
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		IOwnable tile = (IOwnable) world.getBlockTileEntity(x, y, z);
		if (tile.allowsRemoval(player))
			return super.removeBlockByPlayer(world, player, x, y, z);
		else
			return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {

		if (!Proxies.common.isSimulating(world))
			return;

		TileForestry tile = (TileForestry) world.getBlockTileEntity(x, y, z);

		// Release inventory
		
		if(tile instanceof ITileStructure) {
			
			IInventory inventory = ((ITileStructure)tile).getInventory();
			if(inventory != null) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (inventory.getStackInSlot(i) == null) {
						continue;
					}

					StackUtils.dropItemStackAsEntity(inventory.getStackInSlot(i), world, x, y, z);
					inventory.setInventorySlotContents(i, null);
				}
			}
			
		} else if (tile instanceof IInventory) {
			
			IInventory inventory = (IInventory) tile;

			if (inventory != null) {
				for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {

					ItemStack itemstack = inventory.getStackInSlot(slot);

					if (itemstack == null) {
						continue;
					}

					float f = furnaceRand.nextFloat() * 0.8F + 0.1F;
					float f1 = furnaceRand.nextFloat() * 0.8F + 0.1F;
					float f2 = furnaceRand.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0) {

						int stackPartial = furnaceRand.nextInt(21) + 10;
						if (stackPartial > itemstack.stackSize) {
							stackPartial = itemstack.stackSize;
						}
						ItemStack drop = itemstack.splitStack(stackPartial);
						EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, drop);
						float accel = 0.05F;
						entityitem.motionX = (float) furnaceRand.nextGaussian() * accel;
						entityitem.motionY = (float) furnaceRand.nextGaussian() * accel + 0.2F;
						entityitem.motionZ = (float) furnaceRand.nextGaussian() * accel;
						world.spawnEntityInWorld(entityitem);

					}

					inventory.setInventorySlotContents(slot, null);
				}
			}
		}

		// Call removal function on machine
		if (tile != null) {
			tile.onRemoval();
		}

		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {

		if (!Proxies.common.isSimulating(world))
			return;

		TileForestry tile = (TileForestry) world.getBlockTileEntity(i, j, k);
		if (entityliving instanceof EntityPlayer) {
			tile.owner = ((EntityPlayer) entityliving).username;
		}
	}
}
