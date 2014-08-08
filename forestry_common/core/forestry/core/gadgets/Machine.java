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

import ic2.api.Direction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.liquids.LiquidContainerData;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.network.ClassMap;
import forestry.core.network.IndexInPayload;
import forestry.core.network.PacketPayload;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.TankSlot;

public abstract class Machine extends Gadget {

	@Override
	public PacketPayload getPacketPayload() {
		if (!ClassMap.classMappers.containsKey(this.getClass())) {
			ClassMap.classMappers.put(this.getClass(), new ClassMap(this.getClass()));
		}

		ClassMap classmap = ClassMap.classMappers.get(this.getClass());
		PacketPayload payload = new PacketPayload(classmap.intSize, classmap.floatSize, classmap.stringSize);

		try {
			classmap.setData(this, payload.intPayload, payload.floatPayload, payload.stringPayload, new IndexInPayload(0, 0, 0));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload, IndexInPayload index) {

		if (payload.isEmpty())
			return;

		if (!ClassMap.classMappers.containsKey(this.getClass())) {
			ClassMap.classMappers.put(this.getClass(), new ClassMap(this.getClass()));
		}

		ClassMap classmap = ClassMap.classMappers.get(this.getClass());

		try {
			classmap.fromData(this, payload.intPayload, payload.floatPayload, payload.stringPayload, index);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Machine(TileMachine tile) {
		super(tile);
	}

	public abstract String getName();

	public IBlockRenderer customRender = null;

	public void onRemoval() {
	}

	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	// WORKING
	public abstract boolean doWork();

	// STATE INFORMATION
	public abstract boolean isWorking();

	public boolean hasResourcesMin(float percentage) {
		return false;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public boolean hasWork() {
		return false;
	}

	// / ICLIMATISED
	public boolean isClimatized() {
		return false;
	}

	public EnumTemperature getTemperature() {
		return EnumTemperature.NORMAL;
	}

	public EnumHumidity getHumidity() {
		return EnumHumidity.NORMAL;
	}

	public float getExactTemperature() {
		return 0;
	}

	public float getExactHumidity() {
		return 0;
	}

	// / LIQUID CONTAINER HANDLING
	protected ItemStack bottleIntoContainer(ItemStack canStack, ItemStack outputStack, LiquidContainerData container, TankSlot tank) {
		if (tank.quantity < container.stillLiquid.amount)
			return outputStack;
		if (canStack.stackSize <= 0)
			return outputStack;
		if (outputStack != null && !outputStack.isItemEqual(container.filled))
			return outputStack;
		if (outputStack != null && outputStack.stackSize >= outputStack.getMaxStackSize())
			return outputStack;

		tank.quantity -= container.stillLiquid.amount;
		canStack.stackSize--;

		if (outputStack == null) {
			outputStack = container.filled.copy();
		} else {
			outputStack.stackSize++;
		}

		return outputStack;
	}

	// / ADDITIONAL LIQUID HANDLING
	public EnumTankLevel getPrimaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	public EnumTankLevel getSecondaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	// REDSTONE SIGNALS
	public boolean isIndirectlyPoweringTo(IBlockAccess world, int i, int j, int k, int l) {
		return false;
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return false;
	}

	// NEIGHBOUR CHANGE
	public void onNeighborBlockChange() {
	}

	// IC2
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction) {
		return false;
	}

	public boolean isAddedToEnergyNet() {
		return false;
	}

	public int getMaxEnergyOutput() {
		return 0;
	}

	// ILINKEDENTITY
	public void performAction(TileEntity entity) {
	}
}
