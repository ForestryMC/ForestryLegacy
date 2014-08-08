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
package forestry.arboriculture;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.BonemealEvent;
import forestry.api.genetics.IFruitBearer;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.core.proxy.Proxies;

public class EventHandlerArboriculture {

	@ForgeSubscribe
	public void handleBonemeal(BonemealEvent event) {

		if (!Proxies.common.isSimulating(event.world))
			return;

		TileEntity tile = event.world.getBlockTileEntity(event.X, event.Y, event.Z);
		if (tile instanceof TileSapling) {
			if (((TileSapling) tile).tryGrow(true)) {
				event.setResult(Result.ALLOW);
			}
		} else if(tile instanceof IFruitBearer) {
			IFruitBearer bearer = (IFruitBearer)tile;
			if(bearer.getRipeness() <= 1.0f) {
				bearer.addRipeness(1.0f);
				event.setResult(Result.ALLOW);
			}
		}
	}

}
