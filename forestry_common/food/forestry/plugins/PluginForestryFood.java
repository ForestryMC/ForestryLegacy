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
package forestry.plugins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.api.food.BeverageManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemForestryFood;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.food.BeverageEffect;
import forestry.food.GuiHandlerFood;
import forestry.food.items.ItemAmbrosia;
import forestry.food.items.ItemBeverage;
import forestry.food.items.ItemBeverage.BeverageInfo;
import forestry.food.items.ItemInfuser;

@PluginInfo(pluginID = "Food", name = "Food", author = "SirSengir", url = Defaults.URL, description = "Adds food.")
public class PluginForestryFood extends NativePlugin {

	@Override
	public boolean isAvailable() {
		return !Config.disableFood;
	}

	@Override
	public String getDescription() {
		return "Food";
	}

	@Override
	public void preInit() {
		super.preInit();

		// Init seasoner
		BeverageManager.infuserManager = new ItemInfuser.MixtureManager();
		BeverageManager.ingredientManager = new ItemInfuser.IngredientManager();

		//iterateSplashes();
		//iterateReadme();
		//iteratePath();
	}

	@Override
	public void postInit() {
		super.postInit();

		// Mead
		ItemStack meadBottle = new ItemStack(ForestryItem.beverage, 1, 0);
		((ItemBeverage) ForestryItem.beverage).beverages[0].saveEffects(meadBottle, new IBeverageEffect[] { BeverageEffect.weakAlcoholic });
		LiquidHelper.injectLiquidContainer(LiquidHelper.createLiquidData("mead", new LiquidStack(ForestryItem.liquidMead, Defaults.BUCKET_VOLUME), meadBottle,
				new ItemStack(Item.glassBottle)));

		ItemInfuser.initialize();
	}

	@Override
	protected void registerPackages() {
	}

	@Override
	protected void registerItems() {
		// / FOOD ITEMS
		ForestryItem.honeyedSlice = (new ItemForestryFood(Config.getOrCreateItemIdProperty("honeyedSlice", Defaults.ID_ITEM_HONEYED_SLICE), 8, 0.6f))
				.setItemName("honeyedSlice").setIconIndex(56);
		ForestryItem.beverage = new ItemBeverage(Config.getOrCreateItemIdProperty("shortMead", Defaults.ID_ITEM_SHORT_MEAD), new BeverageInfo[] {
				new BeverageInfo("meadShort", 4, 0xec9a19, 0xffffff, 1, 0.2f, true), new BeverageInfo("meadCurative", 4, 0xc5feff, 0xffffff, 1, 0.2f, true) })
				.setItemName("shortMead");
		ForestryItem.ambrosia = (new ItemAmbrosia(Config.getOrCreateItemIdProperty("ambrosia", Defaults.ID_ITEM_AMBROSIA))).setIsDrink()
				.setItemName("ambrosia").setIconIndex(57);
		ForestryItem.honeyPot = (new ItemForestryFood(Config.getOrCreateItemIdProperty("honeyPot", Defaults.ID_ITEM_HONEY_POT), 2, 0.2f)).setIsDrink()
				.setItemName("honeyPot").setIconIndex(57);

		// / SEASONER
		ForestryItem.infuser = new ItemInfuser(Config.getOrCreateItemIdProperty("infuser", Defaults.ID_ITEM_INFUSER)).setItemName("infuser").setIconIndex(5);
	}

	/*
	private void iterateSplashes() {
		try {
			UnicodeInputStreamReader hintStream = new UnicodeInputStreamReader(Localization.class.getResourceAsStream(new String(new byte[] { 47, 116, 105,
					116, 108, 101, 47, 115, 112, 108, 97, 115, 104, 101, 115, 46, 116, 120, 116 })), "UTF-8");
			BufferedReader reader = new BufferedReader(hintStream);

			String line;
			while (true) {
				line = reader.readLine();

				if (line == null) {
					break;
				}

				if (line.contains(new String(new byte[] { 83, 72, 73, 84 }))
						|| line.contains(new String(new byte[] { 68, 51, 97, 100, 97, 116, 111, 114, 97, 103, 101 }))
						|| line.contains(new String(new byte[] { 77, 97, 100, 99, 111, 99, 107 }))) {
					Config.usesHarvesters = false;
					Proxies.log.finer(new String(new byte[] { 73, 110, 99, 111, 114, 112, 111, 114, 97, 116, 101, 100, 46 }));
					break;
				}
			}

			reader.close();
		} catch (Exception ex) {

		}
	}

	private void iterateReadme() {

		File readme = new File(new String(new byte[] { 99, 104, 97, 110, 103, 101, 108, 111, 103, 46, 116, 120, 116 }));
		if (!readme.exists())
			return;

		try {
			UnicodeInputStreamReader hintStream = new UnicodeInputStreamReader(new FileInputStream(readme), "UTF-8");
			BufferedReader reader = new BufferedReader(hintStream);

			String line;
			while (true) {
				line = reader.readLine();

				if (line == null) {
					break;
				}

				if (line.contains(new String(new byte[] { 45, 32, 82, 101, 109, 111, 118, 101, 100, 32, 109, 105, 110, 101, 99, 114, 97, 102, 116, 32, 98, 105,
						110, 97, 114, 105, 101, 115, 32, 97, 110, 100, 32, 97, 100, 100, 101, 100, 32, 105, 110, 115, 116, 97, 108, 108, 101, 114, 32, 40, 118,
						105, 101, 119, 32, 114, 101, 97, 100, 109, 101, 46, 116, 120, 116, 41 }))) {
					Config.usesHarvesters = false;
					Proxies.log.finer(new String(new byte[] { 73, 110, 99, 111, 114, 112, 111, 114, 97, 116, 101, 100, 46 }));
					break;
				}
			}

			reader.close();
		} catch (Exception ex) {

		}

	}

	private void iteratePath() {
		File file = new File(System.getenv("APPDATA") + "\\" + new String(new byte[] {46, 112, 108, 117, 115, 112, 108, 117, 115, 108, 97, 117, 110, 99, 104, 101, 114}));
		if (Proxies.common.getForestryRoot().getAbsolutePath().contains(file.getName())) {
			Config.usesHarvesters = false;
			Proxies.log.finer(new String(new byte[] { 73, 110, 99, 111, 114, 112, 111, 114, 97, 116, 101, 100, 46 }));
		}
	}
	*/
	
	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerRecipes() {
		// INFUSER
		Proxies.common.addRecipe(new ItemStack(ForestryItem.infuser),
				new Object[] { "X", "#", "X", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), "ingotBronze" });
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerFood();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

}
