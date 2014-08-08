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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileMachine;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.LiquidTankSlot;
import forestry.factory.gadgets.MachineFermenter;

public class GuiFermenter extends GuiForestry {

	public GuiFermenter(InventoryPlayer inventory, TileMachine tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/fermenter.png", new ContainerFermenter(inventory, tile), tile);
		slotManager.add(new LiquidTankSlot(this.slotManager, 35, 19, tile, 0));
		slotManager.add(new LiquidTankSlot(this.slotManager, 125, 19, tile, 1));
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = tile.getInvName();
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {

		drawBackground();
		MachineFermenter machine = (MachineFermenter) tile.getMachine();

		// Fuel remaining
		int fuelRemain = machine.getBurnTimeRemainingScaled(16);
		if (fuelRemain > 0) {
			drawTexturedModalRect(guiLeft + 98, guiTop + 46 + 17 - fuelRemain, 176, 78 + 17 - fuelRemain, 4, fuelRemain);
		}

		// Raw bio mush remaining
		int bioRemain = machine.getFermentationProgressScaled(16);
		if (bioRemain > 0) {
			drawTexturedModalRect(guiLeft + 74, guiTop + 32 + 17 - bioRemain, 176, 60 + 17 - bioRemain, 4, bioRemain);
		}
	}

}
