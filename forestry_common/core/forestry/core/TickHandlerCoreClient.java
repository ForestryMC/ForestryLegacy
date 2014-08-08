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

import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import forestry.core.config.Config;
import forestry.core.config.Version;
import forestry.core.proxy.Proxies;

public class TickHandlerCoreClient implements ITickHandler, Runnable {

	private static ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<String>();

	public void queueChatMessage(String message) {
		messages.add(message);
	}

	private boolean nagged;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

		if (messages.size() > 0) {
			String message;
			EntityPlayer player = (EntityPlayer) tickData[0];
			while ((message = messages.poll()) != null) {
				player.sendChatToPlayer(message);
			}
		}

		if (nagged)
			return;

		if (!Config.disableNags) {
			if (Proxies.common.isModLoaded("GregTech_Addon")) {
				queueChatMessage("\u00A76Forestry may have been modified by GregTech. It may behave unexpectedly and some config options may not work. Do not report issues with this install!");
			}
		}

		if (!Config.disableVersionCheck) {
			(new Thread(new TickHandlerCoreClient())).start();
		}
		
		if(Config.invalidFingerprint) {
			queueChatMessage("\u00A76Forestry's jar file was tampered with. Some machines have shut down and beekeeping has grown dangerous. Get a new jar from the official download page to fix that!");
		}
		
		nagged = true;
	}

	@Override
	public void run() {
		if (Version.needsUpdateNoticeAndMarkAsSeen()) {
			queueChatMessage(String.format("\u00A7cNew version of Forestry available: %s for Minecraft %s", Version.getRecommendedVersion(),
					Proxies.common.getMinecraftVersion()));
			queueChatMessage("\u00A7cThis message only displays once. Type '/forestry version' to see the changelog.");
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "Forestry - Player update tick";
	}

}
