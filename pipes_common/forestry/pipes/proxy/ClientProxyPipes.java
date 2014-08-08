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
package forestry.pipes.proxy;

import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.core.utils.Localization;
import buildcraft.transport.TransportProxyClient;
import forestry.plugins.PluginPropolisPipe;

public class ClientProxyPipes extends ProxyPipes {

	@Override
	public void registerCustomItemRenderer(int itemID, IItemRenderer basemod) {
		MinecraftForgeClient.registerItemRenderer(itemID, basemod);
	}

	@Override
	public void initPropolisPipe() {
		super.initPropolisPipe();
		registerCustomItemRenderer(PluginPropolisPipe.pipeItemsPropolis.itemID, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void addLocalizations() {
		Localization.addLocalization("/lang/forestry/pipes/", "en_US");
	}

}
