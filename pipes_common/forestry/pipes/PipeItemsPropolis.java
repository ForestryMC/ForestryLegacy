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

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.IAllele;
import forestry.core.config.Defaults;

public class PipeItemsPropolis extends Pipe implements IPipeTransportItemsHook {

	PipeLogicPropolis pipeLogic;

	public PipeItemsPropolis(int itemID) {
		super(new PipeTransportItems(), new PipeLogicPropolis(), itemID);
		pipeLogic = (PipeLogicPropolis) logic;
		((PipeTransportItems) transport).allowBouncing = true;
	}

	@Override
	public String getTextureFile() {
		return Defaults.TEXTURE_PATH_BLOCKS + "/pipes.png";
	}

	@Override
	public int getTextureIndex(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN)
			return 0;

		return direction.ordinal() + 1;
	}

	@Override
	public LinkedList<ForgeDirection> filterPossibleMovements(LinkedList<ForgeDirection> possibleOrientations, Position pos, IPipedItem item) {

		LinkedList<ForgeDirection> filteredOrientations = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> typedOrientations = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> defaultOrientations = new LinkedList<ForgeDirection>();

		// We need a bee!
		EnumFilterType type = EnumFilterType.getType(item.getItemStack());
		IBee bee = null;

		if (type != EnumFilterType.ITEM) {
			bee = BeeManager.beeInterface.getBee(item.getItemStack());
		}

		// Filtered outputs
		for (ForgeDirection dir : possibleOrientations) {

			// Continue if this direction is closed.
			if (pipeLogic.isClosed(dir)) {
				continue;
			}

			if (pipeLogic.isIndiscriminate(dir)) {
				defaultOrientations.add(dir);
				continue;
			}

			// We need to match the type for this orientation's filter
			if (!pipeLogic.matchType(dir, type, bee)) {
				continue;
			}

			// Passing the type filter is enough for non-bee items.
			if (type == EnumFilterType.ITEM) {
				filteredOrientations.add(dir);
				continue;
			}

			ArrayList<IAllele[]> filters = pipeLogic.getGenomeFilters(dir);
			// If we have no genome filters, this is only a typed route.
			if (filters.size() <= 0) {
				typedOrientations.add(dir);
				continue;
			}

			// Bees need to match one of the genome filters
			for (IAllele[] pattern : filters) {
				if (pipeLogic.matchAllele(pattern[0], bee.getIdent()) && pipeLogic.matchAllele(pattern[1], bee.getGenome().getSecondaryAsBee().getUID())) {
					filteredOrientations.add(dir);
				}
			}
		}

		if (filteredOrientations.size() > 0)
			return filteredOrientations;
		else if (typedOrientations.size() > 0)
			return typedOrientations;
		else
			return defaultOrientations;

	}

	@Override
	public void entityEntered(IPipedItem item, ForgeDirection orientation) {
		// A bit of speed to perhaps prevent bees from popping out of the pipe.
		item.setSpeed(Utils.pipeNormalSpeed * 20F);
	}

	@Override
	public void readjustSpeed(IPipedItem item) {
		((PipeTransportItems) transport).defaultReajustSpeed(item);
	}

}
