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

import buildcraft.api.core.Position;
import buildcraft.api.power.IPowerReceptor;
import ic2.api.Direction;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerData;
import forestry.core.TemperatureState;
import forestry.core.network.PacketPayload;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.TankSlot;
import forestry.plugins.PluginBuildCraft;

public abstract class Engine extends TileBase {

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(6, 0, 0);

		payload.intPayload[0] = maxEnergy;
		payload.intPayload[1] = maxEnergyExtracted;
		payload.intPayload[2] = storedEnergy;
		payload.intPayload[3] = maxHeat;
		payload.intPayload[4] = heat;

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		maxEnergy = payload.intPayload[0];
		maxEnergyExtracted = payload.intPayload[1];
		storedEnergy = payload.intPayload[2];
		maxHeat = payload.intPayload[3];
		heat = payload.intPayload[4];
	}

	public boolean isActive = false; // Used for smp.
	public int stagePiston = 0; // Indicates whether the piston is receding from
								// or approaching the combustion chamber
	public float pistonSpeedServer = 0; // Piston speed as supplied by the

	protected int currentOutput = 0;
	public int maxEnergy;
	public int maxEnergyExtracted;
	public int storedEnergy;
	public int heat;
	protected int maxHeat;
	protected boolean forceCooldown = false;

	public float progress;

	public Engine() {
	}

	protected ItemStack replenishByContainer(ItemStack inventoryStack, LiquidContainerData container, TankSlot tank) {
		if (container == null)
			return inventoryStack;

		if (tank.fill(container.stillLiquid, false) >= container.stillLiquid.amount) {
			tank.fill(container.stillLiquid, true);
			if (container.filled != null && container.filled.getItem().hasContainerItem()) {
				inventoryStack = container.container.copy();
			} else {
				inventoryStack.stackSize--;
			}
		}

		return inventoryStack;
	}

	/**
	 * Adds energy
	 * 
	 * @param addition
	 */
	public void addEnergy(int addition) {
		storedEnergy += addition;

		if (storedEnergy > maxEnergy) {
			storedEnergy = maxEnergy;
		}
	}

	/**
	 * 
	 * @param min
	 *            Minimum energy to extract. Will return 0 if storedEnergy < min.
	 * @param max
	 *            Maximum energy to extract.
	 * @param doExtract
	 *            Determines whether energy will actually be removed from the engine.
	 * @return
	 */
	public int extractEnergy(int min, int max, boolean doExtract) {
		if (storedEnergy < min)
			return 0;

		int ceiling;

		// Determine maximum energy that can be extracted
		if (max > maxEnergyExtracted) {
			ceiling = maxEnergyExtracted;
		} else {
			ceiling = max;
		}

		int extracted;

		if (storedEnergy >= ceiling) {
			extracted = ceiling;
			if (doExtract) {
				storedEnergy -= ceiling;
			}
		} else {
			extracted = storedEnergy;
			if (doExtract) {
				storedEnergy = 0;
			}
		}

		return extracted;
	}

	/**
	 * Adds heat
	 * 
	 * @param i
	 */
	protected void addHeat(int i) {
		heat += i;

		if (heat > maxHeat) {
			heat = maxHeat;
		}
	}

	public abstract int dissipateHeat();

	public abstract int generateHeat();

	public int maxEnergyReceived() {
		return 200;
	}

	public boolean mayBurn() {
		return !forceCooldown;
	}

	public abstract void burn();

	@Override
	public void updateClientSide() {
		if (stagePiston != 0) {
			progress += pistonSpeedServer;

			if (progress > 1) {
				stagePiston = 0;
				progress = 0;
			}
		} else if (this.isActive) {
			stagePiston = 1;
		}
		return;
	}
	
	@Override
	public void updateServerSide() {
		TemperatureState energyState = getTemperatureState();
		if (energyState == TemperatureState.MELTING && heat > 0) {
			forceCooldown = true;
		} else if (forceCooldown && heat <= 0) {
			forceCooldown = false;
		}
		
		// Determine targeted tile
		Position posTarget = new Position(xCoord, yCoord, zCoord, this.getOrientation());
		posTarget.moveForwards(1.0);
		TileEntity tile = worldObj.getBlockTileEntity((int) posTarget.x, (int) posTarget.y, (int) posTarget.z);

		float newPistonSpeed = getPistonSpeed();
		if (newPistonSpeed != pistonSpeedServer) {
			pistonSpeedServer = newPistonSpeed;
			sendNetworkUpdate();
		}

		if (stagePiston != 0) {

			progress += pistonSpeedServer;

			if (progress > 0.5 && stagePiston == 1) {
				stagePiston = 2;

				if (BlockUtil.isPoweredTile(tile)) {
					IPowerReceptor receptor = (IPowerReceptor) tile;
					int extractedEnergy = extractEnergy(receptor.getPowerProvider().getMinEnergyReceived(), receptor.getPowerProvider()
							.getMaxEnergyReceived(), true);
					if (extractedEnergy > 0) {
						PluginBuildCraft.instance.invokeReceiveEnergyMethod(receptor.getPowerProvider(), extractedEnergy);
						// receptor.getPowerProvider().receiveEnergy(extractedEnergy);
					}
				}

			} else if (progress >= 1) {
				progress = 0;
				stagePiston = 0;
			}

		} else if (canPowerTo(tile)) { // If we are not already running, check if
			IPowerReceptor receptor = (IPowerReceptor) tile;
			if (extractEnergy(receptor.getPowerProvider().getMinEnergyReceived(), receptor.getPowerProvider().getMaxEnergyReceived(), false) > 0) {
				stagePiston = 1; // If we can transfer energy, start running
				setActive(true);
			} else {
				setActive(false);
			}
		} else {
			setActive(false);
		}

		dissipateHeat();
		generateHeat();
		// Now let's fire up the engine:
		if (mayBurn()) {
			burn();
		} else {
			extractEnergy(0, 2, true);
		}

	}

	private boolean canPowerTo(TileEntity tile) {
		return isActivated() && BlockUtil.isPoweredTile(tile);
	}
	
	private void setActive(boolean isActive) {
		if (this.isActive == isActive)
			return;

		this.isActive = isActive;
		sendNetworkUpdate();
	}

	/* INTERACTION */
	public void rotateEngine() {

		for (int i = getOrientation().ordinal() + 1; i <= getOrientation().ordinal() + 6; ++i) {
			ForgeDirection orient = ForgeDirection.values()[i % 6];

			Position pos = new Position(xCoord, yCoord, zCoord, orient);
			pos.moveForwards(1.0F);

			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (BlockUtil.isPoweredTile(tile)) {
				setOrientation(orient);
				worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);

				break;
			}
		}
	}

	// STATE INFORMATION
	protected double getHeatLevel() {
		return (double) heat / (double) maxHeat;
	}

	public abstract boolean isBurning();

	public int getBurnTimeRemainingScaled(int i) {
		return 0;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public int getCurrentOutput() {
		if (isBurning() && isActivated())
			return currentOutput;
		else
			return 0;
	}

	public int getEnergyStored() {
		return storedEnergy;
	}

	public int getHeat() {
		return heat;
	}

	/**
	 * Returns the current energy state of the engine
	 * 
	 * @return
	 */
	public TemperatureState getTemperatureState() {
		// double scaledStorage = (double)storedEnergy / (double)maxEnergy;
		double scaledHeat = (double) heat / (double) maxHeat;

		if (scaledHeat < 0.20)
			return TemperatureState.COOL;
		else if (scaledHeat < 0.45)
			return TemperatureState.WARMED_UP;
		else if (scaledHeat < 0.65)
			return TemperatureState.OPERATING_TEMPERATURE;
		else if (scaledHeat < 0.85)
			return TemperatureState.RUNNING_HOT;
		else if (scaledHeat < 1.0)
			return TemperatureState.OVERHEATING;
		else
			return TemperatureState.MELTING;
	}

	/**
	 * Piston speed
	 * 
	 * @return
	 */
	public float getPistonSpeed() {
		switch (getTemperatureState()) {
		case COOL:
			return 0.03f;
		case WARMED_UP:
			return 0.04f;
		case OPERATING_TEMPERATURE:
			return 0.05f;
		case RUNNING_HOT:
			return 0.06f;
		case OVERHEATING:
			return 0.07f;
		case MELTING:
			return 0.08f;
		default:
			return 0;
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		heat = nbttagcompound.getInteger("EngineHeat");
		storedEnergy = nbttagcompound.getInteger("EngineStoredEnergy");
		progress = nbttagcompound.getFloat("EngineProgress");
		forceCooldown = nbttagcompound.getBoolean("ForceCooldown");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("EngineHeat", heat);
		nbttagcompound.setInteger("EngineStoredEnergy", storedEnergy);
		nbttagcompound.setFloat("EngineProgress", progress);
		nbttagcompound.setBoolean("ForceCooldown", forceCooldown);
	}

	/* SMP GUI */
	public abstract void getGUINetworkData(int i, int j);
	public abstract void sendGUINetworkData(Container containerEngine, ICrafting iCrafting);

	// / IENERGYSINK IMPLEMENTATION
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
		return false;
	}

	public boolean isAddedToEnergyNet() {
		return false;
	}

	public int demandsEnergy() {
		return 0;
	}

	public int injectEnergy(Direction directionFrom, int amount) {
		return 0;
	}

	// / ISOCKETABLE
	public int getSocketCount() {
		return 0;
	}

	public ItemStack getSocket(int slot) {
		return null;
	}

	public void setSocket(int slot, ItemStack stack) {
	}

}
