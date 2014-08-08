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
package forestry.arboriculture.gadgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.EnumPlantType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.genetics.Allele;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginForestryArboriculture;

public class TileLeaves extends TileTreeContainer implements IPollinatable, IFruitBearer {

	private int colourLeaves;
	private int colourFruits;
	private int textureIndexFancy = 48;
	private int textureIndexPlain = 64;
	private int textureIndexFruits = -1;
	
	private boolean isFruitLeaf;
	private int ripeningTime;
	
	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		isFruitLeaf = nbttagcompound.getBoolean("FL");
		ripeningTime = nbttagcompound.getInteger("RT");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("FL", isFruitLeaf);
		nbttagcompound.setInteger("RT", ripeningTime);
	}

	@Override
	public void onBlockTick() {
		if(!hasFruit())
			return;
		if(ripeningTime >= Short.MAX_VALUE - 1)
			return;
		
		if(worldObj.rand.nextFloat() < getTree().getGenome().getSappiness()) {
			ripeningTime++;
			sendNetworkUpdateRipening();
		}
	}
	
	@Override
	public void setTree(ITree tree) {
		if(tree.canBearFruit())
			isFruitLeaf = tree.getGenome().getFruitProvider().markAsFruitLeaf(tree.getGenome(), worldObj, xCoord, yCoord, zCoord);
		super.setTree(tree);
	}
	
	public int getFoliageColour() {
		return colourLeaves;
	}
	
	public int getFruitColour() {
		return colourFruits;
	}
	
	public int getTextureIndex(boolean fancy) {
		if(fancy)
			return textureIndexFancy;
		else
			return textureIndexPlain;
	}
	
	public int getTextureFruits() {
		return textureIndexFruits;
	}
	
	public int getRipeningTime() {
		return ripeningTime;
	}
	
	/* IPOLLINATABLE */
	@Override
	public EnumSet<EnumPlantType> getPlantType() {
		if(getTree() == null)
			return EnumSet.noneOf(EnumPlantType.class);
		
		return getTree().getPlantTypes();
	}
	
	@Override
	public boolean canMateWith(IIndividual individual) {
		if(getTree() == null)
			return false;
		if(getTree().getMate() != null)
			return false;

		return !getTree().getGenome().isGeneticEqual(individual.getGenome());
	}

	@Override
	public void mateWith(IIndividual individual) {
		if(getTree() == null)
			return;
		
		getTree().mate((ITree)individual);
	}

	@Override
	public IIndividual getPollen() {
		return getTree();
	}

	private int determineFoliageColour() {

		if (getTree() == null)
			return PluginForestryArboriculture.proxy.getFoliageColorBasic();

		int colour = getTree().getGenome().getPrimaryAsTree().getPrimaryColor();
			
		if(getTree().getMate() != null)
			return getTree().getGenome().getPrimaryAsTree().getSecondaryColor();
			
		return colour;

	}
	
	private int determineFruitColour() {
		if(getTree() == null)
			return 0xffffff;
		
		IFruitProvider fruit = getTree().getGenome().getFruitProvider();
		return fruit.getColour(getTree().getGenome(), worldObj, xCoord, yCoord, zCoord, getRipeningTime());
	}
	
	private int determineTextureIndex(boolean fancy) {
		if (getTree() != null)
			return getTree().getGenome().getPrimaryAsTree().getLeafTextureIndex(getTree(), fancy);

		return 48;
	}
	
	private int determineOverlayIndex() {
		if(getTree() == null)
			return -1;
		if(!hasFruit())
			return -1;
		
		IFruitProvider fruit = getTree().getGenome().getFruitProvider();
		
		// Hardcoded because vanilla oak trees don't show fruits.
		if(getTree().getGenome().getPrimary() == Allele.treeOak
				&& fruit == ((IAlleleFruit)Allele.fruitApple).getProvider()) {
			return -1;
		} else {
			return fruit.getTextureIndex(getTree().getGenome(), worldObj, xCoord, yCoord, zCoord, getRipeningTime(), true);
		}

	}
	
	/* NETWORK */
	@Override
	public Packet getDescriptionPacket() {
		return toPacket().getPacket();
	}

	@Override
	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(toPacket(), xCoord, yCoord, zCoord);
	}

	private void sendNetworkUpdateRipening() {
		Proxies.net.sendNetworkPacket(new PacketUpdate(PacketIds.TILE_UPDATE, xCoord, yCoord, zCoord, determineFruitColour()), xCoord, yCoord, zCoord);
	}
	
	private ForestryPacket toPacket() {
		
		PacketPayload payload = new PacketPayload(2, 5);
		
		payload.shortPayload[0] = (short)ripeningTime;
		if(isFruitLeaf)
			payload.shortPayload[1] = 1;
		
		if(getTree() != null) {
			payload.intPayload[0] = determineFruitColour();
			payload.intPayload[1] = determineFoliageColour();
			payload.shortPayload[2] = (short)determineTextureIndex(true);
			payload.shortPayload[3] = (short)determineTextureIndex(false);
			payload.shortPayload[4] = (short)determineOverlayIndex();
		}
		
		return new PacketUpdate(PacketIds.TILE_UPDATE, xCoord, yCoord, zCoord, payload);		
	}
	
	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		
		PacketUpdate packet = (PacketUpdate) packetRaw;
		if(packet.payload.shortPayload.length > 0) {
			
			ripeningTime = packet.payload.shortPayload[0];
			if(packet.payload.intPayload[1] > 0)
				isFruitLeaf = true;
			else
				isFruitLeaf = false;
			
			textureIndexFancy = packet.payload.shortPayload[2];
			textureIndexPlain = packet.payload.shortPayload[3];
			textureIndexFruits = packet.payload.shortPayload[4];	
		}
		
		if(packet.payload.intPayload.length > 0) {
			colourFruits = packet.payload.intPayload[0];			
		}
		if(packet.payload.intPayload.length > 1) {
			colourLeaves = packet.payload.intPayload[1];
		}
		
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	/* IFRUITBEARER */
	@Override
	public Collection<ItemStack> pickFruit(ItemStack tool) {
		if(!hasFruit())
			return new ArrayList<ItemStack>();
		if(getTree() == null)
			return new ArrayList<ItemStack>();

		ArrayList<ItemStack> picked = new ArrayList<ItemStack>(Arrays.asList(getTree().produceStacks(worldObj, xCoord, yCoord, zCoord, getRipeningTime())));
		ripeningTime = 0;
		sendNetworkUpdateRipening();
		return picked;
	}

	@Override
	public IFruitFamily getFruitFamily() {
		if(getTree() == null)
			return null;
		return getTree().getGenome().getFruitProvider().getFamily();
	}

	@Override
	public float getRipeness() {
		if(getTree() == null)
			return 0f;
		int ripeningPeriod = getTree().getGenome().getFruitProvider().getRipeningPeriod();
		if(ripeningPeriod == 0)
			return 1.0f;
		return (float)ripeningTime / ripeningPeriod;
	}

	@Override
	public boolean hasFruit() {
		return isFruitLeaf;
	}

	@Override
	public void addRipeness(float add) {
		if(getTree() == null)
			return;
		ripeningTime += getTree().getGenome().getFruitProvider().getRipeningPeriod()*add;
		sendNetworkUpdateRipening();
	}

}
