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
package forestry.core.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import forestry.core.config.Config;
import forestry.core.proxy.Proxies;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;

public class Utils {

	private static EntityPlayer modPlayer;
	
	public static EntityPlayer getForestryPlayer(World world, int x, int y, int z) {
		if(modPlayer == null) {
			modPlayer = new EntityPlayer(world) {

				@Override
				public void sendChatToPlayer(String message) {
				}

				@Override
				public boolean canCommandSenderUseCommand(int var1, String var2) {
					return false;
				}

				@Override
				public ChunkCoordinates getPlayerCoordinates() {
					return null;
				}
			
			};
			modPlayer.username = Config.fakeUserLogin;
			modPlayer.posX = x;
			modPlayer.posY = y;
			modPlayer.posZ = z;
			Proxies.log.info("Created player '%s' for Forestry.", modPlayer.username);
			if(Config.fakeUserAutoop) {
				MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).addOp(modPlayer.username);
				Proxies.log.info("Opped player '%s'.", modPlayer.username);
			}
		}
		
		return modPlayer;
	}
	
	private static Random rand;
	
	public static int getUID() {
		if(rand == null)
			rand = new Random();
		
		return rand.nextInt();
	}
	
	@SuppressWarnings("rawtypes")
	public static void broadcastMessage(World world, String message) {
		for (Iterator iterator = world.playerEntities.iterator(); iterator.hasNext();) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();
			entityplayer.addChatMessage(message);
		}
	}

	public static IInventory getChest(IInventory inventory) {
		if (!(inventory instanceof TileEntityChest))
			return inventory;

		TileEntityChest chest = (TileEntityChest) inventory;

		Vect[] adjacent = new Vect[] { new Vect(chest.xCoord + 1, chest.yCoord, chest.zCoord), new Vect(chest.xCoord - 1, chest.yCoord, chest.zCoord),
				new Vect(chest.xCoord, chest.yCoord, chest.zCoord + 1), new Vect(chest.xCoord, chest.yCoord, chest.zCoord - 1) };

		for (Vect pos : adjacent) {
			TileEntity otherchest = chest.worldObj.getBlockTileEntity(pos.x, pos.y, pos.z);
			if (otherchest instanceof TileEntityChest)
				return new InventoryLargeChest("", chest, (TileEntityChest) otherchest);
		}

		return inventory;
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static int[] concat(int[] first, int[] second) {
		int[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static short[] concat(short[] first, short[] second) {
		short[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static float[] concat(float[] first, float[] second) {
		float[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static boolean isWrench(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() instanceof IToolWrench;
	}

	public static EnumTankLevel rateTankLevel(int scaled) {

		if (scaled < 5)
			return EnumTankLevel.EMPTY;
		else if (scaled < 30)
			return EnumTankLevel.LOW;
		else if (scaled < 60)
			return EnumTankLevel.MEDIUM;
		else if (scaled < 90)
			return EnumTankLevel.HIGH;
		else
			return EnumTankLevel.MAXIMUM;
	}
	
	public static boolean isReplaceableBlock(World world, int x, int y, int z) {
		int blockid = world.getBlockId(x, y, z);
		
		return blockid == Block.vine.blockID || blockid == Block.tallGrass.blockID || blockid == Block.deadBush.blockID
                || blockid == Block.snow.blockID || (Block.blocksList[blockid] != null && Block.blocksList[blockid].isBlockReplaceable(world, x, y, z));
	}
	
	public static boolean isUseableByPlayer(EntityPlayer player, TileEntity tile, World world, int x, int y, int z) {
		if (world.getBlockTileEntity(x, y, z) != tile)
			return false;

		return player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;

	}

	public static Entity spawnEntity(World world, Class<? extends Entity> entityClass, double x, double y, double z) {
		if(!EntityList.classToStringMapping.containsKey(entityClass))
			return null;
		
        Entity spawn = EntityList.createEntityByName((String)EntityList.classToStringMapping.get(entityClass), world);

        if (spawn != null && spawn instanceof EntityLiving) {
        	
            EntityLiving living = (EntityLiving)spawn;
            spawn.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
            living.rotationYawHead = living.rotationYaw;
            living.renderYawOffset = living.rotationYaw;
            living.initCreature();
            world.spawnEntityInWorld(spawn);
            living.playLivingSound();
        }

        return spawn;
	}
	
}
