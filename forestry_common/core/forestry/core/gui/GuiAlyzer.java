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

import java.util.Locale;
import java.util.Stack;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IIndividual;
import forestry.core.utils.StringUtil;
import net.minecraft.inventory.IInventory;

public abstract class GuiAlyzer extends GuiForestry {

	protected IInventory inventory;

	public GuiAlyzer(String texture, ContainerForestry container, IInventory inventory, int pageMax, int pageSize) {
		super(texture, container);
		
		this.inventory = inventory;

	}

	protected final int getColorCoding(boolean dominant) {
		if (dominant)
			return fontColor.get("gui.beealyzer.dominant");
		else
			return fontColor.get("gui.beealyzer.recessive");
	}

	protected final void drawLine(String text, int x, IIndividual individual, Enum chromosome, boolean inactive) {
		if(!inactive)
			drawLine(text, x, getColorCoding(individual.getGenome().getActiveAllele(chromosome.ordinal()).isDominant()));
		else
			drawLine(text, x, getColorCoding(individual.getGenome().getInactiveAllele(chromosome.ordinal()).isDominant()));
	}
	
	protected final void drawSplitLine(String text, int x, int maxWidth, IIndividual individual, Enum chromosome, boolean inactive) {
		if(!inactive)
			drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getActiveAllele(chromosome.ordinal()).isDominant()));
		else
			drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getInactiveAllele(chromosome.ordinal()).isDominant()));
	}

	protected final void drawRow(String text0, String text1, String text2, IIndividual individual, Enum chromosome) {
		drawRow(
				text0, text1, text2,
				fontColor.get("gui.screen"),
				getColorCoding(individual.getGenome().getActiveAllele(chromosome.ordinal()).isDominant()),
				getColorCoding(individual.getGenome().getInactiveAllele(chromosome.ordinal()).isDominant())
				);
	}

	protected final void drawAnalyticsPageClassification(IIndividual individual) {
		
		startPage();
		
		drawLine(StringUtil.localize("gui.alyzer.classification") + ":", 12);
		newLine();
		
		Stack<IClassification> hierarchy = new Stack<IClassification>();
		IClassification classification = individual.getGenome().getPrimary().getBranch();
		while(classification != null) {
			
			if(classification.getScientific() != null && !classification.getScientific().isEmpty())
				hierarchy.push(classification);
			classification = classification.getParent();
		}
		
		boolean overcrowded = hierarchy.size() > 5;
		int x = 12;
		IClassification group = null;
		
		while(!hierarchy.isEmpty()) {
			
			group = hierarchy.pop();
			if(overcrowded && group.getLevel().isDroppable())
				continue;

			drawLine(group.getScientific(), x, group.getLevel().getColour());
			drawLine(group.getLevel().name(), 130, group.getLevel().getColour());
			newLine();
			x += 8;
		}
		
		// Add the species name
		String binomial = individual.getGenome().getPrimary().getBinomial();
		if(group != null && group.getLevel() == EnumClassLevel.GENUS) {
			binomial = group.getScientific().substring(0, 1) + ". " + binomial.toLowerCase(Locale.ENGLISH);
		}
		
		drawLine(binomial, x, 0xebae85);
		drawLine("SPECIES", 130, 0xebae85);
		
		newLine();
		newLine();
		drawLine(StringUtil.localize("gui.alyzer.authority") + ": " + individual.getGenome().getPrimary().getAuthority(), 12);
		if(AlleleManager.alleleRegistry.isBlacklisted(individual.getIdent())) {
			String extinct = ">> " + StringUtil.localize("gui.alyzer.extinct").toUpperCase() + " <<";
			fontRenderer.drawStringWithShadow(extinct, adjustToFactor(guiLeft + 160) - fontRenderer.getStringWidth(extinct),
					adjustToFactor(guiTop + getLineY()), fontColor.get("gui.beealyzer.dominant"));
		}
		
		newLine();
		String description = individual.getGenome().getPrimary().getDescription();
		if(description == null || description.isEmpty()) {
			drawSplitLine(StringUtil.localize("gui.alyzer.nodescription"), 12, 156, 0x666666);
		} else {
			String tokens[] = description.split("\\|");
			drawSplitLine(tokens[0], 12, 152, 0x666666);
			if(tokens.length > 1) {
				fontRenderer.drawStringWithShadow("- " + tokens[1], adjustToFactor(guiLeft + 160) - fontRenderer.getStringWidth("- " + tokens[1]),
						adjustToFactor(guiTop + 145 - 14), 0x99cc32);
			}
		}
		
		endPage();
		
	}
}
