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
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.circuits.GuiSolderingIron;
import forestry.core.circuits.ItemSolderingIron.SolderingInventory;
import forestry.core.network.GuiId;
import forestry.plugins.PluginManager;

public class GuiHandler extends GuiHandlerBase {

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id < GuiId.values().length) {
			switch (GuiId.values()[id]) {

			case SolderingIronGUI:
				return new GuiSolderingIron(player.inventory, new SolderingInventory());

			default:
				for (IGuiHandler handler : PluginManager.guiHandlers) {
					Object element = handler.getClientGuiElement(id, player, world, x, y, z);
					if (element != null)
						return element;
				}

				return null;
			}
		}

		return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id < GuiId.values().length) {
			switch (GuiId.values()[id]) {

			case SolderingIronGUI:
				return new ContainerSolderingIron(player.inventory, new SolderingInventory());

			default:
				for (IGuiHandler handler : PluginManager.guiHandlers) {
					Object element = handler.getServerGuiElement(id, player, world, x, y, z);
					if (element != null)
						return element;
				}

				return null;

			}
		}

		return null;
	}

}
