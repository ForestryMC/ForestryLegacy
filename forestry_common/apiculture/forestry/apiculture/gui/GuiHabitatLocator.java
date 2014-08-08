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
package forestry.apiculture.gui;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.biome.BiomeGenBase;

import org.lwjgl.opengl.GL11;

import forestry.apiculture.items.ItemHabitatLocator.HabitatLocatorInventory;
import forestry.core.config.Defaults;
import forestry.core.gui.GfxSlot;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class GuiHabitatLocator extends GuiForestry {

	public class HabitatSlot extends GfxSlot {

		private final int slot;
		private final String name;
		private final int iconIndex;
		public boolean isActive = false;

		public HabitatSlot(int slot, String name) {
			super(slotManager, 0, 0);
			this.slot = slot;
			this.name = name;
			this.iconIndex = slot;
		}

		@Override
		public String getTooltip(EntityPlayer player) {
			return name;
		}

		public int getIconIndex() {
			return iconIndex;
		}

		public String getTexture() {
			return Defaults.TEXTURE_HABITATS;
		}

		public void setPosition(int x, int y) {
			this.xPos = x;
			this.yPos = y;
		}

		@Override
		public void draw(int startX, int startY) {
			if (getTexture() != null && !getTexture().equals("")) {
				int texture = mc.renderEngine.getTexture(getTexture());
				mc.renderEngine.bindTexture(texture);

				int textureI = getIconIndex() >> 4;
				int textureJ = getIconIndex() - textureI * 16;

				if (!isActive) {
					GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.2f);
				} else {
					GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				}
				drawTexturedModalRect(startX + xPos, startY + yPos, 16 * textureJ, 16 * textureI, 16, 16);
			}
		}

	}

	private HabitatSlot[] habitatSlots = new HabitatSlot[] { new HabitatSlot(0, "Ocean"), // ocean
																							// +
																							// beach
			new HabitatSlot(1, "Plains"), new HabitatSlot(2, "Desert"), // desert
																		// +
																		// desert
																		// hills
			new HabitatSlot(3, "Forest"), // forest, forestHills, river
			new HabitatSlot(4, "Jungle"), // jungle, jungleHills
			new HabitatSlot(5, "Taiga"), // taiga, taigaHills
			new HabitatSlot(6, "Hills"), // extremeHills, extremeHillsEdge
			new HabitatSlot(7, "Swampland"), new HabitatSlot(8, "Snow"), // Ice
																			// plains,
																			// mountains,
																			// frozen
																			// rivers,
																			// frozen
																			// oceans
			new HabitatSlot(9, "Mushroom"), // Ice plains, mountains, frozen
											// rivers, frozen oceans
			new HabitatSlot(10, "Hell"), new HabitatSlot(11, "End") };
	private HashMap<Integer, HabitatSlot> biomeToHabitat = new HashMap<Integer, HabitatSlot>();

	private int startX;
	private int startY;

	public GuiHabitatLocator(InventoryPlayer inventory, HabitatLocatorInventory item) {
		super(Defaults.TEXTURE_PATH_GUI + "/biomefinder.png", new ContainerHabitatLocator(inventory, item), item, 1, item.getSizeInventory());

		xSize = 176;
		ySize = 184;

		int x;
		int y;
		for (HabitatSlot slot : habitatSlots) {

			if (slot.slot > 5) {
				x = 18 + (slot.slot - 6) * 20;
				y = 50;
			} else {
				x = 18 + slot.slot * 20;
				y = 32;
			}

			slot.setPosition(x, y);

		}

		biomeToHabitat.put(BiomeGenBase.ocean.biomeID, habitatSlots[0]);
		biomeToHabitat.put(BiomeGenBase.beach.biomeID, habitatSlots[0]);
		biomeToHabitat.put(BiomeGenBase.plains.biomeID, habitatSlots[1]);
		biomeToHabitat.put(BiomeGenBase.desert.biomeID, habitatSlots[2]);
		// biomeToHabitat.put(BiomeGenBase.desertHills.biomeID,
		// habitatSlots[2]); // Removed for TFC compatibility
		biomeToHabitat.put(BiomeGenBase.forest.biomeID, habitatSlots[3]);
		biomeToHabitat.put(BiomeGenBase.forestHills.biomeID, habitatSlots[3]);
		biomeToHabitat.put(BiomeGenBase.river.biomeID, habitatSlots[3]);
		biomeToHabitat.put(BiomeGenBase.jungle.biomeID, habitatSlots[4]);
		biomeToHabitat.put(BiomeGenBase.jungleHills.biomeID, habitatSlots[4]);
		biomeToHabitat.put(BiomeGenBase.taiga.biomeID, habitatSlots[5]);
		biomeToHabitat.put(BiomeGenBase.taigaHills.biomeID, habitatSlots[5]);
		biomeToHabitat.put(BiomeGenBase.extremeHills.biomeID, habitatSlots[6]);
		biomeToHabitat.put(BiomeGenBase.extremeHillsEdge.biomeID, habitatSlots[6]);
		biomeToHabitat.put(BiomeGenBase.swampland.biomeID, habitatSlots[7]);
		biomeToHabitat.put(BiomeGenBase.frozenOcean.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.frozenRiver.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.iceMountains.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.icePlains.biomeID, habitatSlots[8]);
		biomeToHabitat.put(BiomeGenBase.mushroomIsland.biomeID, habitatSlots[9]);
		biomeToHabitat.put(BiomeGenBase.mushroomIslandShore.biomeID, habitatSlots[9]);
		biomeToHabitat.put(BiomeGenBase.hell.biomeID, habitatSlots[10]);
		biomeToHabitat.put(BiomeGenBase.sky.biomeID, habitatSlots[11]);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {

		drawBackground();

		String str = StringUtil.localize("gui.habitatlocator").toUpperCase();
		fontRenderer.drawString(str, startX + 8 + getCenteredOffset(str, 138), startY + 16, fontColor.get("gui.screen"));

		str = "(" + StringUtil.localize("gui.closetosearch") + ")";
		fontRenderer.drawString(str, startX + 8 + getCenteredOffset(str, 138), startY + 76, fontColor.get("gui.table.header"));

		// Reset habitat slots
		for (HabitatSlot slot : habitatSlots) {
			slot.isActive = false;
		}

		// Set according to valid biomes.
		ArrayList<Integer> biomeids = ((ContainerHabitatLocator) inventorySlots).inventory.biomesToSearch;
		for (int biomeid : biomeids) {
			if (!biomeToHabitat.containsKey(biomeid)) {
				continue;
			}

			biomeToHabitat.get(biomeid).isActive = true;
		}

		for (HabitatSlot slot : habitatSlots) {
			slot.draw(startX, startY);
		}
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Reset afterwards.

	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		drawForegroundSelection();
	}

	protected void drawForegroundSelection() {
		String description = "";

		int cornerX = (width - xSize) / 2;
		int cornerY = (height - ySize) / 2;

		int position = getSlotAtLocation(lastX - cornerX, lastY - cornerY);

		if (position != -1) {
			HabitatSlot slot = habitatSlots[position];

			if (slot != null) {
				description = slot.getTooltip(Proxies.common.getClientInstance().thePlayer);
			}
		}

		if (description.length() > 0) {
			int i2 = (lastX - cornerX) + 12;
			int k2 = lastY - cornerY - 12;
			int l2 = fontRenderer.getStringWidth(description);
			drawGradientRect(i2 - 3, k2 - 3, i2 + l2 + 3, k2 + 8 + 3, 0xc0000000, 0xc0000000);
			fontRenderer.drawStringWithShadow(description, i2, k2, -1);
		}
	}

	private int lastX = 0;
	private int lastY = 0;

	@Override
	protected void mouseMovedOrUp(int i, int j, int k) {
		super.mouseMovedOrUp(i, j, k);

		lastX = i;
		lastY = j;
	}

	public int getSlotAtLocation(int i, int j) {
		for (int position = 0; position < habitatSlots.length; ++position) {
			HabitatSlot slot = habitatSlots[position];
			if (slot.intersectsWith(i, j))
				return position;
			/*
			 * if (i >= slot.xPos && i <= slot.xPos + 16 && j >= slot.yPos && j <= slot.yPos + 16) { return position; }
			 */
		}
		return -1;
	}

	@Override
	public void initGui() {
		super.initGui();

		startX = (this.width - this.xSize) / 2;
		startY = (this.height - this.ySize) / 2;
	}

}
