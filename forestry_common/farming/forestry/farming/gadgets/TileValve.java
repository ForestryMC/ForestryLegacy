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
package forestry.farming.gadgets;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.utils.TankSlot;

public class TileValve extends TileFarm implements ILiquidTankContainer {

	public static int TEXTURE_SHIFT = 96;

	public TileValve() {
		textureShift = TEXTURE_SHIFT;
	}

	/* TILEFARM */
	@Override
	public boolean hasFunction() {
		return true;
	}

	@Override
	protected void createInventory() {
	}

	/* ILIQUIDTANKCONTAINER */
	private TankSlot getMasterTank() {
		if(!isIntegratedIntoStructure() || !hasMaster())
			return null;
		
		return ((TileFarmPlain)getCentralTE()).getTank();
	}
	
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		TankSlot tank = getMasterTank();
		if(tank == null)
			return 0;
		
		return tank.fill(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		TankSlot tank = getMasterTank();
		if(tank == null)
			return null;
		
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		TankSlot tank = getMasterTank();
		if(tank == null)
			return TankSlot.FAKETANK_ARRAY;
		
		return new ILiquidTank[] { tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		TankSlot tank = getMasterTank();
		if(tank == null)
			return TankSlot.FAKETANK;
		
		return tank;
	}

}
