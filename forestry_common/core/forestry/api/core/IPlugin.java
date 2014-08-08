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
package forestry.api.core;

import java.util.Random;

import net.minecraft.command.ICommand;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * Plugins get loaded at the beginning of Forestry's ModsLoaded() if isAvailable() returns true.
 * 
 * @author SirSengir
 */
public interface IPlugin {
	public boolean isAvailable();

	public void preInit();

	public void doInit();

	public void postInit();

	/**
	 * Use @PluginInfo!
	 */
	public String getDescription();

	public void generateSurface(World world, Random rand, int chunkX, int chunkZ);

	public IGuiHandler getGuiHandler();

	public IPacketHandler getPacketHandler();

	public IPickupHandler getPickupHandler();

	public IResupplyHandler getResupplyHandler();

	public ISaveEventHandler getSaveEventHandler();

	public IOreDictionaryHandler getDictionaryHandler();

	public ICommand[] getConsoleCommands();
}
