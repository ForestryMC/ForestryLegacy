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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.IApiaristTracker;
import forestry.api.genetics.IMutation;
import forestry.apiculture.genetics.BeeTemplates;
import forestry.apiculture.items.ItemBeeGE;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.EnumMutateChance;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;
import forestry.core.utils.StringUtil;

public class GuiApiaristInventory extends GuiForestry {

	private IInventory inventory;

	private IApiaristTracker breedingTracker;
	private HashMap<String, ItemStack> iconStacks = new HashMap<String, ItemStack>();

	public GuiApiaristInventory(EntityPlayer player, ContainerForestry container, IInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/apiaristinventory.png", container, inventory, 5, 25);

		this.inventory = inventory;

		xSize = 196;
		ySize = 202;

		ArrayList<ItemStack> beeList = new ArrayList<ItemStack>();
		((ItemBeeGE) ForestryItem.beeDroneGE).addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			iconStacks.put(BeeManager.beeInterface.getBee(beeStack).getIdent(), beeStack);
		}

		breedingTracker = BeeManager.breedingManager.getApiaristTracker(player.worldObj, player.username);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		int tex = this.mc.renderEngine.getTexture(textureFile);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(tex);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
		String header = StringUtil.localize("gui.page") + " " + (pageCurrent + 1) + "/" + pageMax;
		fontRenderer.drawString(header, guiLeft + 95 + getCenteredOffset(header, 98), guiTop + 10, fontColor.get("gui.title"));

		IBee bee = getBeeAtPosition(i, j);
		if (bee == null) {
			displayBreedingStatistics(10);
		}

		if (bee != null) {
			RenderHelper.enableGUIStandardItemLighting();
			startPage();
			
			displaySpeciesInformation(true, bee.getGenome().getPrimaryAsBee(), iconStacks.get(bee.getIdent()), 10);
			if (!bee.isPureBred(EnumBeeChromosome.SPECIES)) {
				displaySpeciesInformation(bee.isAnalyzed(), bee.getGenome().getSecondaryAsBee(), iconStacks.get(bee.getGenome().getSecondaryAsBee().getUID()),
						10);
			}
			
			endPage();
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if (mc.thePlayer == null)
			return;

		if (inventorySlots instanceof ContainerItemInventory) {
			((ContainerItemInventory) inventorySlots).purgeBag(mc.thePlayer);
		}

		if (!Proxies.common.isSimulating(Proxies.common.getRenderWorld()))
			return;

		if (inventory instanceof ItemInventory) {
			ItemInventory inv = ((ItemInventory) inventory);
			if (inv.isItemInventory) {
				inv.onGuiSaved(mc.thePlayer);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		controlList.add(new GuiButton(1, guiLeft + 99, guiTop + 7, 10, 12, "<"));
		controlList.add(new GuiButton(2, guiLeft + 179, guiTop + 7, 10, 12, ">"));
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		super.actionPerformed(guibutton);

		if (guibutton.id == 1 && pageCurrent > 0) {
			flipPage(pageCurrent - 1);
		} else if (guibutton.id == 2 && pageCurrent < pageMax - 1) {
			flipPage(pageCurrent + 1);
		}
	}

	private IBee getBeeAtPosition(int x, int y) {
		Slot slot = getSlotAtPosition(x, y);
		if (slot == null)
			return null;

		if (!slot.getHasStack())
			return null;

		if(!slot.getStack().hasTagCompound())
			return null;

		if (!BeeManager.beeInterface.isBee(slot.getStack()))
			return null;

		return BeeManager.beeInterface.getBee(slot.getStack());
	}
	
	private void displayBreedingStatistics(int x) {
		
		startPage();
		newLine();
		
		drawLine(StringUtil.localize("gui.speciescount") + ": " + breedingTracker.getSpeciesBred() + "/" + BeeManager.breedingManager.getBeeSpeciesCount(), x);
		newLine();
		newLine();
		
		drawLine(StringUtil.localize("gui.queens") + ": " + breedingTracker.getQueenCount(), x);
		newLine();
		
		drawLine(StringUtil.localize("gui.princesses") + ": " + breedingTracker.getPrincessCount(), x);
		newLine();
		
		drawLine(StringUtil.localize("gui.drones") + ": " + breedingTracker.getDroneCount(), x);
		newLine();
		
		endPage();
	}

	private void displaySpeciesInformation(boolean analyzed, IAlleleBeeSpecies species, ItemStack iconStack, int x) {

		if (!analyzed) {
			drawLine(StringUtil.localize("gui.unknown"), x);
			return;
		}

		drawLine(species.getName(), x);
		itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, iconStack, adjustToFactor(guiLeft + x + 69), adjustToFactor(guiTop + getLineY() - 2));
		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, iconStack, adjustToFactor(guiLeft + x + 69), adjustToFactor(guiTop + getLineY() - 2));

		newLine();
		
		// Viable Combinations
		int columnWidth = 16;
		int column = 10;

		for (IMutation combination : BeeTemplates.getCombinations(species)) {
			if (combination.isSecret()) {
				continue;
			}

			if (breedingTracker.isDiscovered(combination)) {
				drawMutationIcon(combination, species, column);
			} else {
				drawUnknownIcon(combination, column);
			}
			
			column += columnWidth;
			if(column > 75) {
				column = 10;
				newLine(18);
			}
		}
		
		newLine();
		newLine();
	}

	private void drawMutationIcon(IMutation combination, IAlleleBeeSpecies species, int x) {
		itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, iconStacks.get(combination.getPartner(species).getUID()), adjustToFactor(guiLeft + x), adjustToFactor(guiTop + getLineY()));
		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, iconStacks.get(combination.getPartner(species).getUID()), adjustToFactor(guiLeft + x), adjustToFactor(guiTop + getLineY()));

		int line = 48;
		int column = 0;
		EnumMutateChance chance = EnumMutateChance.rateChance(combination.getBaseChance());
		if (chance == EnumMutateChance.HIGHEST) {
			line += 16;
			column = 228;
		} else if (chance == EnumMutateChance.HIGHER) {
			line += 16;
			column = 212;
		} else if (chance == EnumMutateChance.HIGH) {
			line += 16;
			column = 196;
		} else if (chance == EnumMutateChance.NORMAL) {
			line += 0;
			column = 228;
		} else if (chance == EnumMutateChance.LOW) {
			line += 0;
			column = 212;
		} else {
			line += 0;
			column = 196;
		}

		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/apiaristinventory.png"));
		drawTexturedModalRect(adjustToFactor(guiLeft + x), adjustToFactor(guiTop + getLineY()), column, line, 16, 16);

	}

	private void drawUnknownIcon(IMutation mutation, int x) {

		int chance = mutation.getBaseChance();

		int line = 0;
		int column = 0;
		if (chance >= 20) {
			line = 16;
			column = 228;
		} else if (chance >= 15) {
			line = 16;
			column = 212;
		} else if (chance >= 12) {
			line = 16;
			column = 196;
		} else if (chance >= 10) {
			line = 0;
			column = 228;
		} else if (chance >= 5) {
			line = 0;
			column = 212;
		} else {
			line = 0;
			column = 196;
		}

		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/apiaristinventory.png"));
		drawTexturedModalRect(adjustToFactor(guiLeft + x), adjustToFactor(guiTop + getLineY()), column, line, 16, 16);
	}
}
