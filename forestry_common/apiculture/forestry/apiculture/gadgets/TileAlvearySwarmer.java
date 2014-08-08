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
package forestry.apiculture.gadgets;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

import buildcraft.api.inventory.ISpecialInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.ForestryAPI;
import forestry.apiculture.worldgen.WorldGenHive;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.TileInventoryAdapter;

public class TileAlvearySwarmer extends TileAlveary implements ISpecialInventory {

	public static final int TEXTURE_OFF = 55;
	public static final int TEXTURE_ON = 56;

	TileInventoryAdapter swarmerInventory;
	private Stack<ItemStack> pendingSpawns = new Stack<ItemStack>();

	public TileAlvearySwarmer() {
		super(2);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (swarmerInventory == null) {
			createInventory();
		}
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.AlvearySwarmerGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* UPDATING */
	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (worldObj.getWorldTime() % 100 != 0)
			if (pendingSpawns.size() > 0) {
				trySpawnSwarm();
			}

		if (worldObj.getWorldTime() % 500 != 0)
			return;

		if (!this.hasMaster())
			return;

		IAlvearyComponent master = (IAlvearyComponent) this.getCentralTE();
		if (!(master instanceof IBeeHousing))
			return;

		IBeeHousing housing = (IBeeHousing) master;
		ItemStack queenstack = housing.getQueen();
		if (queenstack == null)
			return;
		if (!BeeManager.beeInterface.isMated(queenstack))
			return;

		// Calculate chance
		int slot = getInducerSlot();
		if (slot < 0)
			return;
		int chance = getChanceFor(swarmerInventory.getStackInSlot(slot));

		// Remove resource
		swarmerInventory.decrStackSize(slot, 1);

		// Try to spawn princess
		if (worldObj.rand.nextInt(1000) >= chance)
			return;

		// Queue swarm spawn
		IBee spawn = BeeManager.beeInterface.getBee(queenstack);
		spawn.setIsNatural(false);
		pendingSpawns.push(BeeManager.beeInterface.getBeeStack(spawn, EnumBeeType.PRINCESS));

	}

	private int getChanceFor(ItemStack stack) {
		Iterator it = BeeManager.inducers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<ItemStack, Integer> entry = (Entry<ItemStack, Integer>) it.next();
			if (entry.getKey().isItemEqual(stack))
				return entry.getValue();
		}

		return 0;
	}

	private int getInducerSlot() {
		for (int i = 0; i < swarmerInventory.getSizeInventory(); i++) {
			if (swarmerInventory.getStackInSlot(i) == null) {
				continue;
			}

			Iterator it = BeeManager.inducers.entrySet().iterator();
			while (it.hasNext()) {
				Entry<ItemStack, Integer> entry = (Entry<ItemStack, Integer>) it.next();
				if (entry.getKey().isItemEqual(swarmerInventory.getStackInSlot(i)))
					return i;
			}
		}

		return -1;
	}

	private void trySpawnSwarm() {

		ItemStack toSpawn = pendingSpawns.peek();
		WorldGenHive generator = new WorldGenHive(new ItemStack[] { toSpawn });

		int i = 0;
		while (i < 10) {
			i++;
			int spawnX = xCoord + worldObj.rand.nextInt(40 * 2) - 40;
			int spawnY = yCoord + worldObj.rand.nextInt(40);
			int spawnZ = zCoord + worldObj.rand.nextInt(40 * 2) - 40;
			if (generator.generate(worldObj, worldObj.rand, spawnX, spawnY, spawnZ)) {
				pendingSpawns.pop();
				break;
			}
		}

	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* TEXTURES */
	@Override
	public int getBlockTexture(int side, int metadata) {
		if(pendingSpawns.size() > 0)
			return TEXTURE_ON;
		else
			return TEXTURE_OFF;
	}
	
	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		if (swarmerInventory == null) {
			createInventory();
		}
		swarmerInventory.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingSpawns");
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
			pendingSpawns.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		if (swarmerInventory != null) {
			swarmerInventory.writeToNBT(nbttagcompound);
		}

		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] offspring = pendingSpawns.toArray(new ItemStack[pendingSpawns.size()]);
		for (int i = 0; i < offspring.length; i++)
			if (offspring[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				offspring[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingSpawns", nbttaglist);

	}

	@Override
	protected void createInventory() {
		swarmerInventory = new TileInventoryAdapter(this, 4, "SwarmInv");
	}

	@Override
	public IInventory getInventory() {
		return swarmerInventory;
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (swarmerInventory != null)
			return swarmerInventory.addStack(stack, false, doAdd);
		else
			return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		return null;
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		if (swarmerInventory != null)
			return swarmerInventory.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if (swarmerInventory != null)
			return swarmerInventory.getStackInSlot(slotIndex);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if (swarmerInventory != null)
			return swarmerInventory.decrStackSize(slotIndex, amount);
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if (swarmerInventory != null)
			return swarmerInventory.getStackInSlotOnClosing(slotIndex);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		// Client side handling for container synch
		if (swarmerInventory == null && !Proxies.common.isSimulating(worldObj)) {
			createInventory();
		}

		if (swarmerInventory != null) {
			swarmerInventory.setInventorySlotContents(slotIndex, itemstack);
		}
	}

	@Override
	public String getInvName() {
		return "tile.alvearySwarmer";
	}

	@Override
	public int getInventoryStackLimit() {
		if (swarmerInventory != null)
			return swarmerInventory.getInventoryStackLimit();
		else
			return 0;
	}

	@Override public void openChest() {}
	@Override public void closeChest() {}

}
