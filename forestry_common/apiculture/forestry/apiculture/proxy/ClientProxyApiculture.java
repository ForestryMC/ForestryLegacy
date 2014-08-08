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
package forestry.apiculture.proxy;

import net.minecraft.world.World;
import forestry.apiculture.render.EntityBeeFX;
import forestry.core.config.Config;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;
import forestry.core.render.RenderAnalyzer;
import forestry.core.utils.Localization;

public class ClientProxyApiculture extends ProxyApiculture {

	@Override
	public void addBeeHiveFX(String texture, World world, double xCoord, double yCoord, double zCoord, int color, int areaX, int areaY, int areaZ) {
		if (!Config.enableParticleFX)
			return;

		if (world.rand.nextBoolean()) {
			Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityBeeFX(world, xCoord + 0.5D, yCoord + 0.75D, zCoord + 0.5D, 0.0f, 0.0f, 0.0f,
					color).setTexture(texture));
		} else {

			double spawnX = xCoord + world.rand.nextInt(areaX * 2) - areaX;
			double spawnY = yCoord + world.rand.nextInt(areaY);
			double spawnZ = zCoord + world.rand.nextInt(areaZ * 2) - areaZ;

			Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityBeeFX(world, spawnX, spawnY, spawnZ, 0.0f, 0.0f, 0.0f, color)
					.setTexture(texture));
		}
	}

	@Override
	public void addBeeSwarmFX(String texture, World world, double xCoord, double yCoord, double zCoord, int color) {
		if (!Config.enableParticleFX)
			return;

		if (world.rand.nextBoolean()) {
			Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityBeeFX(world, xCoord, yCoord, zCoord, 0.0f, 0.0f, 0.0f, color)
					.setTexture(texture));
		} else {

			double spawnX = xCoord + world.rand.nextInt(4) - 2;
			double spawnY = yCoord + world.rand.nextInt(4) - 2;
			double spawnZ = zCoord + world.rand.nextInt(4) - 2;

			Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityBeeFX(world, spawnX, spawnY, spawnZ, 0.0f, 0.0f, 0.0f, color)
					.setTexture(texture));
		}
	}

	@Override
	public IBlockRenderer getRendererAnalyzer(String gfxBase) {
		return new RenderAnalyzer(gfxBase);
	}

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/apiculture/");
	}


}
