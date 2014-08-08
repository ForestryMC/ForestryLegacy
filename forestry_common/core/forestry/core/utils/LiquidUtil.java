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

import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class LiquidUtil {

	public static int emptyInto(LiquidStack liquidStack, LiquidTank tank, boolean doFill) {

		int filled = 0;

		if (tank.getLiquid().amount != 0 && (tank.getLiquid().itemID != liquidStack.itemID || tank.getLiquid().itemMeta != liquidStack.itemMeta)) {
			filled = 0;
		} else if (tank.getLiquid().amount + liquidStack.amount <= tank.getCapacity()) {

			if (doFill) {
				tank.getLiquid().amount += liquidStack.amount;
				tank.setLiquid(new LiquidStack(liquidStack.itemID, tank.getLiquid().amount, liquidStack.itemMeta));
			}
			filled = liquidStack.amount;

			// Only partial space
		} else {
			int used = tank.getCapacity() - tank.getLiquid().amount;

			if (doFill) {
				tank.setLiquid(new LiquidStack(liquidStack.itemID, tank.getCapacity(), liquidStack.itemMeta));
			}

			filled = used;
		}

		return filled;
	}

}
