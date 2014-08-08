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
import forestry.factory.gadgets.MachineStill;

public class GuiStill extends GuiForestry {

	public GuiStill(InventoryPlayer inventory, TileMachine tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/still.png", new ContainerStill(inventory, tile), tile);
		slotManager.add(new LiquidTankSlot(this.slotManager, 35, 15, tile, 0));
		slotManager.add(new LiquidTankSlot(this.slotManager, 125, 15, tile, 1));
	}

	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = tile.getInvName();
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int i, int j) {

		drawBackground();
		MachineStill boiler = (MachineStill) tile.getMachine();

		if (boiler.isWorking()) {
			drawTexturedModalRect(guiLeft + 81, guiTop + 57, 176, 60, 14, 14);
		}

		int massRemaining = boiler.getDistillationProgressScaled(16);
		if (massRemaining > 0) {
			drawTexturedModalRect(guiLeft + 84, guiTop + 17 + 17 - massRemaining, 176, 74 + 17 - massRemaining, 4, massRemaining);
		}

	}

}
