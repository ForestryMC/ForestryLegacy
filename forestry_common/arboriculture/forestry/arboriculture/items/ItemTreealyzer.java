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
package forestry.arboriculture.items;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.interfaces.IInventoriedItem;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;

public class ItemTreealyzer extends ItemForestry implements IInventoriedItem {

	public static class TreealyzerInventory extends ItemInventory implements IErrorSource, IHintSource {

		public final static int SLOT_SPECIMEN = 0;
		public final static int SLOT_ANALYZE_1 = 1;
		public final static int SLOT_ANALYZE_2 = 2;
		public final static int SLOT_ANALYZE_3 = 3;
		public final static int SLOT_ANALYZE_4 = 4;
		public final static int SLOT_ANALYZE_5 = 6;
		public final static int SLOT_ENERGY = 5;

		EntityPlayer player;

		public TreealyzerInventory(EntityPlayer player) {
			super(7);
			this.player = player;
		}

		public TreealyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(7, itemStack);
			this.player = player;
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {

			NBTTagList nbttaglist = new NBTTagList();
			for (int i = SLOT_ENERGY; i < SLOT_ENERGY + 1; i++)
				if (inventoryStacks[i] != null) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte) i);
					inventoryStacks[i].writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			nbttagcompound.setTag("Items", nbttaglist);

		}

		private boolean isEnergy(ItemStack itemstack) {
			if (itemstack == null || itemstack.stackSize <= 0)
				return false;

			return itemstack.itemID == ForestryItem.honeyDrop.itemID || itemstack.itemID == ForestryItem.honeydew.itemID;
		}

		private void tryAnalyze() {

			// Analyzed slot occupied, abort
			if (inventoryStacks[SLOT_ANALYZE_1] != null || inventoryStacks[SLOT_ANALYZE_2] != null || inventoryStacks[SLOT_ANALYZE_3] != null
					|| inventoryStacks[SLOT_ANALYZE_4] != null || inventoryStacks[SLOT_ANALYZE_5] != null)
				return;

			// Source slot to analyze empty
			if (getStackInSlot(SLOT_SPECIMEN) == null)
				return;
			
			ITree tree = TreeManager.treeInterface.getTree(getStackInSlot(SLOT_SPECIMEN));
			// No tree, abort
			if (tree == null) {
				
				for(Map.Entry<ItemStack, IIndividual> entry : AlleleManager.ersatzSaplings.entrySet()) {
					if(entry.getKey().itemID != getStackInSlot(SLOT_SPECIMEN).itemID)
						continue;
					if(entry.getKey().getItemDamage() != getStackInSlot(SLOT_SPECIMEN).getItemDamage())
						continue;
					
					if(entry.getValue() instanceof ITree)
						tree = (ITree)entry.getValue();
					
					ItemStack ersatz = TreeManager.treeInterface.getGermlingStack(tree, EnumGermlingType.SAPLING);
					ersatz.stackSize = getStackInSlot(SLOT_SPECIMEN).stackSize;
					setInventorySlotContents(SLOT_SPECIMEN, ersatz);
				}

				if(tree == null)
					return;
			}

			// Analyze if necessary
			if (!tree.isAnalyzed()) {

				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY)))
					return;

				tree.analyze();
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				tree.writeToNBT(nbttagcompound);
				getStackInSlot(SLOT_SPECIMEN).setTagCompound(nbttagcompound);

				// Decrease energy
				decrStackSize(SLOT_ENERGY, 1);
			}

			setInventorySlotContents(SLOT_ANALYZE_1, getStackInSlot(SLOT_SPECIMEN));
			setInventorySlotContents(SLOT_SPECIMEN, null);
		}

		@Override
		public void onInventoryChanged() {
			//if (!Proxies.common.isSimulating(player.worldObj))
			//	return;
			tryAnalyze();
		}

		// / IHINTSOURCE
		@Override
		public boolean hasHints() {
			return Config.hints.get("treealyzer") != null && Config.hints.get("treealyzer").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("treealyzer");
		}

		// / IERRORSOURCE
		@Override
		public boolean throwsErrors() {
			return true;
		}

		@Override
		public EnumErrorCode getErrorState() {
			if (TreeManager.treeInterface.isGermling(inventoryStacks[SLOT_SPECIMEN]) && !isEnergy(getStackInSlot(SLOT_ENERGY)))
				return EnumErrorCode.NOHONEY;

			return EnumErrorCode.OK;
		}
	}

	public ItemTreealyzer(int i) {
		super(i);
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.TreealyzerGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}
}
