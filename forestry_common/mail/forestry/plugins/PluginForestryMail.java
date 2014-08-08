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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.IPacketHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.api.core.PluginInfo;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.items.ItemForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.Trigger;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.mail.CommandMail;
import forestry.mail.EnumPostage;
import forestry.mail.GuiHandlerMail;
import forestry.mail.PacketHandlerMail;
import forestry.mail.SaveEventHandlerMail;
import forestry.mail.TickHandlerMailClient;
import forestry.mail.gadgets.MachineMailbox;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.gadgets.MachinePhilatelist;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemStamps;
import forestry.mail.items.ItemStamps.StampInfo;
import forestry.mail.proxy.ProxyMail;
import forestry.mail.triggers.TriggerBuffer;
import forestry.mail.triggers.TriggerHasMail;
import forestry.mail.triggers.TriggerLowInput;
import forestry.mail.triggers.TriggerLowPaper;
import forestry.mail.triggers.TriggerLowStamps;

@PluginInfo(pluginID = "Mail", name = "Mail", author = "SirSengir", url = Defaults.URL, description = "Adds Forestry's mail and trade system.")
public class PluginForestryMail extends NativePlugin {

	@SidedProxy(clientSide = "forestry.mail.proxy.ClientProxyMail", serverSide = "forestry.mail.proxy.ProxyMail")
	public static ProxyMail proxy;

	public static Trigger triggerHasMail;

	public static Trigger lowPaper25;
	public static Trigger lowPaper10;
	public static Trigger lowPostage40;
	public static Trigger lowPostage20;
	public static Trigger lowInput25;
	public static Trigger lowInput10;
	public static Trigger highBuffer75;
	public static Trigger highBuffer90;

	public static MachineDefinition definitionMailbox;
	public static MachineDefinition definitionTradestation;
	public static MachineDefinition definitionPhilatelist;
	
	@Override
	public boolean isAvailable() {
		return !Config.disableMail;
	}

	@Override
	public void preInit() {
		super.preInit();

		TickRegistry.registerTickHandler(new TickHandlerMailClient(), Side.CLIENT);

		triggerHasMail = new TriggerHasMail(Defaults.ID_TRIGGER_HASMAIL);
		lowPaper25 = new TriggerLowPaper(Defaults.ID_TRIGGER_LOWPAPER_25, 0.25f);
		lowPaper10 = new TriggerLowPaper(Defaults.ID_TRIGGER_LOWPAPER_10, 0.1f);
		lowPostage40 = new TriggerLowStamps(Defaults.ID_TRIGGER_LOWSTAMPS_40, 40);
		lowPostage20 = new TriggerLowStamps(Defaults.ID_TRIGGER_LOWSTAMPS_20, 20);
		lowInput25 = new TriggerLowInput(Defaults.ID_TRIGGER_LOWINPUT_25, 0.25f);
		lowInput10 = new TriggerLowInput(Defaults.ID_TRIGGER_LOWINPUT_10, 0.1f);
		highBuffer75 = new TriggerBuffer(Defaults.ID_TRIGGER_BUFFER_75, 0.75f);
		highBuffer90 = new TriggerBuffer(Defaults.ID_TRIGGER_BUFFER_90, 0.90f);
		
		int blockid = Config.getOrCreateBlockIdProperty("mail", Defaults.ID_BLOCK_MAIL);
		
		definitionMailbox = new MachineDefinition(blockid, Defaults.DEFINITION_MAILBOX_META, "forestry.Mailbox", MachineMailbox.class,
				ShapedRecipeCustom.createShapedRecipe(new Object[] { " # ", "#Y#", "XXX", Character.valueOf('#'), "ingotTin", Character.valueOf('X'), Block.chest,
						Character.valueOf('Y'), ForestryItem.sturdyCasing }, new ItemStack(blockid, 1, Defaults.DEFINITION_MAILBOX_META))
				).setFaces(87, 89, 88, 88, 88, 88, 87, 90);
		
		definitionTradestation = new MachineDefinition(blockid, Defaults.DEFINITION_TRADESTATION_META, "forestry.Tradestation", MachineTrader.class,
				ShapedRecipeCustom.createShapedRecipe(new Object[] { "Z#Z", "#Y#", "XWX", Character.valueOf('#'), new ItemStack(ForestryItem.tubes, 1, 2),
						Character.valueOf('X'), Block.chest, Character.valueOf('Y'), ForestryItem.sturdyCasing, Character.valueOf('Z'),
						new ItemStack(ForestryItem.tubes, 1, 3), Character.valueOf('W'), new ItemStack(ForestryItem.circuitboards, 1, 2) },
						new ItemStack(blockid, 1, Defaults.DEFINITION_TRADESTATION_META)
				)).setFaces(87, 89, 105, 106, 88, 88, 87, 90);
		
		definitionPhilatelist = new MachineDefinition(blockid, Defaults.DEFINITION_PHILATELIST_META, "forestry.Philatelist", MachinePhilatelist.class).setFaces(87, 89, 103, 104, 103, 103, 87, 90);
		
		ForestryBlock.mail = new BlockBase(blockid,
				Material.iron, new MachineDefinition[] { definitionMailbox, definitionTradestation, definitionPhilatelist }).setBlockName("for.mail");
		Item.itemsList[ForestryBlock.mail.blockID] = null;
		Item.itemsList[ForestryBlock.mail.blockID] = new ItemForestryBlock(ForestryBlock.mail.blockID - 256, "for.mail");
	}

	public void doInit() {
		super.doInit();
		
		definitionMailbox.register();
		definitionTradestation.register();
		definitionPhilatelist.register();
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	public String getDescription() {
		return "Mail";
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerMail();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerMail();
	}

	@Override
	protected void registerPackages() {
		//GadgetManager.registerMillPackage(Defaults.ID_PACKAGE_MILL_MAILBOX, PackagesMail.getMailboxPackage());
		//GadgetManager.registerMillPackage(Defaults.ID_PACKAGE_MILL_TRADER, PackagesMail.getTraderPackage());
		//GadgetManager.registerMillPackage(Defaults.ID_PACKAGE_MILL_PHILATELIST, PackagesMail.getPhilatelistPackage());
	}

	@Override
	protected void registerItems() {

		// / STAMPS
		ForestryItem.stamps = new ItemStamps(Config.getOrCreateItemIdProperty("stamps", Defaults.ID_ITEM_STAMPS), new StampInfo[] {
				new StampInfo("1p", EnumPostage.P_1, 0x4a8ca7, 0xffffff), new StampInfo("2p", EnumPostage.P_2, 0xe8c814, 0xffffff),
				new StampInfo("5p", EnumPostage.P_5, 0x9c0707, 0xffffff), new StampInfo("10p", EnumPostage.P_10, 0x7bd1b8, 0xffffff) }).setIcons(51, 50)
				.setItemName("stamps");

		// / LETTER
		ForestryItem.letters = new ItemLetter(Config.getOrCreateItemIdProperty("letters", Defaults.ID_ITEM_LETTERS)).setItemName("letters");

		// / MAIL INDICATOR
		// ForestryItem.mailIndicator = new ItemMailIndicator(Config.getOrCreateItemIdProperty("mailIndicator", Defaults.ID_ITEM_MAIL_INDICATOR))
		// .setItemName("mailIndicator").setIconIndex(10);
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {
		// Letters
		Proxies.common.addShapelessRecipe(new ItemStack(ForestryItem.letters, 1), new Object[] { Item.paper, new ItemStack(ForestryItem.propolis, 1, -1) });

		if (Config.craftingStampsEnabled) {

			// Stamps
			Proxies.common.addRecipe(new ItemStack(ForestryItem.stamps, 9, 0), new Object[] { "XXX", "###", "ZZZ", Character.valueOf('X'),
					ForestryItem.apatite, Character.valueOf('#'), Item.paper, Character.valueOf('Z'), ForestryItem.honeyDrop });
			Proxies.common.addRecipe(new ItemStack(ForestryItem.stamps, 9, 1), new Object[] { "XXX", "###", "ZZZ", Character.valueOf('X'), "ingotCopper",
					Character.valueOf('#'), Item.paper, Character.valueOf('Z'), ForestryItem.honeyDrop });
			Proxies.common.addRecipe(new ItemStack(ForestryItem.stamps, 9, 2), new Object[] { "XXX", "###", "ZZZ", Character.valueOf('X'), "ingotTin",
					Character.valueOf('#'), Item.paper, Character.valueOf('Z'), ForestryItem.honeyDrop });
			Proxies.common.addRecipe(new ItemStack(ForestryItem.stamps, 9, 3), new Object[] { " X ", "###", "ZZZ", Character.valueOf('X'), Item.ingotGold,
					Character.valueOf('#'), Item.paper, Character.valueOf('Z'), ForestryItem.honeyDrop });

		}

		// Recycling
		Proxies.common.addRecipe(new ItemStack(Item.paper), new Object[] { "###", Character.valueOf('#'), new ItemStack(ForestryItem.letters, 1, 3) });
		Proxies.common.addRecipe(new ItemStack(Item.paper), new Object[] { "###", Character.valueOf('#'), new ItemStack(ForestryItem.letters, 1, 19) });
		Proxies.common.addRecipe(new ItemStack(Item.paper), new Object[] { "###", Character.valueOf('#'), new ItemStack(ForestryItem.letters, 1, 35) });
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerMail();
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[] { new CommandMail() };
	}


}
