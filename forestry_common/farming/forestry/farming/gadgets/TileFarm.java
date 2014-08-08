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
package forestry.farming.gadgets;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.core.gadgets.Gadget;
import forestry.core.gadgets.TileForestry;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Utils;

public abstract class TileFarm extends TileForestry implements IFarmComponent {

	public enum EnumFarmBlock {
		BRICK_STONE(0), BRICK_MOSSY(1), BRICK_CRACKED(2), BRICK(3), SANDSTONE_SMOOTH(4), SANDSTONE_CHISELED(5), BRICK_NETHER(6), BRICK_CHISELED(7);
		
		private final int textureColumn;
		private EnumFarmBlock(int textureColumn) {
			this.textureColumn = textureColumn;
		}
		
		public int getColumn() {
			return this.textureColumn;
		}
		
		public void saveToCompound(NBTTagCompound compound) {
			compound.setInteger("FarmBlock", this.ordinal());
		}
		
		public String getName() {
			switch(this) {
			case BRICK_MOSSY:
				return Item.itemsList[Block.stoneBrick.blockID].getItemDisplayName(new ItemStack(Block.stoneBrick, 1, 1));
			case BRICK_CRACKED:
				return Item.itemsList[Block.stoneBrick.blockID].getItemDisplayName(new ItemStack(Block.stoneBrick, 1, 2));
			case BRICK_CHISELED:
				return Item.itemsList[Block.stoneBrick.blockID].getItemDisplayName(new ItemStack(Block.stoneBrick, 1, 3));
			case BRICK:
				return Item.itemsList[Block.brick.blockID].getItemDisplayName(new ItemStack(Block.brick));
			case SANDSTONE_SMOOTH:
				return Item.itemsList[Block.sandStone.blockID].getItemDisplayName(new ItemStack(Block.sandStone, 1, 2));
			case SANDSTONE_CHISELED:
				return Item.itemsList[Block.sandStone.blockID].getItemDisplayName(new ItemStack(Block.sandStone, 1, 1));
			case BRICK_NETHER:
				return Item.itemsList[Block.netherBrick.blockID].getItemDisplayName(new ItemStack(Block.netherBrick));
			default:
				return Item.itemsList[Block.stoneBrick.blockID].getItemDisplayName(new ItemStack(Block.stoneBrick, 1, 0));
			}
		}
		
		public ItemStack getCraftingIngredient() {
			switch(this) {
			case BRICK_MOSSY:
				return new ItemStack(Block.stoneBrick, 1, 1);
			case BRICK_CRACKED:
				return new ItemStack(Block.stoneBrick, 1, 2);
			case BRICK_CHISELED:
				return new ItemStack(Block.stoneBrick, 1, 3);
			case BRICK:
				return new ItemStack(Block.brick, 1);
			case SANDSTONE_SMOOTH:
				return new ItemStack(Block.sandStone, 1, 2);
			case SANDSTONE_CHISELED:
				return new ItemStack(Block.sandStone, 1, 1);
			case BRICK_NETHER:
				return new ItemStack(Block.netherBrick, 1);
			default:
				return new ItemStack(Block.stoneBrick, 1, 0);	
			}
		}
		
		public static EnumFarmBlock getFromCompound(NBTTagCompound compound) {
			
			if(compound != null) {
				int farmBlockOrdinal = compound.getInteger("FarmBlock");
				if(farmBlockOrdinal < EnumFarmBlock.values().length)
					return EnumFarmBlock.values()[farmBlockOrdinal];
			}

			return EnumFarmBlock.BRICK_STONE;
		}
	}
	
	public TileFarm() {
		this.structureLogic = Farmables.farmInterface.createFarmStructureLogic(this);
	}
	
	/* GUI */
	@Override
	public void openGui(EntityPlayer player) {
		if (this.isMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		} else if (this.hasMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, masterX, masterY, masterZ);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		
		this.isMaster = nbttagcompound.getBoolean("IsMaster");
		this.masterX = nbttagcompound.getInteger("MasterX");
		this.masterY = nbttagcompound.getInteger("MasterY");
		this.masterZ = nbttagcompound.getInteger("MasterZ");
		
		farmBlock = EnumFarmBlock.getFromCompound(nbttagcompound);
		
		// Init for master state
		if(isMaster)
			makeMaster();
		
		if(inventory != null)
			inventory.readFromNBT(nbttagcompound);
		
		structureLogic.readFromNBT(nbttagcompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
		nbttagcompound.setBoolean("IsMaster", isMaster);
		nbttagcompound.setInteger("MasterX", masterX);
		nbttagcompound.setInteger("MasterY", masterY);
		nbttagcompound.setInteger("MasterZ", masterZ);
		
		farmBlock.saveToCompound(nbttagcompound);
		
		if(inventory != null)
			inventory.writeToNBT(nbttagcompound);
		
		structureLogic.writeToNBT(nbttagcompound);
	}
	
	/* UPDATING */
	@Override
	public void initialize() {
	}

	@Override
	public void updateEntity() {
		
		if(!Proxies.common.isSimulating(worldObj)) {
			updateClientSide();
			
		} else {
			
			if (!isInited) {
				initialize();
				isInited = true;
			}

			// Periodic validation if needed
			if(worldObj.getWorldTime() % 200 == 0 && (!isIntegratedIntoStructure() || isMaster()))
				validateStructure();
			
			updateServerSide();
		}
	}
		
	protected void updateServerSide() {		
	}
	
	protected void updateClientSide() {
	}

	/* CONSTRUCTION MATERIAL */
	EnumFarmBlock farmBlock = EnumFarmBlock.BRICK_STONE;
	int textureShift = 0;

	public void setFarmBlock(EnumFarmBlock block) {
		farmBlock = block;
		sendNetworkUpdate();
	}
	
	public EnumFarmBlock getFarmBlock() {
		return farmBlock;
	}
	
	public int getBlockTexture(int side, int meta) {
		if(meta == 1)
			return 48 + farmBlock.getColumn();
		
		int sideShift = 0;
		if(meta == 0 && side == 2)
			sideShift = 16;
		else if(meta == 0 && (side == 0 || side == 1))
			sideShift = 32;
		
		return textureShift + sideShift + farmBlock.getColumn();
	}
	
	/* INVENTORY MANAGMENT */
	protected TileInventoryAdapter inventory;
		
	protected abstract void createInventory();
	
	/* TILEFORESTRY */
	@Override public Gadget getMachine() { return null; }
	
	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(0,1);
		payload.shortPayload[0] = (short)farmBlock.ordinal();
		return payload;
	}
	@Override
	public void fromPacketPayload(PacketPayload payload) {
		this.farmBlock = EnumFarmBlock.values()[payload.shortPayload[0]];
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);

	}
	
	/* ITILESTRUCTURE */
	IStructureLogic structureLogic;
	
	private boolean isMaster;
	private int masterX, masterZ;
	private int masterY = -99;
	
	@Override public String getTypeUID() { return structureLogic.getTypeUID(); }
	@Override public void validateStructure() { structureLogic.validateStructure(); }

	@Override
	public void makeMaster() {
		setCentralTE(null);
		this.isMaster = true;
		
		if(inventory == null)
			createInventory();
	}

	@Override
	public void onStructureReset() {
		setCentralTE(null);
		if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1)
			worldObj.setBlockMetadata(xCoord, yCoord, zCoord, 0);
		isMaster = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public ITileStructure getCentralTE() {

		if(!isIntegratedIntoStructure())
			return null;
		
		if(!isMaster) {
			TileEntity tile = worldObj.getBlockTileEntity(masterX, masterY, masterZ);
			if(tile instanceof ITileStructure) {
				ITileStructure master = (ITileStructure) worldObj.getBlockTileEntity(masterX, masterY, masterZ);
				if(master.isMaster())
					return master;
				else
					return null;
			} else
				return null;
		} else
			return this;

	}

	private boolean isSameTile(TileEntity tile) {
		return tile.xCoord == xCoord && tile.yCoord == yCoord
				&& tile.zCoord == zCoord;
	}
	
	@Override
	public void setCentralTE(TileEntity tile) {
		if(tile == null || tile == this || isSameTile(tile)) {
			this.masterX = this.masterZ = 0;
			this.masterY = -99;
			return;
		}
		
		this.isMaster = false;
		this.masterX = tile.xCoord;
		this.masterY = tile.yCoord;
		this.masterZ = tile.zCoord;
		
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	protected boolean hasMaster() {
		return this.masterY >= 0;
	}
	
	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public boolean isIntegratedIntoStructure() {
		return isMaster || this.masterY >= 0;
	}

	@Override public void registerListener(IFarmListener listener) {}
	@Override public void removeListener(IFarmListener listener) {}

	@Override public String getInvName() { return "Farm"; }

	/* INTERACTION */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return Utils.isUseableByPlayer(player, this, worldObj, xCoord, yCoord, zCoord);
	}

}
