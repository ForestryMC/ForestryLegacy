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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import forestry.core.network.EntityNetData;

public class TankSlot implements ILiquidTank {

	public static TankSlot FAKETANK = new TankSlot(0);
	public static TankSlot[] FAKETANK_ARRAY = new TankSlot[] { FAKETANK };
	
	@EntityNetData
	public int capacity = 0;
	@EntityNetData
	public int liquidId = 0;
	@EntityNetData
	public int liquidMeta = 0;
	@EntityNetData
	public int quantity = 0;

	public TankSlot(int capacity) {
		this.capacity = capacity;
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("liquidId", liquidId);
		nbttagcompound.setInteger("liquidMeta", liquidMeta);
		nbttagcompound.setInteger("quantity", quantity);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		liquidId = nbttagcompound.getInteger("liquidId");
		liquidMeta = nbttagcompound.getInteger("liquidMeta");

		if (liquidId != 0) {
			quantity = nbttagcompound.getInteger("quantity");
		} else {
			quantity = 0;
		}
	}

	public LiquidStack asLiquidStack() {
		return new LiquidStack(liquidId, quantity, liquidMeta);
	}

	/* ILIQUIDTANK */

	@Override
	public LiquidStack getLiquid() {
		return this.asLiquidStack();
	}

	@Override
	public int getCapacity() {
		return this.capacity;
	}

	@Override
	public int fill(LiquidStack resource, boolean doFill) {
		int filled = 0;

		if (quantity != 0 && (liquidId != resource.itemID || liquidMeta != resource.itemMeta)) {
			filled = 0;
		} else if (quantity + resource.amount <= capacity) {

			if (doFill) {
				quantity = quantity + resource.amount;
			}

			liquidId = resource.itemID;
			liquidMeta = resource.itemMeta;
			filled = resource.amount;

			// Only partial space
		} else {
			int used = capacity - quantity;

			if (doFill) {
				quantity = capacity;
			}

			liquidId = resource.itemID;
			liquidMeta = resource.itemMeta;
			filled = used;
		}

		return filled;
	}

	@Override
	public LiquidStack drain(int maxDrain, boolean doDrain) {
		int used = maxDrain;
		if (quantity < maxDrain) {
			used = quantity;
		}

		LiquidStack product = new LiquidStack(liquidId, used, liquidMeta);
		if (doDrain) {
			quantity -= used;
			// Reset liquid id if we are empty
			if (quantity <= 0 && liquidId > 0) {
				liquidId = 0;
				liquidMeta = 0;
			}
		}

		return product;
	}

	@Override
	public int getTankPressure() {
		return 0;
	}
}
