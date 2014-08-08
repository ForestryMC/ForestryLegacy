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
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import buildcraft.api.tools.IToolPipette;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class ContainerLiquidTanks extends ContainerForestry {

	private ILiquidTankContainer tanks;

	public ContainerLiquidTanks(IInventory inventory, ILiquidTankContainer tanks) {
		this(inventory, tanks, inventory.getSizeInventory());
	}

	public ContainerLiquidTanks(IInventory inventory, ILiquidTankContainer tanks, int slotCount) {
		super(inventory, slotCount);
		this.tanks = tanks;
	}

	public void handlePipetteClick(int slot, EntityPlayer player) {

		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null)
			return;

		Item held = itemstack.getItem();
		if (!(held instanceof IToolPipette))
			return;

		if (!Proxies.common.isSimulating(player.worldObj)) {

			PacketPayload payload = new PacketPayload(1, 0, 0);
			payload.intPayload[0] = slot;
			Proxies.net.sendToServer(new PacketUpdate(PacketIds.PIPETTE_CLICK, payload));
			return;

		}

		IToolPipette pipette = (IToolPipette) held;
		int liquidAmount = tanks.getTanks(ForgeDirection.UNKNOWN)[slot].getLiquid().amount;
		if (pipette.canPipette(itemstack) && liquidAmount > 0) {

			if (liquidAmount > 0) {
				int filled = pipette.fill(itemstack, tanks.getTanks(ForgeDirection.UNKNOWN)[slot].drain(1000, false), true);
				tanks.getTanks(ForgeDirection.UNKNOWN)[slot].drain(filled, true);
			}

		} else {

			ILiquidTank tank = tanks.getTanks(ForgeDirection.UNKNOWN)[slot];
			LiquidStack potential = pipette.drain(itemstack, pipette.getCapacity(itemstack), false);
			if (potential != null) {
				pipette.drain(itemstack, tank.fill(potential, true), true);
			}

		}
	}

}
