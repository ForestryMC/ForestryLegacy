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
package forestry.factory.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.Mill;
import forestry.core.gadgets.TileMill;
import forestry.core.proxy.Proxies;

public class MillRainmaker extends Mill {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MillRainmaker((TileMill) tile);
		}
	}

	private int duration;
	private boolean reverse;

	public MillRainmaker(TileMill tile) {
		super(tile);
		tileMill = tile;
		speed = 0.01f;
	}

	@Override
	public String getName() {
		return "Rainmaker";
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
		if (!Proxies.common.isSimulating(player.worldObj))
			return;

		if (player.inventory.getCurrentItem() == null)
			return;

		// We don't have a gui, but we can be activated
		int usedItemId = player.inventory.getCurrentItem().itemID;
		if (FuelManager.rainSubstrate.containsKey(player.inventory.getCurrentItem()) && charge == 0) {
			RainSubstrate substrate = FuelManager.rainSubstrate.get(usedItemId);
			if (substrate.item.isItemEqual(player.inventory.getCurrentItem())) {
				addCharge(substrate);
				player.inventory.getCurrentItem().stackSize--;
			}
		}
		tileMill.sendNetworkUpdate();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		charge = nbttagcompound.getInteger("Charge");
		progress = nbttagcompound.getFloat("Progress");
		stage = nbttagcompound.getInteger("Stage");
		duration = nbttagcompound.getInteger("Duration");
		reverse = nbttagcompound.getBoolean("Reverse");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Charge", charge);
		nbttagcompound.setFloat("Progress", progress);
		nbttagcompound.setInteger("Stage", stage);
		nbttagcompound.setInteger("Duration", duration);
		nbttagcompound.setBoolean("Reverse", reverse);
	}

	public void addCharge(RainSubstrate substrate) {
		charge = 1;
		speed = substrate.speed;
		duration = substrate.duration;
		reverse = substrate.reverse;
	}

	@Override
	public void activate() {
		if (Proxies.render.hasRendering()) {
			tileMill.worldObj.playSoundEffect(tileMill.xCoord, tileMill.yCoord, tileMill.zCoord, "ambient.weather.thunder", 4F,
					(1.0F + (tileMill.worldObj.rand.nextFloat() - tileMill.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

			float f = tile.xCoord + 0.5F;
			float f1 = tile.yCoord + 0.0F + (tile.worldObj.rand.nextFloat() * 6F) / 16F;
			float f2 = tile.zCoord + 0.5F;
			float f3 = 0.52F;
			float f4 = tile.worldObj.rand.nextFloat() * 0.6F - 0.3F;

			Proxies.common.addEntityExplodeFX(tile.worldObj, (f - f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntityExplodeFX(tile.worldObj, (f + f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntityExplodeFX(tile.worldObj, (f + f4), f1, (f2 - f3), 0F, 0F, 0F);
			Proxies.common.addEntityExplodeFX(tile.worldObj, (f + f4), f1, (f2 + f3), 0F, 0F, 0F);
		}

		if (Proxies.common.isSimulating(tile.worldObj)) {
			if (reverse) {
				tile.worldObj.getWorldInfo().setRaining(false);
			} else {
				tile.worldObj.getWorldInfo().setRaining(true);
				tile.worldObj.getWorldInfo().setRainTime(duration);
			}
			charge = 0;
			duration = 0;
			reverse = false;
			tileMill.sendNetworkUpdate();
		}
	}

	@Override
	public boolean doWork() {
		return false;
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (charge != 0)
			return 0;

		if (!FuelManager.rainSubstrate.containsKey(stack))
			return 0;

		RainSubstrate substrate = FuelManager.rainSubstrate.get(stack);
		if (!substrate.item.isItemEqual(stack))
			return 0;

		if (doAdd) {
			addCharge(substrate);
		}
		return 1;
	}
}
