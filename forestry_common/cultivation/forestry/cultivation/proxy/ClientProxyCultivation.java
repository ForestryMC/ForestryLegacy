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

import cpw.mods.fml.client.registry.ClientRegistry;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.render.RenderForestryTile;
import forestry.cultivation.gadgets.TilePlanter;
import forestry.cultivation.render.RenderPlanter;

public class ClientProxyCultivation extends ProxyCultivation {

	@Override
	public void registerPlanterTE() {
		super.registerPlanterTE();
		ClientRegistry.bindTileEntitySpecialRenderer(TilePlanter.class, new RenderForestryTile());
	}

	@Override
	public IBlockRenderer getRenderDefaultPlanter(String gfxBase) {
		return new RenderPlanter(gfxBase);
	}
}
