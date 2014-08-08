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
package forestry.core.gadgets;

public class TileEngine extends TileMachine {

	protected MachinePackage getPackage(int meta) {
		return null;
	}

	/*
	public Engine engine;
	public boolean isActive = false; // Used for smp.
	public int stagePiston = 0; // Indicates whether the piston is receding from
								// or approaching the combustion chamber
	public float pistonSpeedServer = 0; // Piston speed as supplied by the
										// server

	private IPowerProvider powerProvider;

	public TileEngine() {
		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(0, 10, 200, 10, 100000);
	}

	@Override
	public void initialize() {
		if (!Proxies.common.isSimulating(worldObj))
			return;

		if (engine == null) {
			createEngine();
		}
	}

	private void createEngine() {
		if (engine != null)
			return;

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (!GadgetManager.hasPlanterPackage(meta)) {
			Proxies.log.info("Encountered an engine with meta " + meta + ". However no such EnginePackage exists. Reverting to 0.");
			meta = 0;
		}

		this.pack = GadgetManager.getEnginePackage(meta);
		EngineFactory factory = ((EnginePackage) pack).factory;

		if (factory != null) {
			engine = factory.createEngine(this);
		} else
			throw new RuntimeException("Missing EngineFactory for meta " + meta);

		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, meta);
	}

	@Override
	public void updateEntity() {
		// Client connected to a server only update its piston
		if (!Proxies.common.isSimulating(worldObj)) {
			if (stagePiston != 0) {
				engine.progress += pistonSpeedServer;

				if (engine.progress > 1) {
					stagePiston = 0;
					engine.progress = 0;
				}
			} else if (this.isActive) {
				stagePiston = 1;
			}
			return;
		}

		super.updateEntity();

		engine.updateServerSide();

		// Determine targeted tile
		Position posTarget = new Position(xCoord, yCoord, zCoord, this.getOrientation());
		posTarget.moveForwards(1.0);
		TileEntity tile = worldObj.getBlockTileEntity((int) posTarget.x, (int) posTarget.y, (int) posTarget.z);

		float newPistonSpeed = engine.getPistonSpeed();
		if (newPistonSpeed != pistonSpeedServer) {
			pistonSpeedServer = newPistonSpeed;
			sendNetworkUpdate();
		}

		if (stagePiston != 0) {

			engine.progress += pistonSpeedServer;

			if (engine.progress > 0.5 && stagePiston == 1) {
				stagePiston = 2;

				if (isPoweredTile(tile)) {
					IPowerReceptor receptor = (IPowerReceptor) tile;
					int extractedEnergy = engine.extractEnergy(receptor.getPowerProvider().getMinEnergyReceived(), receptor.getPowerProvider()
							.getMaxEnergyReceived(), true);
					if (extractedEnergy > 0) {
						PluginBuildCraft.instance.invokeReceiveEnergyMethod(receptor.getPowerProvider(), extractedEnergy);
						// receptor.getPowerProvider().receiveEnergy(extractedEnergy);
					}
				}

			} else if (engine.progress >= 1) {
				engine.progress = 0;
				stagePiston = 0;
			}

		} else if (canPowerTo(tile)) { // If we are not already running, check if
			IPowerReceptor receptor = (IPowerReceptor) tile;
			if (engine.extractEnergy(receptor.getPowerProvider().getMinEnergyReceived(), receptor.getPowerProvider().getMaxEnergyReceived(), false) > 0) {
				stagePiston = 1; // If we can transfer energy, start running
				setActive(true);
			} else {
				setActive(false);
			}
		} else {
			setActive(false);
		}

		engine.dissipateHeat();
		engine.generateHeat();
		// Now let's fire up the engine:
		if (engine.mayBurn()) {
			engine.burn();
		} else {
			engine.extractEnergy(0, 2, true);
		}
	}

	private void setActive(boolean isActive) {
		if (this.isActive == isActive)
			return;

		this.isActive = isActive;
		sendNetworkUpdate();
	}

	private boolean canPowerTo(TileEntity tile) {
		return isActivated() && isPoweredTile(tile);
	}

	public boolean isBurning() {
		return engine.isBurning();
	}

	public int getBurnTimeRemainingScaled(int i) {
		return engine.getBurnTimeRemainingScaled(i);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		PowerFramework.currentFramework.loadPowerProvider(this, nbttagcompound);

		int kind = nbttagcompound.getInteger("Kind");
		pack = GadgetManager.getEnginePackage(kind);
		engine = ((EnginePackage) pack).factory.createEngine(this);
		engine.readFromNBT(nbttagcompound);

		stagePiston = nbttagcompound.getInteger("stagePiston");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		PowerFramework.currentFramework.savePowerProvider(this, nbttagcompound);
		int kind = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (GadgetManager.hasEnginePackage(kind)) {
			nbttagcompound.setInteger("Kind", kind);
		} else {
			nbttagcompound.setInteger("Kind", 0);
		}
		engine.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("stagePiston", stagePiston);
	}

	public boolean isPoweredTile(TileEntity tile) {
		if (tile == null)
			return false;

		if (!(tile instanceof IPowerReceptor))
			return false;

		IPowerReceptor receptor = (IPowerReceptor) tile;
		return receptor.getPowerProvider() != null;
	}

	public void rotateEngine() {
		if (engine == null) {
			initialize();
			isInited = true;
		}

		for (int i = getOrientation().ordinal() + 1; i <= getOrientation().ordinal() + 6; ++i) {
			ForgeDirection orient = ForgeDirection.values()[i % 6];

			Position pos = new Position(xCoord, yCoord, zCoord, orient);
			pos.moveForwards(1.0F);

			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (isPoweredTile(tile)) {
				if (engine != null) {
					setOrientation(orient);
				}
				worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);

				break;
			}
		}
	}

	// / MACHINERY
	public Gadget getMachine() {
		return this.engine;
	}

	public Engine getEngine() {
		return this.engine;
	}

	// / ERROR HANDLING
	@Override
	public boolean throwsErrors() {
		return true;
	}

	// / IHINTSOURCE
	@Override
	public boolean hasHints() {
		if (engine != null)
			return engine.hasHints();
		else
			return false;
	}

	@Override
	public String[] getHints() {
		if (engine != null)
			return engine.getHints();
		else
			return null;
	}

	// / IMPLEMENTATION OF IPOWERPROVIDER
	@Override
	public void setPowerProvider(IPowerProvider provider) {
		powerProvider = provider;
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public void doWork() {
		if (!Proxies.common.isSimulating(worldObj))
			return;

		engine.addEnergy((int) (PluginBuildCraft.instance.invokeUseEnergyMethod(powerProvider, 1, engine.maxEnergyReceived(), true) * 0.95F));
	}

	@Override
	public int powerRequest() {
		return 0;
	}

	// / IMPLEMENTATION OF IINVENTORY
	@Override
	public int getSizeInventory() {
		if (engine != null)
			return engine.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (engine != null)
			return engine.getStackInSlot(i);
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (engine != null)
			return engine.decrStackSize(i, j);
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (engine != null) {
			engine.setInventorySlotContents(i, itemstack);
		}
	}

	@Override
	public String getInvName() {
		if (engine != null)
			return engine.getInvName();
		else
			return "";
	}

	@Override
	public int getInventoryStackLimit() {
		if (engine != null)
			return engine.getInventoryStackLimit();
		else
			return 64;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (engine != null)
			return engine.getStackInSlotOnClosing(slot);
		else
			return null;
	}

	// / ISIDEDINVENTORY IMPLEMENTATION
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if (engine == null)
			return 0;
		else
			return engine.getStartInventorySide(side.ordinal());
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		if (engine == null)
			return 0;
		else
			return engine.getSizeInventorySide(side.ordinal());
	}

	// / ISPECIALINVENTORY IMPLEMENTATION
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (getAccess() == EnumAccess.PRIVATE)
			return 0;

		if (from == getOrientation())
			return 0;
		else if (engine != null)
			return engine.addItem(stack, doAdd, from);
		else
			return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		if (getAccess() == EnumAccess.PRIVATE)
			return new ItemStack[0];

		if (from == getOrientation())
			return new ItemStack[0];
		else if (engine != null)
			return engine.extractItem(doRemove, from, maxItemCount);
		else
			return new ItemStack[0];
	}

	// INETWORKEDTILE IMPLEMENTATION
	@Override
	public PacketPayload getPacketPayload() {

		PacketPayload payload = new PacketPayload(1, 1, 0);
		if (this.isActive) {
			payload.intPayload[0] = 1;
		} else {
			payload.intPayload[0] = 0;
		}
		payload.floatPayload[0] = pistonSpeedServer;

		if (engine != null) {
			payload.append(engine.getPacketPayload());
		}

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		if (payload.intPayload[0] > 0) {
			this.isActive = true;
		} else {
			this.isActive = false;
		}
		pistonSpeedServer = payload.floatPayload[0];

		if (engine == null) {
			createEngine();
		}

		engine.fromPacketPayload(payload, new IndexInPayload(1, 1, 0));
	}

	// / IENERGYSINK IMPLEMENTATION
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction) {
		if (engine != null)
			return engine.acceptsEnergyFrom(emitter, direction);
		else
			return false;
	}

	@Override
	public boolean isAddedToEnergyNet() {
		if (engine != null)
			return engine.isAddedToEnergyNet();
		else
			return false;
	}

	@Override
	public int demandsEnergy() {
		if (engine != null)
			return engine.demandsEnergy();
		else
			return 0;
	}

	@Override
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount) {
		if (engine != null)
			return engine.injectEnergy(directionFrom, amount);
		else
			return 0;
	}

	// ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getCustomTriggers() {
		if (engine != null)
			return engine.getCustomTriggers();
		else
			return null;
	}

	// / ISOCKETABLE
	@Override
	public int getSocketCount() {
		if (engine != null)
			return engine.getSocketCount();
		else
			return 0;
	}

	@Override
	public ItemStack getSocket(int slot) {
		if (engine != null)
			return engine.getSocket(slot);
		else
			return null;
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {
		if (engine != null) {
			engine.setSocket(slot, stack);
		}
	}

	// / ITANKCONTAINER
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		if (engine == null)
			return 0;

		if (from == getOrientation())
			return 0;

		return engine.fill(from, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		if (engine == null)
			return 0;

		return engine.fill(tankIndex, resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (engine == null)
			return null;

		if (from == getOrientation())
			return null;

		return engine.drain(from, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		if (engine == null)
			return null;
		return engine.drain(tankIndex, maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		if (engine != null)
			return engine.getTanks(direction);
		else
			return new LiquidTank[0];
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		if (engine != null)
			return engine.getTank(direction, type);
		else
			return null;
	}
	*/
}
