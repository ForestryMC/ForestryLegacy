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
package forestry.cultivation.proxy;

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.core.interfaces.IBlockRenderer;
import forestry.cultivation.gadgets.TileHarvester;
import forestry.cultivation.gadgets.TilePlanter;

public class ProxyCultivation {
	public void registerPlanterTE() {
		GameRegistry.registerTileEntity(TilePlanter.class, "forestry.Planter");
	}

	public void registerHarvesterTE() {
		GameRegistry.registerTileEntity(TileHarvester.class, "forestry.Harvester");
	}

	public IBlockRenderer getRenderDefaultPlanter(String gfxBase) {
		return null;
	}
}
