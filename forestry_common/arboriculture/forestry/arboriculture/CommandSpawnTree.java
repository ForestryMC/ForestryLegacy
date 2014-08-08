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
package forestry.arboriculture;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.gen.feature.WorldGenerator;
import forestry.api.arboriculture.TreeManager;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.genetics.TreeTemplates;
import forestry.arboriculture.worldgen.WorldGenBalsa;
import forestry.core.utils.CommandMC;

public class CommandSpawnTree extends CommandMC {

	public CommandSpawnTree() {
	}

	public String getCommandName() {
		return "spawntree";
	}

	public String getCommandUsage(ICommandSender sender) {
		return sender.translateString("/" + getCommandName() + " <player-name> <species-name>");
	}

	public void processCommand(ICommandSender sender, String[] arguments) {

		EntityPlayer player = this.getPlayerFromName(sender.getCommandSenderName());

		int x = (int) player.posX;
		int y = (int) (player.posY);
		int z = (int) player.posZ;

		WorldGenerator gen = new WorldGenBalsa((ITreeGenData)TreeManager.treeInterface.getTree(player.worldObj,
				TreeTemplates.templateAsGenome(TreeManager.breedingManager.getTreeTemplate("treeBalsa"))));

		gen.generate(player.worldObj, player.worldObj.rand, x, y, z);

	}
}
