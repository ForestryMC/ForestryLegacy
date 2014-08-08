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
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import forestry.core.proxy.Proxies;

/**
 * Basic non-ItemStack slot
 */
public abstract class GfxSlot {

	protected GfxSlotManager manager;
	protected int xPos;
	protected int yPos;
	protected int width = 16;
	protected int height = 16;

	public GfxSlot(GfxSlotManager manager, int xPos, int yPos) {
		this.manager = manager;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public abstract void draw(int startX, int startY);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getItemNameandInformation() {
		ArrayList list = new ArrayList();
		if (getTooltip(Proxies.common.getClientInstance().thePlayer) != null) {
			list.add(getTooltip(Proxies.common.getClientInstance().thePlayer).trim());
		}
		addInformation(list, Proxies.common.getClientInstance().thePlayer);
		return list;
	}

	protected abstract String getTooltip(EntityPlayer player);

	@SuppressWarnings("rawtypes")
	protected void addInformation(ArrayList list, EntityPlayer player) {
	}

	public boolean intersectsWith(int mouseX, int mouseY) {

		if (mouseX >= xPos && mouseX <= xPos + this.width && mouseY >= yPos && mouseY <= yPos + this.height)
			return true;

		return false;
	}

	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
	}
}
