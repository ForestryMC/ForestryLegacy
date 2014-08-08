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
import java.util.Map;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleFlowers;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IApiaristTracker;
import forestry.api.genetics.IMutation;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.BeeTemplates;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBeealyzer.BeealyzerInventory;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.AlleleArea;
import forestry.core.genetics.AlleleBoolean;
import forestry.core.genetics.AlleleTolerance;
import forestry.core.genetics.ClimateHelper;
import forestry.core.genetics.EnumMutateChance;
import forestry.core.gui.GuiAlyzer;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Vect;

public class GuiBeealyzer extends GuiAlyzer {

	private static final int COLUMN_0 = 12;
	private static final int COLUMN_1 = 52;
	private static final int COLUMN_2 = 108;
	
	private HashMap<String, ItemStack> iconStacks = new HashMap<String, ItemStack>();

	private ItemStack[] tempProductList;

	private IApiaristTracker breedingTracker;

	public GuiBeealyzer(EntityPlayer player, BeealyzerInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png", new ContainerBeealyzer(player.inventory, inventory), inventory, 1, inventory.getSizeInventory());

		xSize = 196;
		ySize = 238;

		ArrayList<ItemStack> beeList = new ArrayList<ItemStack>();
		((ItemBeeGE) ForestryItem.beeDroneGE).addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			iconStacks.put(BeeManager.beeInterface.getBee(beeStack).getIdent(), beeStack);
		}

		breedingTracker = BeeManager.breedingManager.getApiaristTracker(player.worldObj, player.username);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		drawBackground();

		int page = 0;
		IBee bee = null;
		for (int k = 1; k < BeealyzerInventory.SLOT_ANALYZE_5 + 1; k++) {
			if(k == BeealyzerInventory.SLOT_ENERGY)
				continue;
			
			if (inventory.getStackInSlot(k) == null) {
				continue;
			}
			bee = BeeManager.beeInterface.getBee(inventory.getStackInSlot(k));
			if (bee == null || !bee.isAnalyzed()) {
				continue;
			}

			page = k;
			break;
		}

		switch (page) {
		case 1:
			drawAnalyticsPage1(bee);
			break;
		case 2:
			drawAnalyticsPage2(bee);
			break;
		case 3:
			drawAnalyticsPage3(bee);
			break;
		case 4:
			drawAnalyticsPage4(bee);
			break;
		case 6:
			drawAnalyticsPageClassification(bee);
			break;
		default:
			drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsOverview() {

		startPage();
		
		newLine();
		String title = StringUtil.localize("gui.beealyzer").toUpperCase();
		drawCenteredLine(title, 8, 158);
		newLine();
		
		fontRenderer.drawSplitString(StringUtil.localize("gui.beealyzer.help"), (int)((guiLeft + COLUMN_0 + 4)*(1/factor)), (int)((guiTop + 42)*(1/factor)), (int)(158*(1/factor)), fontColor.get("gui.screen"));
		newLine();
		newLine();
		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.beealyzer.overview") + ":", COLUMN_0 + 4);
		newLine();
		drawLine("I  : " + StringUtil.localize("gui.general"), COLUMN_0 + 4);
		newLine();
		drawLine("II : " + StringUtil.localize("gui.environment"), COLUMN_0 + 4);
		newLine();
		drawLine("III: " + StringUtil.localize("gui.produce"), COLUMN_0 + 4);
		newLine();
		drawLine("IV : " + StringUtil.localize("gui.evolution"), COLUMN_0 + 4);

		newLine();
		
		String mode = breedingTracker.getModeName();
		if (mode != null && !mode.isEmpty()) {
			newLine();
			String rules = StringUtil.localize("gui.beealyzer.behaviour") + ": " + StringUtil.capitalize(mode);
			drawLine(rules, 8 + getCenteredOffset(title, 158), fontColor.get("gui.beealyzer.binomial"));
		}

		endPage();

	}

	private void drawAnalyticsPage1(IBee bee) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		
		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.species"), COLUMN_0);
		drawSplitLine(bee.getGenome().getPrimaryAsBee().getName(), COLUMN_1, COLUMN_2 - COLUMN_1 - 4, bee, EnumBeeChromosome.SPECIES, false);
		drawSplitLine(bee.getGenome().getSecondaryAsBee().getName(), COLUMN_2, COLUMN_2 - COLUMN_1 - 4, bee, EnumBeeChromosome.SPECIES, true);

		newLine();
		newLine();

		drawRow(StringUtil.localize("gui.lifespan"),
				Bee.rateLifespan(bee.getGenome().getLifespan()),
				Bee.rateLifespan(((IAlleleInteger) bee.getGenome().getInactiveAllele(EnumBeeChromosome.LIFESPAN.ordinal())).getValue()),
				bee, EnumBeeChromosome.LIFESPAN
				);
		
		drawRow(StringUtil.localize("gui.speed"),
				Bee.rateSpeed(bee.getGenome().getSpeed()),
				Bee.rateSpeed(((IAlleleFloat) bee.getGenome().getInactiveAllele(EnumBeeChromosome.SPEED.ordinal())).getValue()),
				bee, EnumBeeChromosome.SPEED
				);
		
		drawRow(StringUtil.localize("gui.pollination"),
				Bee.rateFlowering(bee.getGenome().getFlowering()),
				Bee.rateFlowering(((IAlleleInteger) bee.getGenome().getInactiveAllele(EnumBeeChromosome.FLOWERING.ordinal())).getValue()),
				bee, EnumBeeChromosome.FLOWERING
				);
		
		drawRow(StringUtil.localize("gui.flowers"),
				StringUtil.localize(bee.getGenome().getFlowerProvider().getDescription()),
				StringUtil.localize(((IAlleleFlowers) bee.getGenome().getInactiveAllele(EnumBeeChromosome.FLOWER_PROVIDER.ordinal())).getProvider().getDescription()),
				bee, EnumBeeChromosome.FLOWER_PROVIDER
				);
		
		drawLine(StringUtil.localize("gui.fertility"), COLUMN_0);
		drawFertilityInfo(bee.getGenome().getFertility(), COLUMN_1,
				getColorCoding(bee.getGenome().getActiveAllele(EnumBeeChromosome.FERTILITY.ordinal()).isDominant()));
		drawFertilityInfo(((IAlleleInteger) bee.getGenome().getInactiveAllele(EnumBeeChromosome.FERTILITY.ordinal())).getValue(), COLUMN_2,
				getColorCoding(bee.getGenome().getInactiveAllele(EnumBeeChromosome.FERTILITY.ordinal()).isDominant()));

		newLine();
		
		int[] areaAr = bee.getGenome().getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]);
		drawRow(StringUtil.localize("gui.area"),
				area.toString(),
				((AlleleArea) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TERRITORY.ordinal())).getArea().toString(),
				bee, EnumBeeChromosome.TERRITORY
				);
		
		drawRow(StringUtil.localize("gui.effect"),
				StringUtil.localize(bee.getGenome().getEffect().getIdentifier()),
				StringUtil.localize(((IAlleleBeeEffect) bee.getGenome().getInactiveAllele(EnumBeeChromosome.EFFECT.ordinal())).getIdentifier()),
				bee, EnumBeeChromosome.EFFECT
				);
		
		newLine();
		
		endPage();
	}

	private void drawAnalyticsPage2(IBee bee) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		
		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		drawRow(StringUtil.localize("gui.climate"),
				ClimateHelper.toDisplay(bee.getGenome().getPrimaryAsBee().getTemperature()),
				ClimateHelper.toDisplay(bee.getGenome().getSecondaryAsBee().getTemperature()),
				bee, EnumBeeChromosome.SPECIES
				);
		
		drawLine(StringUtil.localize("gui.temptol"), COLUMN_0);
		drawToleranceInfo(bee.getGenome().getToleranceTemp(), COLUMN_1,
				getColorCoding(bee.getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()).isDominant()));
		drawToleranceInfo(((AlleleTolerance) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal())).getValue(), COLUMN_2,
				getColorCoding(bee.getGenome().getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()).isDominant()));

		newLine();
		
		drawRow(StringUtil.localize("gui.humidity"),
				ClimateHelper.toDisplay(bee.getGenome().getPrimaryAsBee().getHumidity()),
				ClimateHelper.toDisplay(bee.getGenome().getSecondaryAsBee().getHumidity()),
				bee, EnumBeeChromosome.SPECIES
				);
		
		drawLine(StringUtil.localize("gui.humidtol"), COLUMN_0);
		drawToleranceInfo(bee.getGenome().getToleranceHumid(), COLUMN_1,
				getColorCoding(bee.getGenome().getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()).isDominant()));
		drawToleranceInfo(((AlleleTolerance) bee.getGenome().getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal())).getValue(), COLUMN_2,
				getColorCoding(bee.getGenome().getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()).isDominant()));

		newLine();
		newLine();
		
		String yes = StringUtil.localize("yes");
		String no = StringUtil.localize("no");

		String primary = StringUtil.readableBoolean(bee.getGenome().getNocturnal(), yes, no);
		String secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue(),
				yes, no);

		drawRow(StringUtil.localize("gui.nocturnal"),
				primary, secondary, bee, EnumBeeChromosome.NOCTURNAL);
		
		primary = StringUtil.readableBoolean(bee.getGenome().getTolerantFlyer(), yes, no);
		secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TOLERANT_FLYER.ordinal())).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.flyer"),
				primary, secondary, bee, EnumBeeChromosome.TOLERANT_FLYER);

		primary = StringUtil.readableBoolean(bee.getGenome().getCaveDwelling(), yes, no);
		secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING.ordinal())).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.cave"),
				primary, secondary, bee, EnumBeeChromosome.CAVE_DWELLING);

		newLine();
		newLine();

		String origin = "gui.beealyzer.natural";
		if (!bee.isNatural()) {
			origin = "gui.beealyzer.artificial";
		}
		origin = StringUtil.localize(origin);
		//int offset = (int)(((158 - fontRenderer.getStringWidth(origin)) / 2)*(1/factor));
		drawCenteredLine(origin, 8, 158, fontColor.get("gui.beealyzer.binomial"));

		if(bee.getGeneration() >= 0) {
			newLine();

			origin = bee.getGeneration() + " " + StringUtil.localize("gui.beealyzer.generations");
			//offset = (int)(((158 - fontRenderer.getStringWidth(origin)) / 2)*(1/factor));
			drawCenteredLine(origin, 8, 158, fontColor.get("gui.beealyzer.binomial"));
		}

		endPage();
	}

	private void drawAnalyticsPage3(IBee bee) {

		tempProductList = bee.getProduceList();

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		
		drawLine(StringUtil.localize("gui.beealyzer.produce") + ":", COLUMN_0);
		
		newLine();

		int x = COLUMN_0;
		for (ItemStack stack : tempProductList) {
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, stack, (int)((guiLeft + x)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)));
			x += 18;
			if (x > adjustToFactor(148)) {
				x = COLUMN_0;
				newLine();
			}
		}

		newLine();
		newLine();
		
		drawLine(StringUtil.localize("gui.beealyzer.specialty") + ":", COLUMN_0);
		newLine();
		
		x = COLUMN_0;
		for (ItemStack stack : bee.getSpecialtyList()) {
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, stack, (int)((guiLeft + x)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)));
			x += 18;
			if (x > adjustToFactor(148)) {
				x = COLUMN_0;
				newLine();
			}
		}

		endPage();
	}

	private void drawAnalyticsPage4(IBee bee) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		drawLine(StringUtil.localize("gui.beealyzer.mutations") + ":", COLUMN_0);
		newLine();
		newLine();
		
		RenderHelper.enableGUIStandardItemLighting();

		HashMap<IMutation, IAllele> combinations = new HashMap<IMutation, IAllele>();

		for (IMutation mutation : BeeTemplates.getCombinations(bee.getGenome().getPrimaryAsBee())) {
			combinations.put(mutation, bee.getGenome().getPrimaryAsBee());
		}

		for (IMutation mutation : BeeTemplates.getCombinations(bee.getGenome().getSecondaryAsBee())) {
			combinations.put(mutation, bee.getGenome().getSecondaryAsBee());
		}

		int columnWidth = 50;
		int x = 0;

		for (Map.Entry<IMutation, IAllele> mutation : combinations.entrySet()) {

			if (breedingTracker.isDiscovered(mutation.getKey())) {
				drawMutationInfo(mutation.getKey(), mutation.getValue(), COLUMN_0 + x);
			} else {
				// Do not display secret undiscovered mutations.
				if (mutation.getKey().isSecret()) {
					continue;
				}

				drawUnknownMutation(mutation.getKey(), mutation.getValue(), COLUMN_0 + x);
			}
			
			x += columnWidth;
			if(x > 150) {
				x = 0;
				newLine();
				newLine();
			}
		}
		
		endPage();
	}

	private void drawFertilityInfo(int fertility, int x, int textColor) {

		// Enable correct lighting.
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect((int)((guiLeft + x + 19)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)), 196, 43, 12, 9);

		drawLine(Integer.toString(fertility) + " x", x, textColor);
	}

	private void drawToleranceInfo(EnumTolerance tolerance, int x, int textColor) {
		int length = tolerance.toString().length();
		String text = "(" + tolerance.toString().substring(length - 1) + ")";

		// Enable correct lighting.
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		switch (tolerance) {
		case BOTH_1:
		case BOTH_2:
		case BOTH_3:
		case BOTH_4:
		case BOTH_5:
			drawBothSymbol(x, getLineY() - 1);
			drawLine(text, x + (int)(20*factor), textColor);
			break;
		case DOWN_1:
		case DOWN_2:
		case DOWN_3:
		case DOWN_4:
		case DOWN_5:
			drawDownSymbol(x, getLineY() - 1);
			drawLine(text, x + (int)(20*factor), textColor);
			break;
		case UP_1:
		case UP_2:
		case UP_3:
		case UP_4:
		case UP_5:
			drawUpSymbol(x, getLineY() - 1);
			drawLine(text, x + (int)(20*factor), textColor);
			break;
		default:
			drawNoneSymbol(x, getLineY() - 1);
			drawLine("(0)", x + (int)(20*factor), textColor);
			break;
		}
	}

	private void drawDownSymbol(int x, int y) {
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect((int)((guiLeft + x)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)), 196, 34, 15, 9);
	}

	private void drawUpSymbol(int x, int y) {
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect((int)((guiLeft + x)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)), 211, 34, 15, 9);
	}

	private void drawBothSymbol(int x, int y) {
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect((int)((guiLeft + x)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)), 226, 34, 15, 9);
	}

	private void drawNoneSymbol(int x, int y) {
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect((int)((guiLeft + x)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)), 241, 34, 15, 9);
	}

	private void drawMutationInfo(IMutation combination, IAllele species, int x) {

		itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, iconStacks.get(combination.getPartner(species).getUID()), adjustToFactor(guiLeft) + x, adjustToFactor(guiTop) + getLineY());
		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, iconStacks.get(combination.getPartner(species).getUID()), adjustToFactor(guiLeft) + x, adjustToFactor(guiTop) + getLineY());

		IAllele result = combination.getTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
		itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, iconStacks.get(result.getUID()), adjustToFactor(guiLeft) + x + 33, adjustToFactor(guiTop) + getLineY());
		itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, iconStacks.get(result.getUID()), adjustToFactor(guiLeft) + x + 33, adjustToFactor(guiTop) + getLineY());

		int line = 0;
		int column = 196;

		switch (EnumMutateChance.rateChance(combination.getBaseChance())) {
		case HIGHEST:
			column = 226;
			line = 9;
			break;
		case HIGHER:
			column = 211;
			line = 9;
			break;
		case HIGH:
			line = 9;
			break;
		case NORMAL:
			column = 226;
			break;
		case LOW:
			column = 211;
			break;
		case LOWEST:
		default:
			break;
		}

		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect(adjustToFactor(guiLeft) + x + 18, adjustToFactor(guiTop) + getLineY() + 4, column, line, 15, 9);

	}

	private void drawUnknownMutation(IMutation combination, IAllele species, int x) {

		// Question marks
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect(adjustToFactor(guiLeft) + x, adjustToFactor(guiTop) + getLineY(), 196, 18, 16, 16);
		
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect(adjustToFactor(guiLeft) + x + 32, adjustToFactor(guiTop) + getLineY(), 196, 18, 16, 16);

		int line = 0;
		int column = 196;

		switch (EnumMutateChance.rateChance(combination.getBaseChance())) {
		case HIGHEST:
			column = 226;
			line = 9;
			break;
		case HIGHER:
			column = 211;
			line = 9;
			break;
		case HIGH:
			line = 9;
			break;
		case NORMAL:
			column = 226;
			break;
		case LOW:
			column = 211;
			break;
		case LOWEST:
		default:
			break;
		}

		// Probability arrow
		mc.renderEngine.bindTexture(mc.renderEngine.getTexture(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png"));
		drawTexturedModalRect(adjustToFactor(guiLeft) + x + 18, adjustToFactor(guiTop) + getLineY() + 4, column, line, 15, 9);

	}

}
