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
package forestry.core;

import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import forestry.api.core.IOreDictionaryHandler;
import forestry.api.core.IPickupHandler;
import forestry.api.core.ISaveEventHandler;
import forestry.plugins.PluginManager;

public class EventHandlerCore {

	@ForgeSubscribe
	public void handleItemPickup(EntityItemPickupEvent event) {

		if (event.isCanceled())
			return;

		for (IPickupHandler handler : PluginManager.pickupHandlers) {
			if (!handler.onItemPickup(event.entityPlayer, event.item)) {
				event.setResult(Result.ALLOW);
				return;
			}
		}

	}

	@ForgeSubscribe
	public void handleOreRegistration(OreDictionary.OreRegisterEvent event) {

		if (event.isCanceled())
			return;

		for (IOreDictionaryHandler handler : PluginManager.dictionaryHandlers) {
			handler.onOreRegistration(event.Name, event.Ore);
		}
	}

	@ForgeSubscribe
	public void handleWorldLoad(WorldEvent.Load event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldLoad(event.world);
		}
	}

	@ForgeSubscribe
	public void handleWorldSave(WorldEvent.Save event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldSave(event.world);
		}
	}

	@ForgeSubscribe
	public void handleWorldUnload(WorldEvent.Unload event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldUnload(event.world);
		}
	}
}
