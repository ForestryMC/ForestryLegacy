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
import forestry.factory.gadgets.MachineSqueezer;

public class GuiSqueezer extends GuiForestry {

	public GuiSqueezer(InventoryPlayer inventory, TileMachine tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/squeezer.png", new ContainerSqueezer(inventory, tile), tile);
		slotManager.add(new LiquidTankSlot(this.slotManager, 85, 15, tile, 0));
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = tile.getInvName();
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		drawBackground();
		MachineSqueezer machine = (MachineSqueezer) tile.getMachine();

		int progress = machine.getProgressScaled(43);
		drawTexturedModalRect(guiLeft + 75, guiTop + 20, 176, 60, 43 - progress, 18);
	}

}
