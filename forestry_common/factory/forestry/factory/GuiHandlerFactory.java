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
package forestry.factory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.factory.gui.ContainerBottler;
import forestry.factory.gui.ContainerCarpenter;
import forestry.factory.gui.ContainerCentrifuge;
import forestry.factory.gui.ContainerFabricator;
import forestry.factory.gui.ContainerFermenter;
import forestry.factory.gui.ContainerMoistener;
import forestry.factory.gui.ContainerRaintank;
import forestry.factory.gui.ContainerSqueezer;
import forestry.factory.gui.ContainerStill;
import forestry.factory.gui.GuiBottler;
import forestry.factory.gui.GuiCarpenter;
import forestry.factory.gui.GuiCentrifuge;
import forestry.factory.gui.GuiFabricator;
import forestry.factory.gui.GuiFermenter;
import forestry.factory.gui.GuiMoistener;
import forestry.factory.gui.GuiRaintank;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.gui.GuiStill;

public class GuiHandlerFactory extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {

		case BottlerGUI:
			return new GuiBottler(player.inventory, getTileMachine(world, x, y, z));

		case CarpenterGUI:
			return new GuiCarpenter(player.inventory, getTileMachine(world, x, y, z));

		case CentrifugeGUI:
			return new GuiCentrifuge(player.inventory, getTileMachine(world, x, y, z));

		case FabricatorGUI:
			return new GuiFabricator(player.inventory, getTileMachine(world, x, y, z));

		case FermenterGUI:
			return new GuiFermenter(player.inventory, getTileMachine(world, x, y, z));

		case MoistenerGUI:
			return new GuiMoistener(player.inventory, getTileMachine(world, x, y, z));

		case RaintankGUI:
			return new GuiRaintank(player.inventory, getTileMachine(world, x, y, z));

		case SqueezerGUI:
			return new GuiSqueezer(player.inventory, getTileMachine(world, x, y, z));

		case StillGUI:
			return new GuiStill(player.inventory, getTileMachine(world, x, y, z));

		default:
			return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {

		case BottlerGUI:
			return new ContainerBottler(player.inventory, getTileMachine(world, x, y, z));

		case CarpenterGUI:
			return new ContainerCarpenter(player.inventory, getTileMachine(world, x, y, z));

		case CentrifugeGUI:
			return new ContainerCentrifuge(player.inventory, getTileMachine(world, x, y, z));

		case FabricatorGUI:
			return new ContainerFabricator(player.inventory, getTileMachine(world, x, y, z));

		case FermenterGUI:
			return new ContainerFermenter(player.inventory, getTileMachine(world, x, y, z));

		case MoistenerGUI:
			return new ContainerMoistener(player.inventory, getTileMachine(world, x, y, z));

		case RaintankGUI:
			return new ContainerRaintank(player.inventory, getTileMachine(world, x, y, z));

		case SqueezerGUI:
			return new ContainerSqueezer(player.inventory, getTileMachine(world, x, y, z));

		case StillGUI:
			return new ContainerStill(player.inventory, getTileMachine(world, x, y, z));

		default:
			return null;

		}
	}

}
