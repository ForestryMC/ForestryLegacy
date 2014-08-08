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

import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumRarity;

import org.lwjgl.opengl.GL11;

import forestry.core.proxy.Proxies;

public class GfxSlotManager {

	public GuiForestry gui;
	public Minecraft minecraft;

	protected ArrayList<GfxSlot> slots = new ArrayList<GfxSlot>();

	public GfxSlotManager(GuiForestry gui) {
		this.gui = gui;
		this.minecraft = Proxies.common.getClientInstance();
	}

	public void add(GfxSlot slot) {
		this.slots.add(slot);
	}

	public void remove(GfxSlot slot) {
		this.slots.remove(slot);
	}
	
	public void clear() {
		this.slots.clear();
	}
	
	protected GfxSlot getAtPosition(int mX, int mY) {
		for (GfxSlot slot : slots)
			if (slot.intersectsWith(mX, mY))
				return slot;

		return null;
	}

	public void drawSlots() {
		for (GfxSlot slot : slots) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			slot.draw(0, 0);
		}
	}

	public void drawTooltips(int mouseX, int mouseY) {

		GfxSlot slot = getAtPosition(mouseX - gui.guiLeft, mouseY - gui.guiTop);
		if (slot != null) {
			gui.drawTooltip(mouseX, mouseY, 300f, slot.getItemNameandInformation(), EnumRarity.common);
		}

	}

	public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
		GfxSlot slot = getAtPosition(mouseX - gui.guiLeft, mouseY - gui.guiTop);
		if (slot != null) {
			slot.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}
}
