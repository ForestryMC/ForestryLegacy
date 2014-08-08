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
package forestry.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.apiculture.BeeManager;
import forestry.apiculture.gui.GuiApiaristInventory;
import forestry.core.GuiHandlerBase;
import forestry.core.config.Defaults;
import forestry.core.network.GuiId;
import forestry.core.utils.ItemInventory;
import forestry.storage.gui.ContainerApiaristBackpack;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.GuiBackpack;
import forestry.storage.gui.GuiBackpackT2;
import forestry.storage.items.ItemBackpack;

public class GuiHandlerStorage extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		ItemStack equipped;
		switch (GuiId.values()[id]) {

		case ApiaristBackpackGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;
			ItemInventory inventory = new ItemInventory(Defaults.SLOTS_BACKPACK_APIARIST, equipped);
			return new GuiApiaristInventory(player, new ContainerApiaristBackpack(player.inventory, inventory, 5, 25), inventory);

		case BackpackGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;
			if (equipped.getItem() instanceof ItemBackpack)
				return new GuiBackpack(new ContainerBackpack(player, new ItemInventory(Defaults.SLOTS_BACKPACK_DEFAULT, equipped)));
			else
				return null;

		case BackpackT2GUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;
			return new GuiBackpackT2(new ContainerBackpack(player, new ItemInventory(Defaults.SLOTS_BACKPACK_T2, equipped)));

		default:
			return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		ItemStack equipped;
		switch (GuiId.values()[id]) {

		case ApiaristBackpackGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;

			BeeManager.breedingManager.getApiaristTracker(world, player.username).synchToPlayer(player);
			return new ContainerApiaristBackpack(player.inventory, new ItemInventory(Defaults.SLOTS_BACKPACK_APIARIST, equipped), 5, 25);

		case BackpackGUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;

			return new ContainerBackpack(player, new ItemInventory(Defaults.SLOTS_BACKPACK_DEFAULT, equipped));

		case BackpackT2GUI:
			equipped = getEquippedItem(player);
			if(equipped == null)
				return null;

			return new ContainerBackpack(player, new ItemInventory(Defaults.SLOTS_BACKPACK_T2, equipped));

		default:
			return null;

		}
	}

}
