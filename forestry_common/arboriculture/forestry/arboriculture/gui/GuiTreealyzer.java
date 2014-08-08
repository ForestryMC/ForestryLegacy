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
package forestry.arboriculture.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemTreealyzer.TreealyzerInventory;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.Allele;
import forestry.core.genetics.AllelePlantType;
import forestry.core.gui.GuiAlyzer;
import forestry.core.utils.StringUtil;

public class GuiTreealyzer extends GuiAlyzer {

	private static final int COLUMN_0 = 12;
	private static final int COLUMN_1 = 52;
	private static final int COLUMN_2 = 108;
	
	private HashMap<String, ItemStack> iconStacks = new HashMap<String, ItemStack>();
	private ItemStack[] tempProductList;

	//private IApiaristTracker breedingTracker;

	public GuiTreealyzer(EntityPlayer player, TreealyzerInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/beealyzer.png", new ContainerTreealyzer(player.inventory, inventory), inventory, 1, inventory.getSizeInventory());

		xSize = 196;
		ySize = 238;

		ArrayList<ItemStack> treeList = new ArrayList<ItemStack>();
		((ItemGermlingGE) ForestryItem.sapling).addCreativeItems(treeList, false);
		for (ItemStack beeStack : treeList) {
			iconStacks.put(TreeManager.treeInterface.getTree(beeStack).getIdent(), beeStack);
		}

		//breedingTracker = TreeManager.treeInterface.getApiaristTracker(player.worldObj);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		drawBackground();

		int page = 0;
		ITree tree = null;
		for (int k = 1; k < TreealyzerInventory.SLOT_ANALYZE_5 + 1; k++) {
			if(k == TreealyzerInventory.SLOT_ENERGY)
				continue;
			
			if (inventory.getStackInSlot(k) == null) {
				continue;
			}
			tree = TreeManager.treeInterface.getTree(inventory.getStackInSlot(k));
			if (tree == null || !tree.isAnalyzed()) {
				continue;
			}

			page = k;
			break;
		}

		switch (page) {
		case 1:
			drawAnalyticsPage1(tree);
			break;
		case 2:
			drawAnalyticsPage2(tree);
			break;
		case 3:
			drawAnalyticsPage3(tree);
			break;
		case 4:
			drawAnalyticsPage4(tree);
			break;
		case 6:
			drawAnalyticsPageClassification(tree);
			break;
		default:
			drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsOverview() {

		startPage();
		
		newLine();
		String title = StringUtil.localize("gui.treealyzer").toUpperCase();
		drawCenteredLine(title, 8, 158);
		newLine();
		
		fontRenderer.drawSplitString(StringUtil.localize("gui.treealyzer.help"), (int)((guiLeft + COLUMN_0 + 4)*(1/factor)), (int)((guiTop + 42)*(1/factor)), (int)(158*(1/factor)), fontColor.get("gui.screen"));
		newLine();
		newLine();
		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.treealyzer.overview") + ":", COLUMN_0 + 4);
		newLine();
		drawLine("I  : " + StringUtil.localize("gui.general"), COLUMN_0 + 4);
		newLine();
		drawLine("II : " + StringUtil.localize("gui.environment"), COLUMN_0 + 4);
		newLine();
		drawLine("III: " + StringUtil.localize("gui.produce"), COLUMN_0 + 4);
		newLine();
		drawLine("IV : " + StringUtil.localize("gui.evolution"), COLUMN_0 + 4);

		endPage();
	}

	private void drawAnalyticsPage1(ITree tree) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		
		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);
		
		newLine();
		newLine();

		IAlleleTreeSpecies primary = tree.getGenome().getPrimaryAsTree(); 
		IAlleleTreeSpecies secondary = tree.getGenome().getSecondaryAsTree();
		
		drawLine(StringUtil.localize("gui.species"), COLUMN_0);
		drawSplitLine(primary.getName(), COLUMN_1, COLUMN_2 - COLUMN_1 - 4, tree, EnumTreeChromosome.SPECIES, false);
		drawSplitLine(secondary.getName(), COLUMN_2, COLUMN_2 - COLUMN_1 - 4, tree, EnumTreeChromosome.SPECIES, true);

		newLine();
		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.saplings"), COLUMN_0);
		drawLine(Tree.rateFertility(tree.getGenome().getFertility()), COLUMN_1, tree, EnumTreeChromosome.FERTILITY, false);
		drawLine(Tree.rateFertility(((IAlleleFloat) tree.getGenome().getInactiveAllele(EnumTreeChromosome.FERTILITY.ordinal())).getValue()),
				COLUMN_2, tree, EnumTreeChromosome.FERTILITY, true);

		newLine();

		drawRow(StringUtil.localize("gui.maturity"),
				Tree.rateMaturity(tree.getGenome().getMaturationTime()),
				Tree.rateMaturity(((IAlleleInteger) tree.getGenome().getInactiveAllele(EnumTreeChromosome.MATURATION.ordinal())).getValue()),
				tree, EnumTreeChromosome.MATURATION
				);
		
		drawLine(StringUtil.localize("gui.height"), COLUMN_0);
		drawLine(Tree.rateHeight(tree.getGenome().getHeight()), COLUMN_1, tree, EnumTreeChromosome.HEIGHT, false);
		drawLine(Tree.rateHeight(((IAlleleFloat) tree.getGenome().getInactiveAllele(EnumTreeChromosome.HEIGHT.ordinal())).getValue()),
				COLUMN_2, tree, EnumTreeChromosome.HEIGHT, true);

		newLine();

		drawLine(StringUtil.localize("gui.girth"), COLUMN_0);
		drawLine(String.format("%sx%s", primary.getGirth(), primary.getGirth()), COLUMN_1, tree, EnumTreeChromosome.FERTILITY, false);
		drawLine(String.format("%sx%s", secondary.getGirth(), secondary.getGirth()), COLUMN_2, tree, EnumTreeChromosome.FERTILITY, true);

		newLine();

		drawLine(StringUtil.localize("gui.yield"), COLUMN_0);
		drawLine(Tree.rateYield(tree.getGenome().getYield()), COLUMN_1, tree, EnumTreeChromosome.YIELD, false);
		drawLine(Tree.rateYield(((IAlleleFloat) tree.getGenome().getInactiveAllele(EnumTreeChromosome.YIELD.ordinal())).getValue()),
				COLUMN_2, tree, EnumTreeChromosome.YIELD, true);

		newLine();

		drawLine(StringUtil.localize("gui.sappiness"), COLUMN_0);
		drawLine(Tree.rateSappiness(tree.getGenome().getSappiness()), COLUMN_1, tree, EnumTreeChromosome.SAPPINESS, false);
		
		// FIXME: Legacy handling
		IAllele sappiness = tree.getGenome().getInactiveAllele(EnumTreeChromosome.SAPPINESS.ordinal());
		float sap;
		if(sappiness instanceof IAlleleFloat)
			sap = ((IAlleleFloat)sappiness).getValue();
		else
			sap = 0.1f;
			
		drawLine(Tree.rateSappiness(sap), COLUMN_2, tree, EnumTreeChromosome.SAPPINESS, true);

		newLine();
		newLine();

		endPage();
	}

	private void drawAnalyticsPage2(ITree tree) {

		startPage();

		int speciesDominance0 = getColorCoding(tree.getGenome().getPrimaryAsTree().isDominant());
		int speciesDominance1 = getColorCoding(tree.getGenome().getSecondaryAsTree().isDominant());
		
		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();
		
		drawLine(StringUtil.localize("gui.growth"), COLUMN_0);
		drawLine(tree.getGenome().getGrowthProvider().getDescription(), COLUMN_1, tree, EnumTreeChromosome.GROWTH, false);
		drawLine(((IAlleleGrowth)tree.getGenome().getInactiveAllele(EnumTreeChromosome.GROWTH.ordinal())).getProvider().getDescription(),
				COLUMN_2, tree, EnumTreeChromosome.GROWTH, true);
		
		newLine();
		
		drawLine(StringUtil.localize("gui.native"), COLUMN_0);
		drawLine(StringUtil.localize("gui." + tree.getGenome().getPrimaryAsTree().getPlantType().toString().toLowerCase(Locale.ENGLISH)), COLUMN_1, speciesDominance0);
		drawLine(StringUtil.localize("gui." + tree.getGenome().getSecondaryAsTree().getPlantType().toString().toLowerCase(Locale.ENGLISH)), COLUMN_2, speciesDominance1);

		newLine();
		
		drawLine(StringUtil.localize("gui.tolerated"), COLUMN_0);
		
		EnumPlantType[] tolerated0 = tree.getGenome().getPlantTypes().toArray(new EnumPlantType[0]);
		EnumPlantType[] tolerated1 = new EnumPlantType[0];
		IAllele allele1 = tree.getGenome().getInactiveAllele(EnumTreeChromosome.PLANT.ordinal());
		if(allele1 instanceof AllelePlantType)
			tolerated1 = ((AllelePlantType)allele1).getPlantTypes().toArray(new EnumPlantType[0]);
		
		int max = tolerated0.length > tolerated1.length ? tolerated0.length : tolerated1.length;
		for(int i = 0; i < max; i++) {
			if(i > 0)
				newLine();
			drawLine(StringUtil.localize("gui." + tolerated0[i].toString().toLowerCase(Locale.ENGLISH)), COLUMN_1, tree, EnumTreeChromosome.PLANT, false);
			drawLine(StringUtil.localize("gui." + tolerated1[i].toString().toLowerCase(Locale.ENGLISH)), COLUMN_2, tree, EnumTreeChromosome.PLANT, true);
		}
		newLine();
		
		// FRUITS
		drawLine(StringUtil.localize("gui.supports"), COLUMN_0);
		IFruitFamily[] families0 = tree.getGenome().getPrimaryAsTree().getSuitableFruit().toArray(new IFruitFamily[0]);
		IFruitFamily[] families1 = tree.getGenome().getPrimaryAsTree().getSuitableFruit().toArray(new IFruitFamily[0]);
		
		max = families0.length > families1.length ? families0.length : families1.length;
		for(int i = 0; i < max; i++) {
			if(i > 0)
				newLine();
			
			if(families0.length > i)
				drawLine(StringUtil.localize(families0[i].getName()), COLUMN_1, speciesDominance0);
			if(families1.length > 0)
				drawLine(StringUtil.localize(families1[i].getName()), COLUMN_2, speciesDominance1);
			
		}
		
		newLine();
		newLine();
		
		int fruitDominance0 = getColorCoding(tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS.ordinal()).isDominant());
		int fruitDominance1 = getColorCoding(tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS.ordinal()).isDominant());
		
		drawLine(StringUtil.localize("gui.fruits"), COLUMN_0);
		String strike = "";
		IAllele fruit0 = tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS.ordinal());
		if(!tree.canBearFruit()
				&& fruit0 != Allele.fruitNone) {
			strike = "\u00A7m";
		}
		drawLine(strike + StringUtil.localize(tree.getGenome().getFruitProvider().getDescription()), COLUMN_1, fruitDominance0);
		
		strike = "";
		IAllele fruit1 = tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS.ordinal());
		if(!tree.getGenome().getSecondaryAsTree().getSuitableFruit().contains(((IAlleleFruit)fruit1).getProvider().getFamily())
				&& fruit1 != Allele.fruitNone) {
			strike = "\u00A7m";
		}
		drawLine(strike + StringUtil.localize(((IAlleleFruit)fruit1).getProvider().getDescription()), COLUMN_2, fruitDominance1);

		newLine();
		
		drawLine(StringUtil.localize("gui.family"), COLUMN_0);
		IFruitFamily primary = tree.getGenome().getFruitProvider().getFamily();
		IFruitFamily secondary = ((IAlleleFruit)tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS.ordinal())).getProvider().getFamily();
		
		if(primary != null)
			drawLine(StringUtil.localize(primary.getName()), COLUMN_1, fruitDominance0);
		if(secondary != null)
			drawLine(StringUtil.localize(secondary.getName()), COLUMN_2, fruitDominance1);

		endPage();
	}

	private void drawAnalyticsPage3(ITree tree) {

		tempProductList = tree.getProduceList();

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
		for (ItemStack stack : tree.getSpecialtyList()) {
			itemRenderer.renderItemIntoGUI(fontRenderer, mc.renderEngine, stack, (int)((guiLeft + x)*(1/factor)), (int)((guiTop + getLineY())*(1/factor)));
			x += 18;
			if (x > adjustToFactor(148)) {
				x = COLUMN_0;
				newLine();
			}
		}

		endPage();
	}

	private void drawAnalyticsPage4(ITree tree) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		drawLine(StringUtil.localize("gui.beealyzer.mutations") + ":", COLUMN_0);
		newLine();
		newLine();
		
		RenderHelper.enableGUIStandardItemLighting();
		
		endPage();
	}

}
