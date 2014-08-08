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

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import forestry.core.config.Defaults;

public class SlotLiquidContainer extends SlotCustom implements ITextureSlot {

	private boolean isEmpty;

	public SlotLiquidContainer(IInventory iinventory, int i, int j, int k) {
		this(iinventory, i, j, k, false);
	}

	public SlotLiquidContainer(IInventory iinventory, int i, int j, int k, boolean isEmpty) {
		super(iinventory, i, j, k);

		this.isEmpty = isEmpty;
		ArrayList container = new ArrayList();

		for (LiquidContainerData cont : LiquidContainerRegistry.getRegisteredLiquidContainerData())
			if (isEmpty) {
				container.add(cont.container);
			} else {
				container.add(cont.filled);
			}

		this.items = container.toArray();
	}

	public int getBackgroundIconIndex() {
		if (isEmpty)
			return 5;
		else
			return 6;
	}

	@Override
	public String getTextureFile() {
		return Defaults.TEXTURE_ICONS_MISC;
	}

}
