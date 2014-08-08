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
package forestry.mail.gui;

import net.minecraft.entity.player.InventoryPlayer;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.utils.StringUtil;
import forestry.mail.gadgets.MachineTrader;

public class GuiTrader extends GuiForestry {

	private ContainerTrader container;

	public GuiTrader(InventoryPlayer inventoryplayer, MachineTrader tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/mailtrader.png", new ContainerTrader(inventoryplayer, tile), tile);
		this.xSize = 226;
		this.ySize = 220;

		this.container = (ContainerTrader) inventorySlots;
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		String name = StringUtil.localize(tile.getInvName());
		this.fontRenderer.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.mail.text"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		this.drawBackground();

		fontRenderer.drawString(container.getMoniker(), guiLeft + 19, guiTop + 22, fontColor.get("gui.mail.text"));

	}
}
