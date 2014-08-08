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
package forestry.mail;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import forestry.core.config.ForestryItem;
import forestry.mail.items.ItemLetter;
import forestry.plugins.PluginForestryMail;

public class PostOffice extends WorldSavedData implements IPostOffice {

	// / CONSTANTS
	public static final String SAVE_NAME = "ForestryMail";

	public static PostOffice cachedPostOffice;
	public static HashMap<String, POBox> cachedPOBoxes = new HashMap<String, POBox>();
	public static HashMap<String, TradeStation> cachedTradeStations = new HashMap<String, TradeStation>();

	/**
	 * @param world
	 * @param username
	 * @return true if the passed username is valid for poboxes.
	 */
	public static boolean isValidPOBox(World world, String username) {
		if (!username.matches("^[a-zA-Z0-9]+$"))
			return false;

		return true;
	}

	public static POBox getPOBox(World world, String username) {

		if (cachedPOBoxes.containsKey(username.toLowerCase(Locale.ENGLISH)))
			return cachedPOBoxes.get(username.toLowerCase(Locale.ENGLISH));

		POBox pobox = (POBox) world.loadItemData(POBox.class, POBox.SAVE_NAME + username.toLowerCase(Locale.ENGLISH));
		if (pobox != null) {
			cachedPOBoxes.put(username.toLowerCase(Locale.ENGLISH), pobox);
		}
		return pobox;
	}

	public static POBox getOrCreatePOBox(World world, String username) {
		POBox pobox = getPOBox(world, username);

		if (pobox == null) {
			pobox = new POBox(username.toLowerCase(Locale.ENGLISH), true);
			world.setItemData(POBox.SAVE_NAME + username.toLowerCase(Locale.ENGLISH), pobox);
			pobox.markDirty();
			cachedPOBoxes.put(username.toLowerCase(Locale.ENGLISH), pobox);
			PluginForestryMail.proxy.setPOBoxInfo(world, username, pobox.getPOBoxInfo());
		}

		return pobox;
	}

	/**
	 * @param world
	 * @param moniker
	 * @return true if the passed moniker can be a moniker for a trade station
	 */
	public static boolean isValidTradeMoniker(World world, String moniker) {
		if (!moniker.matches("^[a-zA-Z0-9]+$"))
			return false;

		return true;
	}

	/**
	 * @param world
	 * @param moniker
	 * @return true if the trade moniker has not yet been used before.
	 */
	public static boolean isAvailableTradeMoniker(World world, String moniker) {
		return getTradeStation(world, moniker) == null;
	}

	public static TradeStation getTradeStation(World world, String moniker) {
		if (cachedTradeStations.containsKey(moniker))
			return cachedTradeStations.get(moniker);

		TradeStation trade = (TradeStation) world.loadItemData(TradeStation.class, TradeStation.SAVE_NAME + moniker);

		// Only existing and valid mail orders are returned
		if (trade != null && trade.isValid()) {
			cachedTradeStations.put(moniker, trade);
			PostOffice.getPostOffice(world).registerTradeStation(trade);
			return trade;
		}

		return null;
	}

	public static TradeStation getOrCreateTradeStation(World world, String owner, String moniker) {
		TradeStation trade = getTradeStation(world, moniker);

		if (trade == null) {
			trade = new TradeStation(owner, moniker, true);
			world.setItemData(TradeStation.SAVE_NAME + moniker, trade);
			trade.markDirty();
			cachedTradeStations.put(moniker, trade);
			PostOffice.getPostOffice(world).registerTradeStation(trade);
		}

		return trade;
	}

	public static void deleteTradeStation(World world, String moniker) {
		TradeStation trade = getTradeStation(world, moniker);
		if (trade == null)
			return;

		// Need to be marked as invalid since WorldSavedData seems to do some caching of its own.
		trade.invalidate();
		cachedTradeStations.remove(moniker);
		getPostOffice(world).deregisterTradeStation(trade);
		File file = world.getSaveHandler().getMapFileFromName(trade.mapName);
		file.delete();
	}

	public static IPostOffice getPostOffice(World world) {
		if (cachedPostOffice != null)
			return cachedPostOffice;

		PostOffice office = (PostOffice) world.loadItemData(PostOffice.class, SAVE_NAME);

		// Create office if there is none yet
		if (office == null) {
			office = new PostOffice();
			world.setItemData(SAVE_NAME, office);
		}

		cachedPostOffice = office;
		return office;
	}

	private int[] collectedPostage = new int[EnumPostage.values().length];

	// / CONSTRUCTOR
	public PostOffice(String s) {
		super(s);
	}

	public PostOffice() {
		super(SAVE_NAME);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		for (int i = 0; i < collectedPostage.length; i++)
			if (nbttagcompound.hasKey("CPS" + i)) {
				collectedPostage[i] = nbttagcompound.getInteger("CPS" + i);
			}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		for (int i = 0; i < collectedPostage.length; i++) {
			nbttagcompound.setInteger("CPS" + i, collectedPostage[i]);
		}
	}

	/* TRADE STATION MANAGMENT */
	private LinkedHashMap<String, TradeStation> activeTradeStations;

	@Override
	public LinkedHashMap<String, TradeStation> getActiveTradeStations(World world) {
		if(activeTradeStations == null)
			refreshActiveTradeStations(world);
		
		return this.activeTradeStations;
	}

	private void refreshActiveTradeStations(World world) {
		activeTradeStations = new LinkedHashMap<String, TradeStation>();
		File file = world.getSaveHandler().getMapFileFromName("dummy").getParentFile();
		if(!file.exists() || !file.isDirectory())
			return;
		
		for(String str : file.list()) {
			if(!str.startsWith(TradeStation.SAVE_NAME))
				continue;
			if(!str.endsWith(".dat"))
				continue;
			
			TradeStation trade = getTradeStation(world, str.replace(TradeStation.SAVE_NAME, "").replace(".dat", ""));
			if(trade == null)
				continue;
			
			registerTradeStation(trade);
		}
	}
	
	@Override
	public void registerTradeStation(TradeStation trade) {
		if(activeTradeStations == null)
			return;
		
		if (!activeTradeStations.containsKey(trade.getMoniker())) {
			activeTradeStations.put(trade.getMoniker(), trade);
		}

	}

	@Override
	public void deregisterTradeStation(TradeStation trade) {
		if(activeTradeStations == null)
			return;
		
		activeTradeStations.remove(trade.getMoniker());
	}

	// / STAMP MANAGMENT
	@Override
	public ItemStack getAnyStamp(int max) {
		return getAnyStamp(EnumPostage.values(), max);
	}

	@Override
	public ItemStack getAnyStamp(EnumPostage postage, int max) {
		return getAnyStamp(new EnumPostage[] { postage }, max);
	}

	@Override
	public ItemStack getAnyStamp(EnumPostage[] postages, int max) {

		for (EnumPostage postage : postages) {

			int collected = 0;
			if (collectedPostage[postage.ordinal()] <= 0) {
				continue;
			}

			if (max >= collectedPostage[postage.ordinal()]) {
				collected = collectedPostage[postage.ordinal()];
				collectedPostage[postage.ordinal()] = 0;
			} else {
				collected = max;
				collectedPostage[postage.ordinal()] -= max;
			}

			if (collected > 0)
				return new ItemStack(ForestryItem.stamps, collected, postage.ordinal() - 1);
		}

		return null;
	}

	// / DELIVERY
	@Override
	public IPostalState lodgeLetter(World world, ItemStack itemstack, boolean doLodge) {
		ILetter letter = ItemLetter.getLetter(itemstack);

		if (letter.isProcessed())
			return EnumDeliveryState.ALREADY_MAILED;

		if (!letter.isPostPaid())
			return EnumDeliveryState.NOT_POSTPAID;

		if (!letter.isMailable())
			return EnumDeliveryState.NOT_MAILABLE;

		IPostalState state = EnumDeliveryState.NOT_MAILABLE;
		for (MailAddress address : letter.getRecipients()) {
			if (address.isPlayer()) {
				state = storeInPOBox(world, address, itemstack, doLodge);
			} else if (address.getType() == EnumAddressee.TRADER) {
				state = handleTradeLetter(world, address, itemstack, doLodge);
			}
		}

		if (!state.isOk())
			return state;

		collectPostage(letter.getPostage());

		markDirty();
		return EnumDeliveryState.OK;

	}

	private IPostalState handleTradeLetter(World world, MailAddress address, ItemStack letterstack, boolean doLodge) {
		IPostalState state = EnumDeliveryState.NOT_MAILABLE;

		TradeStation trade = getTradeStation(world, address.getIdentifier());
		if (trade == null)
			return EnumDeliveryState.NO_MAILBOX;

		state = trade.handleLetter(world, address, letterstack, doLodge);

		return state;
	}

	private EnumDeliveryState storeInPOBox(World world, MailAddress address, ItemStack letterstack, boolean doLodge) {

		if (!address.isPlayer())
			return EnumDeliveryState.NOT_MAILABLE;

		POBox pobox = getPOBox(world, address.getIdentifier());
		if (pobox == null)
			return EnumDeliveryState.NO_MAILBOX;

		if (!pobox.storeLetter(letterstack.copy()))
			return EnumDeliveryState.MAILBOX_FULL;
		else {
			PluginForestryMail.proxy.setPOBoxInfo(world, address.getIdentifier(), pobox.getPOBoxInfo());
		}

		return EnumDeliveryState.OK;
	}

	@Override
	public void collectPostage(ItemStack[] stamps) {
		for (ItemStack stamp : stamps) {
			if (stamp == null) {
				continue;
			}

			if (stamp.getItem() instanceof IStamps) {
				EnumPostage postage = ((IStamps) stamp.getItem()).getPostage(stamp);
				collectedPostage[postage.ordinal()] += stamp.stackSize;
			}
		}
	}

}
