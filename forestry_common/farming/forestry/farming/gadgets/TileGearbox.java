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

import net.minecraft.nbt.NBTTagCompound;
import forestry.api.core.ITileStructure;
import forestry.api.farming.IFarmHousing;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;

public class TileGearbox extends TileFarm  implements IPowerReceptor {

	public static int MIN_ENERGY_RECEIVED = 5;
	public static int MAX_ENERGY_RECEIVED = 75;
	public static int MIN_ACTIVATION_ENERGY = 150;
	public static int MAX_ENERGY = 1050;
	
	public static int TEXTURE_SHIFT = 64;
	
	private int activationDelay = 0;
	private int previousDelays = 0;
	
	public TileGearbox() {
		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(Defaults.MACHINE_LATENCY, MIN_ENERGY_RECEIVED,
				Math.round(MAX_ENERGY_RECEIVED  * GameMode.getGameMode().getEnergyDemandModifier()),
				Math.round(MIN_ACTIVATION_ENERGY  * GameMode.getGameMode().getEnergyDemandModifier()),
				Math.round(MAX_ENERGY * GameMode.getGameMode().getEnergyDemandModifier()));
		
		textureShift = TEXTURE_SHIFT;
	}

	@Override
	protected void createInventory() {
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* UPDATING */
	protected void updateServerSide() {
		if(powerProvider != null)
			powerProvider.update(this);
	}
	
	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		PowerFramework.currentFramework.loadPowerProvider(this, nbttagcompound);
		
		activationDelay = nbttagcompound.getInteger("ActivationDelay");
		previousDelays = nbttagcompound.getInteger("PrevDelays");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		PowerFramework.currentFramework.savePowerProvider(this, nbttagcompound);
		
		nbttagcompound.setInteger("ActivationDelay", activationDelay);
		nbttagcompound.setInteger("PrevDelays", previousDelays);
	}
	
	/* IPOWERRECEPTOR */
	IPowerProvider powerProvider;
	
	@Override
	public void setPowerProvider(IPowerProvider provider) {
		this.powerProvider = provider;
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public void doWork() {
		
		if(activationDelay > 0) {
			activationDelay--;
			return;
		}

		// Hard limit to 4 cycles / second.
		if (worldObj.getWorldTime() % 5 * 10 != 0)
			return;

		ITileStructure central = getCentralTE();
		if(!(central instanceof IFarmHousing))
			return;
		
		if(((IFarmHousing)central).doWork()) {
			powerProvider.useEnergy(MIN_ACTIVATION_ENERGY, MIN_ACTIVATION_ENERGY, true);
			previousDelays = 0;
		} else {
			// If the central TE doesn't have work, we add to the activation delay to throttle the CPU usage.
			activationDelay = 10*previousDelays < 120 ? 10*previousDelays : 120;
			previousDelays++; // First delay is free!
		}
	}

	@Override
	public int powerRequest() {
		if(getPowerProvider().getEnergyStored() >= getPowerProvider().getMaxEnergyStored())
			return 0;
		if(!isIntegratedIntoStructure())
			return 0;
		if(getCentralTE() == null)
			return 0;
		
		return getPowerProvider().getMaxEnergyReceived();
	}

}
