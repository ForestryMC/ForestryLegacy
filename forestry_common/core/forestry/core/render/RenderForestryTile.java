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
package forestry.core.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import forestry.core.gadgets.TileForestry;
import forestry.core.interfaces.IBlockRenderer;

public class RenderForestryTile extends TileEntitySpecialRenderer {

	public RenderForestryTile() {
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		TileForestry tile = (TileForestry) tileentity;

		if (tile.pack == null)
			return;

		if (tile.pack.renderer != null) {
			IBlockRenderer renderer = tile.pack.renderer;
			renderer.renderTileEntityAt(tileentity, d, d1, d2, f);
		}
	}
}
