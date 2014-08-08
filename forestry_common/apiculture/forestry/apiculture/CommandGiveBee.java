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
package forestry.apiculture;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.core.utils.CommandMC;

public class CommandGiveBee extends CommandMC {

	EnumBeeType type;

	public CommandGiveBee(EnumBeeType type) {
		this.type = type;
	}

	public String getCommandName() {
		return "give" + type.toString().toLowerCase();
	}

	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return par1ICommandSender.translateString("/" + getCommandName() + " <player-name> <species-name>");
	}

	public void processCommand(ICommandSender sender, String[] arguments) {
		if (arguments.length >= 2) {

			EntityPlayer player = getPlayerFromName(arguments[0]);

			String parameter = arguments[1];

			IAlleleBeeSpecies species = null;

			search: for (String uid : AlleleManager.alleleRegistry.getRegisteredAlleles().keySet()) {

				if (!uid.equals(parameter)) {
					continue;
				}

				if (AlleleManager.alleleRegistry.getAllele(uid) instanceof IAlleleBeeSpecies) {
					species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(uid);
					break search;
				}
			}

			search: if (species == null) {
				for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
					if (allele instanceof IAlleleBeeSpecies) {
						if (((IAlleleBeeSpecies) allele).getName().equals(parameter)) {
							species = (IAlleleBeeSpecies) allele;
							break search;
						}

					}
				}
			}

			if (species == null)
				throw new SpeciesNotFoundException(parameter);

			IAllele[] template = BeeManager.breedingManager.getBeeTemplate(species.getUID());

			if (template == null)
				throw new TemplateNotFoundException(species);

			IBeeGenome genome = BeeManager.beeInterface.templateAsGenome(template);

			IBee bee = BeeManager.beeInterface.getBee(player.worldObj, genome);

			ItemStack beestack = BeeManager.beeInterface.getBeeStack(bee, type);
			player.dropPlayerItem(beestack);
			notifyAdmins(sender, "Player %s was given a %s bee.", player.getEntityName(), ((IAlleleSpecies) template[0]).getName());
		} else
			throw new WrongUsageException("/" + getCommandName() + " <player-name> <species-name>");
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	public List addTabCompletionOptions(ICommandSender sender, String[] parameters) {
		if (parameters.length == 1)
			return getListOfStringsMatchingLastWord(parameters, this.getPlayers());
		else if (parameters.length == 2)
			return getListOfStringsMatchingLastWord(parameters, this.getSpecies());
		else
			return null;
	}

	protected String[] getSpecies() {
		List<String> species = new ArrayList<String>();

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleBeeSpecies) {
				species.add(((IAlleleSpecies) allele).getName());
			}
		}

		return species.toArray(new String[] {});
	}
}
