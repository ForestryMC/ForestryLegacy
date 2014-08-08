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
package forestry.cultivation.gadgets;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.Mill;
import forestry.core.gadgets.TileMill;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;
import forestry.plugins.PluginIC2;

public class MillTreetap extends Mill {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new MillTreetap((TileMill) tile);
		}
	}

	private Vect area = new Vect(21, 13, 21);
	private Vect posOffset = new Vect(-10, -2, -10);
	private Vect posCurrent = new Vect(0, 0, 0);
	private boolean isFinished = false;

	public MillTreetap(TileMill tile) {
		super(tile);
		speed = 0.025F;
	}

	@Override
	public String getName() {
		return "Treetaper";
	}

	@Override
	public void openGui(EntityPlayer player, IInventory tile) {
	}

	@Override
	protected void activate() {

		if (Proxies.common.isSimulating(tile.worldObj)) {

			while (!isFinished) {
				advanceIterator();
				Vect posBlock = posCurrent.add(tile.Coords());
				posBlock = posBlock.add(posOffset);
				int blockid = tile.worldObj.getBlockId(posBlock.x, posBlock.y, posBlock.z);
				// Not rubber wood, return.
				if (blockid != PluginIC2.rubberwood.itemID) {
					continue;
				}
				int meta = tile.worldObj.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);
				if (meta <= 0) {
					continue;
				}

				tile.worldObj.setBlockMetadataWithNotify(posBlock.x, posBlock.y, posBlock.z, 0);

				int amount = tile.worldObj.rand.nextInt(3) + 1;
				for (int i = 0; i < amount; i++) {
					EntityItem entity = new EntityItem(tile.worldObj, posBlock.x + 1, posBlock.y, posBlock.z, PluginIC2.resin.copy());
					tile.worldObj.spawnEntityInWorld(entity);
				}
				break;

			}
			if (isFinished) {
				resetIterator();
			}

			charge = 0;
			tileMill.sendNetworkUpdate();
		}
	}

	@Override
	public boolean doWork() {

		if (!PluginIC2.instance.isAvailable())
			return false;

		if (!Proxies.common.isSimulating(tile.worldObj))
			return false;

		if (charge != 0)
			return false;

		charge = 1;
		return true;
	}

	@Override
	public boolean isWorking() {
		return PluginIC2.instance.isAvailable();
	}

	// ITERATION
	private void advanceIterator() {
		// Increment z first until end reached
		if (posCurrent.z < area.z - 1) {
			posCurrent.z++;
		} else {
			posCurrent.z = 0;

			if (posCurrent.x < area.x - 1) {
				posCurrent.x++;
			} else {
				posCurrent.x = 0;

				if (posCurrent.y < area.y - 1) {
					posCurrent.y++;
				} else {
					isFinished = true;
				}
			}
		}
	}

	private void resetIterator() {
		isFinished = false;
		posCurrent = new Vect(0, 0, 0);
	}

}
