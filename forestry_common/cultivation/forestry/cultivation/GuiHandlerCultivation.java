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
package forestry.cultivation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.cultivation.gadgets.TilePlanter;
import forestry.cultivation.gui.ContainerArboretum;
import forestry.cultivation.gui.ContainerFarm;
import forestry.cultivation.gui.ContainerForester;
import forestry.cultivation.gui.ContainerPlantation;
import forestry.cultivation.gui.ContainerPlanterSoilGermling;
import forestry.cultivation.gui.ContainerPlanterSoilWaste;
import forestry.cultivation.gui.ContainerPumpkinFarm;
import forestry.cultivation.gui.GuiArboretum;
import forestry.cultivation.gui.GuiFarm;
import forestry.cultivation.gui.GuiForester;
import forestry.cultivation.gui.GuiNetherFarm;
import forestry.cultivation.gui.GuiPeatBog;
import forestry.cultivation.gui.GuiPlantation;
import forestry.cultivation.gui.GuiPumpkinFarm;

public class GuiHandlerCultivation extends GuiHandlerBase {

	public TilePlanter getTilePlanter(World world, int x, int y, int z) {
		return (TilePlanter) world.getBlockTileEntity(x, y, z);
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case ArboretumGUI:
			return new GuiArboretum(player.inventory, getTilePlanter(world, x, y, z));

		case FarmGUI:
			return new GuiFarm(player.inventory, getTilePlanter(world, x, y, z));

		case ForesterGUI:
			return new GuiForester(player.inventory, getTileMill(world, x, y, z));

		case MushroomFarmGUI:
			return new GuiArboretum(player.inventory, getTilePlanter(world, x, y, z));

		case NetherFarmGUI:
			return new GuiNetherFarm(player.inventory, getTilePlanter(world, x, y, z));

		case PeatBogGUI:
			return new GuiPeatBog(player.inventory, getTilePlanter(world, x, y, z));

		case PlantationGUI:
			return new GuiPlantation(player.inventory, getTilePlanter(world, x, y, z));

		case PumpkinFarmGUI:
			return new GuiPumpkinFarm(player.inventory, getTilePlanter(world, x, y, z));

		default:
			return null;

		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length)
			return null;

		switch (GuiId.values()[id]) {
		case ArboretumGUI:
			return new ContainerArboretum(player.inventory, getTilePlanter(world, x, y, z));

		case FarmGUI:
			return new ContainerFarm(player.inventory, getTilePlanter(world, x, y, z));

		case ForesterGUI:
			return new ContainerForester(player.inventory, getTileMill(world, x, y, z));

		case MushroomFarmGUI:
			return new ContainerArboretum(player.inventory, getTilePlanter(world, x, y, z));

		case NetherFarmGUI:
			return new ContainerPlanterSoilGermling(player.inventory, getTilePlanter(world, x, y, z));

		case PeatBogGUI:
			return new ContainerPlanterSoilWaste(player.inventory, getTilePlanter(world, x, y, z));

		case PlantationGUI:
			return new ContainerPlantation(player.inventory, getTilePlanter(world, x, y, z));

		case PumpkinFarmGUI:
			return new ContainerPumpkinFarm(player.inventory, getTilePlanter(world, x, y, z));

		default:
			return null;

		}
	}

}
