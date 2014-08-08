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
package forestry.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.apiculture.BeeManager;
import forestry.core.network.GuiId;
import forestry.pipes.gui.ContainerPropolisPipe;
import forestry.pipes.gui.GuiPropolisPipe;

public class GuiHandlerPipes implements IGuiHandler {

	private Pipe getPipe(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile == null)
			return null;

		if (!(tile instanceof TileGenericPipe))
			return null;

		return ((TileGenericPipe) tile).pipe;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case PropolisPipeGUI:
			return new GuiPropolisPipe(player, getPipe(world, x, y, z));
		default:
			return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case PropolisPipeGUI:
			BeeManager.breedingManager.getApiaristTracker(world, player.username).synchToPlayer(player);
			return new ContainerPropolisPipe(player.inventory, getPipe(world, x, y, z));
		default:
			return null;

		}
	}

}
