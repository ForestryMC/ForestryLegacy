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
package forestry.apiculture.gadgets;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.core.ForestryAPI;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GenericInventoryAdapter;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.TankSlot;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidStack;

public class TileAlvearyHygroregulator extends TileAlveary implements IInventory, ILiquidTankContainer {

	public static final int TEXTURE_EMPTY = 73;
	
	private static class HygroregulatorRecipe {
		public final LiquidStack liquid;
		public final int transferTime;
		public final float humidChange;
		public final float tempChange;
		
		public HygroregulatorRecipe(LiquidStack liquid, int transferTime, float humidChange, float tempChange) {
			this.liquid = liquid;
			this.transferTime = transferTime;
			this.humidChange = humidChange;
			this.tempChange = tempChange;
		}
	}
	
	private HygroregulatorRecipe[] recipes;
	
	GenericInventoryAdapter canInventory = new GenericInventoryAdapter(1, "CanInv");
	private TankSlot liquidTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
	
	private HygroregulatorRecipe currentRecipe;
	private int transferTime;

	public TileAlvearyHygroregulator() {
		super(5);
		
		recipes = new HygroregulatorRecipe[] {
				new HygroregulatorRecipe(new LiquidStack(Block.waterStill, 1), 1, 0.01f, -0.005f),
				new HygroregulatorRecipe(new LiquidStack(Block.lavaStill, 1), 10, -0.01f, +0.005f),
				new HygroregulatorRecipe(new LiquidStack(ForestryItem.liquidIce, 1), 10, 0.02f, -0.01f)
		};
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.HygroregulatorGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public String getInvName() {
		return "tile.alveary.5";
	}

	/* UPDATING */
	private HygroregulatorRecipe getRecipe(LiquidStack liquid) {
		HygroregulatorRecipe recipe = null;
		for(HygroregulatorRecipe rec : recipes) {
			if(rec.liquid.isLiquidEqual(liquid)) {
				recipe = rec;
				break;
			}
		}
		return recipe;
	}
	
	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if(transferTime <= 0 && liquidTank.quantity > 0) {
			currentRecipe = getRecipe(liquidTank.getLiquid());
			
			if(currentRecipe != null) {
				liquidTank.drain(currentRecipe.liquid.amount, true);
				transferTime = currentRecipe.transferTime;
			}
		}
		
		if(transferTime > 0) {

			transferTime--;
			if(currentRecipe != null) {
				IAlvearyComponent component = (IAlvearyComponent) this.getCentralTE();
				if(component != null) {
					component.addHumidityChange(currentRecipe.humidChange, 0.0f, 1.0f);
					component.addTemperatureChange(currentRecipe.tempChange, 0.0f, 2.0f);
				}
			} else
				transferTime = 0;
		}

		if (worldObj.getWorldTime() % 20 * 10 != 0)
			return;

		// Check if we have suitable items waiting in the item slot
		if (canInventory.getStackInSlot(0) != null) {
			LiquidContainerData container = LiquidHelper.getLiquidContainer(canInventory.getStackInSlot(0));
			if (container != null && (container.stillLiquid.itemID == Block.waterStill.blockID
					|| container.stillLiquid.itemID == Block.lavaStill.blockID)) {

				canInventory.setInventorySlotContents(0, StackUtils.replenishByContainer(this, canInventory.getStackInSlot(0), container, liquidTank));
				if (canInventory.getStackInSlot(0).stackSize <= 0) {
					canInventory.setInventorySlotContents(0, null);
				}
			}
		}

	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		
		canInventory.readFromNBT(nbttagcompound);
		
		liquidTank = new TankSlot(Defaults.PROCESSOR_TANK_CAPACITY);
		if (nbttagcompound.hasKey("LiquidTank")) {
			liquidTank.readFromNBT(nbttagcompound.getCompoundTag("LiquidTank"));
		}

		transferTime = nbttagcompound.getInteger("TransferTime");

		if(nbttagcompound.hasKey("CurrentLiquid")) {
			LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbttagcompound.getCompoundTag("CurrentLiquid"));
			currentRecipe = getRecipe(liquid);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
		canInventory.writeToNBT(nbttagcompound);
		
		NBTTagCompound NBTresourceSlot = new NBTTagCompound();
		liquidTank.writeToNBT(NBTresourceSlot);
		nbttagcompound.setTag("LiquidTank", NBTresourceSlot);

		nbttagcompound.setInteger("TransferTime", transferTime);
		if(currentRecipe != null) {
			NBTTagCompound subcompound = new NBTTagCompound();
			currentRecipe.liquid.writeToNBT(subcompound);
			nbttagcompound.setTag("CurrentLiquid", subcompound);
		}

	}
	
	/* TEXTURES */
	@Override
	public int getBlockTexture(int side, int metadata) {
		return TEXTURE_EMPTY;
	}

	
	@Override
	public IInventory getInventory() {
		return canInventory;
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		if (canInventory != null)
			return canInventory.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if (canInventory != null)
			return canInventory.getStackInSlot(slotIndex);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if (canInventory != null)
			return canInventory.decrStackSize(slotIndex, amount);
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if (canInventory != null)
			return canInventory.getStackInSlotOnClosing(slotIndex);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		// Client side handling for container synch
		if (canInventory == null && !Proxies.common.isSimulating(worldObj)) {
			createInventory();
		}

		if (canInventory != null) {
			canInventory.setInventorySlotContents(slotIndex, itemstack);
		}
	}

	@Override
	public int getInventoryStackLimit() {
		if (canInventory != null)
			return canInventory.getInventoryStackLimit();
		else
			return 0;
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

	/* ILIQUIDTANKCONTAINER */
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return liquidTank.fill(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return liquidTank.drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[] { liquidTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return liquidTank;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {

		switch (i) {
		case 0:
			liquidTank.liquidId = j;
			break;
		case 1:
			liquidTank.quantity = j;
			break;
		case 2:
			liquidTank.liquidMeta = j;
			break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, liquidTank.liquidId);
		iCrafting.sendProgressBarUpdate(container, 1, liquidTank.quantity);
		iCrafting.sendProgressBarUpdate(container, 2, liquidTank.liquidMeta);
	}

}
