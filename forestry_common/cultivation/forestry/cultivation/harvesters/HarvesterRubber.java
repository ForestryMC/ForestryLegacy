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
package forestry.cultivation.harvesters;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import forestry.api.cultivation.ICropProvider;
import forestry.core.gadgets.Machine;
import forestry.core.gadgets.MachineFactory;
import forestry.core.gadgets.TileMachine;
import forestry.plugins.CropProviderRubber;
import forestry.plugins.PluginIC2;

public class HarvesterRubber extends Harvester {

	public static class Factory extends MachineFactory {
		@Override
		public Machine createMachine(TileEntity tile) {
			return new HarvesterRubber((TileMachine) tile);
		}
	}

	public HarvesterRubber(TileMachine tile) {
		super(tile);
		if (PluginIC2.instance.isAvailable()) {
			ICropProvider provider = new CropProviderRubber();
			cropProviders.add(provider);
			ItemStack[] windfall = provider.getWindfall();
			if (windfall != null && windfall.length > 0) {
				for (ItemStack itemstack : windfall) {
					putWindfall(itemstack);
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Rubber Tree Harvester";
	}

	@Override
	public boolean doWork() {
		if (!PluginIC2.instance.isAvailable())
			return false;

		return super.doWork();
	}

	public static String dir_3 = ".tech";
}
