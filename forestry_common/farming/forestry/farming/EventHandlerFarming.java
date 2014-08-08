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
package forestry.farming;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.BonemealEvent;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.farming.gadgets.BlockMushroom;

public class EventHandlerFarming {

	@ForgeSubscribe
	public void handleBonemeal(BonemealEvent event) {

		if (!Proxies.common.isSimulating(event.world))
			return;

		int blockid = event.world.getBlockId(event.X, event.Y, event.Z);
		if(blockid != ForestryBlock.mushroom.blockID)
			return;
		
		((BlockMushroom)ForestryBlock.mushroom).growTree(event.world, event.X, event.Y, event.Z, event.world.rand);
		event.setResult(Result.ALLOW);
	}
}
