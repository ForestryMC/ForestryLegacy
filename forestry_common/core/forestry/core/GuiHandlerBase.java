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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.core.gadgets.TileForestry;
import forestry.core.gadgets.TileMachine;
import forestry.core.gadgets.TileMill;
import forestry.core.proxy.Proxies;

public abstract class GuiHandlerBase implements IGuiHandler {

	public TileForestry getTileForestry(World world, int x, int y, int z) {
		try {
			return (TileForestry) world.getBlockTileEntity(x, y, z);
		} catch (Exception ex) {
			Proxies.log.warning("Failed to cast a tile entity to a TileForestry at " + x + "/" + y + "/" + z);
		}

		return null;
	}

	public TileMachine getTileMachine(World world, int x, int y, int z) {
		try {
			return (TileMachine) world.getBlockTileEntity(x, y, z);
		} catch (Exception ex) {
			Proxies.log.warning("Failed to cast a tile entity to a TileMachine at " + x + "/" + y + "/" + z);
		}

		return null;
	}

	public TileMill getTileMill(World world, int x, int y, int z) {
		try {
			return (TileMill) world.getBlockTileEntity(x, y, z);
		} catch (Exception ex) {
			Proxies.log.warning("Failed to cast a tile entity to a TileMill at " + x + "/" + y + "/" + z);
		}

		return null;

	}

	public ItemStack getEquippedItem(EntityPlayer player) {
		return player.getCurrentEquippedItem();
	}

}
