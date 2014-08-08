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

import java.util.LinkedHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPostOffice {

	void collectPostage(ItemStack[] stamps);

	IPostalState lodgeLetter(World world, ItemStack itemstack, boolean doLodge);

	ItemStack getAnyStamp(int max);

	ItemStack getAnyStamp(EnumPostage postage, int max);

	ItemStack getAnyStamp(EnumPostage[] postages, int max);

	void registerTradeStation(TradeStation trade);

	void deregisterTradeStation(TradeStation trade);

	LinkedHashMap<String, TradeStation> getActiveTradeStations(World world);
}
