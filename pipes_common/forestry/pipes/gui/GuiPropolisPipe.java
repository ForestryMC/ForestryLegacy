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
package forestry.pipes.gui;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import buildcraft.transport.Pipe;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IApiaristTracker;
import forestry.apiculture.items.ItemBeeGE;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.ItemGE;
import forestry.core.gui.GfxSlot;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.pipes.EnumFilterType;
import forestry.pipes.PipeLogicPropolis;
import forestry.plugins.PluginPropolisPipe;

/**
 * GuiScreen for propolis pipes.
 * 
 * @author SirSengir
 */
public class GuiPropolisPipe extends GuiForestry {

	class TypeFilterSlot extends GfxSlot {

		ForgeDirection orientation;
		PipeLogicPropolis logic;

		public TypeFilterSlot(int x, int y, ForgeDirection orientation, PipeLogicPropolis logic) {
			super(slotManager, x, y);
			this.orientation = orientation;
			this.logic = logic;
		}

		public EnumFilterType getType() {
			return logic.getTypeFilter(orientation);
		}

		@Override
		public void draw(int startX, int startY) {
			EnumFilterType type = logic.getTypeFilter(orientation);
			int iconIndex = 0;
			if (type != null) {
				iconIndex = type.ordinal();
			}

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			int tex = mc.renderEngine.getTexture(PluginPropolisPipe.textureBees);
			mc.renderEngine.bindTexture(tex);
			int textureRow = iconIndex >> 4;
			int textureColumn = iconIndex - 16 * textureRow;
			drawTexturedModalRect(startX + xPos, startY + yPos, 16 * textureColumn, 16 * textureRow, 16, 16);

		}

		@Override
		protected String getTooltip(EntityPlayer player) {
			EnumFilterType type = logic.getTypeFilter(orientation);
			return StringUtil.localize("gui.pipe.filter." + type.toString().toLowerCase(Locale.ENGLISH));
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			EnumFilterType change;
			if (mouseButton == 1) {
				change = EnumFilterType.CLOSED;
			} else if (getType().ordinal() < EnumFilterType.values().length - 1) {
				change = EnumFilterType.values()[getType().ordinal() + 1];
			} else {
				change = EnumFilterType.CLOSED;
			}
			pipeLogic.setTypeFilter(orientation, change);

		}

	}

	class SpeciesFilterSlot extends GfxSlot {

		IApiaristTracker tracker;
		ForgeDirection orientation;
		PipeLogicPropolis logic;
		int pattern;
		int allele;

		public SpeciesFilterSlot(IApiaristTracker tracker, int x, int y, ForgeDirection orientation, int pattern, int allele, PipeLogicPropolis logic) {
			super(slotManager, x, y);
			this.tracker = tracker;
			this.orientation = orientation;
			this.pattern = pattern;
			this.allele = allele;
			this.logic = logic;
		}

		public IAlleleSpecies getSpecies() {
			return logic.getSpeciesFilter(orientation, pattern, allele);
		}

		public boolean isDefined() {
			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			return species != null;
		}

		@Override
		public void draw(int startX, int startY) {
			if (!isDefined())
				return;

			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			int tex = mc.renderEngine.getTexture(Defaults.TEXTURE_BEES);
			GL11.glDisable(GL11.GL_LIGHTING);
			mc.renderEngine.bindTexture(tex);

			for (int i = 0; i < 3; ++i) {

				int iconIndex = ((ItemBeeGE) ForestryItem.beeDroneGE).getIconIndexFromSpecies(species, i);
				int color = ((ItemGE) ForestryItem.beeDroneGE).getColourFromSpecies(species, i);
				float colorR = (color >> 16 & 255) / 255.0F;
				float colorG = (color >> 8 & 255) / 255.0F;
				float colorB = (color & 255) / 255.0F;

				GL11.glColor4f(colorR, colorG, colorB, 1.0F);
				drawTexturedModalRect(startX + xPos, startY + yPos, iconIndex % 16 * 16, iconIndex / 16 * 16, 16, 16);

			}
			GL11.glEnable(GL11.GL_LIGHTING);

		}

		@Override
		protected String getTooltip(EntityPlayer player) {
			IAlleleSpecies species = logic.getSpeciesFilter(orientation, pattern, allele);
			if (species != null)
				return species.getName();
			else
				return null;
		}

		@Override
		public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
			IAlleleSpecies change = null;
			if (mouseButton == 1) {
				change = null;
			} else if (getSpecies() == null) {

				Iterator it = AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, IAllele> entry = (Entry<String, IAllele>) it.next();
					if (!(entry.getValue() instanceof IAlleleBeeSpecies)) {
						continue;
					}

					change = (IAlleleBeeSpecies) entry.getValue();
					break;
				}

			} else {

				Iterator it = AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, IAllele> entry = (Entry<String, IAllele>) it.next();
					if (!(entry.getValue() instanceof IAlleleBeeSpecies)) {
						continue;
					}

					IAlleleBeeSpecies species = (IAlleleBeeSpecies) entry.getValue();
					if (!species.getUID().equals(getSpecies().getUID())) {
						continue;
					}

					while (it.hasNext()) {
						Entry<String, IAllele> entry2 = (Entry<String, IAllele>) it.next();
						if (!(entry2.getValue() instanceof IAlleleBeeSpecies)) {
							continue;
						}

						IAlleleBeeSpecies next = (IAlleleBeeSpecies) entry2.getValue();
						if (!Defaults.DEBUG && next.isSecret()
								&& !tracker.isDiscovered(next)) {
							continue;
						}

						change = next;
						break;
					}

					break;
				}
			}
			pipeLogic.setSpeciesFilter(orientation, pattern, allele, change);
		}
	}

	PipeLogicPropolis pipeLogic;

	public GuiPropolisPipe(EntityPlayer player, Pipe pipe) {
		super(Defaults.TEXTURE_PATH_GUI + "/analyzer.png", new ContainerPropolisPipe(player.inventory, pipe));

		pipeLogic = (PipeLogicPropolis) pipe.logic;
		// Request filter set update if on client
		if (!Proxies.common.isSimulating(pipe.worldObj)) {
			pipeLogic.requestFilterSet();
		}

		xSize = 175;
		ySize = 225;

		for (int i = 0; i < 6; i++) {
			slotManager.add(new TypeFilterSlot(8, 18 + i * 18, ForgeDirection.values()[i], pipeLogic));
		}

		IApiaristTracker tracker = BeeManager.breedingManager.getApiaristTracker(player.worldObj, player.username);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 2; k++) {
					slotManager.add(new SpeciesFilterSlot(tracker, 44 + j * 45 + k * 18, 18 + i * 18, ForgeDirection.values()[i], j, k, pipeLogic));
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		super.drawGuiContainerForegroundLayer();
		fontRenderer.drawString("Apiarist's Pipe", 56, 6, 0x303030);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		drawBackground();
	}
}
