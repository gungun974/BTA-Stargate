package gungun974.stargate.gate.components;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.StargateBlocks;
import gungun974.stargate.core.*;
import gungun974.stargate.gate.blocks.BlockLogicStargate;
import gungun974.stargate.gate.renders.StargateEventHorizon;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import gungun974.stargate.network.server.PlayerEnterStargateMessage;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.*;

public abstract class StargateComponent {
	public final static double symbolAngle = 360.0 / StargateMilkyWayAddress.NUMBER_OF_SYMBOL;
	public final TileEntity stargateTile;
	protected final Queue<Runnable> commandQueue = new ArrayDeque<>();
	public StargateEventHorizon eventHorizon = new StargateEventHorizon();
	protected StargateState state = StargateState.IDLE;
	protected StargateState lastState = StargateState.IDLE;
	protected double targetAngle = 0;
	protected double currentAngle = 0;
	protected short currentDialingAddressSize = 0;
	protected int[] currentDialingAddress = new int[9];
	protected boolean ringDirection = false;
	protected boolean lastRingMove = false;
	protected boolean ringMove = false;
	protected boolean ringMoveForEver = false;
	protected double lastAngle = 0;
	int longTick = 0;
	private boolean lastEventHorizonNoise = false;
	private Direction direction = Direction.NORTH;
	private Direction orientation = Direction.NORTH;
	private StargateAnimation lastAnimation = StargateAnimation.NONE;
	private StargateAnimation animation = StargateAnimation.NONE;
	private int animationTick = 0;
	private int lastAnimationTick = 0;
	private int eventHorizonTick = 0;
	private int lastEventHorizonTick = 0;
	private boolean wasRecieverGate = false;

	protected StargateComponent(TileEntity stargateTile) {
		this.stargateTile = stargateTile;
	}

	public static double angularDistance(double angle1, double angle2) {
		double diff = Math.abs(angle1 - angle2) % 360.0;
		return diff > 180.0 ? 360.0 - diff : diff;
	}

	public short getCurrentDialingAddressSize() {
		return currentDialingAddressSize;
	}

	protected double computeTargetAngle(int symbol) {
		return Math.floorMod(symbol, StargateMilkyWayAddress.NUMBER_OF_SYMBOL) * symbolAngle;
	}

	public double getCurrentAngle() {
		return (((currentAngle % 360) + 360) % 360);
	}

	public int[] getCurrentDialingAddress() {
		switch (getState()) {
			case IDLE:
			case DIALLING:
			case AWAIT: {
				return Arrays.copyOfRange(
					currentDialingAddress,
					0,
					currentDialingAddressSize
				);
			}
			case OPENING:
			case CONNECTED:
			case CLOSING:
		}

		int[] result = Arrays.copyOfRange(
			currentDialingAddress,
			0,
			Math.max(currentDialingAddressSize - 1, 0)
		);
		return result.length >= 9 ? result : Arrays.copyOf(result, result.length + 1);
	}

	public void checkIfStillValid() {
		if (orientation == Direction.NORTH) {
			verticalCheckIfStillValid();
			return;
		}
		horizontalCheckIfStillValid();
	}

	public void destroyGate() {
		if (orientation == Direction.NORTH) {
			destroyVertical();
			return;
		}
		destroyHorizontal();
		invalidate();
	}

	private int getIdForStargateBlock() {
		switch (getFamily()) {
			case MilkyWay:
				return StargateBlocks.STARGATE_MILKYWAY.id();
			case Pegasus:
				return StargateBlocks.STARGATE_PEGASUS.id();
			case Universe:
				return StargateBlocks.STARGATE_UNIVERSE.id();
		}

		return StargateBlocks.STARGATE_MILKYWAY.id();
	}

	private void verticalCheckIfStillValid() {
		if (stargateTile.worldObj == null) {
			return;
		}

		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		boolean invertOrder = (direction == Direction.WEST || direction == Direction.NORTH);

		int stargateBlockId = getIdForStargateBlock();

		boolean isValidStructure = true;

		for (int idx = 0; idx < ringOrder.length; idx++) {
			int i = ringOrder[idx][0];
			int j = ringOrder[idx][1];

			int meta;
			if (invertOrder && idx > 0) {
				meta = ringOrder.length - idx;
			} else {
				meta = idx;
			}

			int px = stargateTile.x + direction.getOffsetZ() * i;
			int py = stargateTile.y + j;
			int pz = stargateTile.z + direction.getOffsetX() * i;

			if (stargateTile.worldObj.getBlockId(px, py, pz) != stargateBlockId) {
				isValidStructure = false;
				break;
			}

			int rawMetadata = stargateTile.worldObj.getBlockMetadata(px, py, pz);

			int ringMetadata = rawMetadata & 0b1111;
			int directionMetadata = rawMetadata & 0b110000;

			if (ringMetadata != meta) {
				isValidStructure = false;
				break;
			}

			if (direction == Direction.EAST || direction == Direction.WEST) {
				if (directionMetadata != 0b010000) {
					isValidStructure = false;
					break;
				}
			} else {
				if (directionMetadata != 0b000000) {
					isValidStructure = false;
					break;
				}
			}
		}

		if (isValidStructure) {
			return;
		}

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = stargateTile.x + direction.getOffsetZ() * i;
			int py = stargateTile.y + j;
			int pz = stargateTile.z + direction.getOffsetX() * i;

			BlockLogicStargate blockLogicStargate = stargateTile.worldObj.getBlockLogic(px, py, pz, BlockLogicStargate.class);

			if (blockLogicStargate == null) {
				continue;
			}

			blockLogicStargate.restoreOriginalBlock(stargateTile.worldObj, px, py, pz);
		}
	}

	private void destroyVertical() {
		if (stargateTile.worldObj == null) {
			return;
		}

		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		boolean invertOrder = (direction == Direction.WEST || direction == Direction.NORTH);

		int stargateBlockId = getIdForStargateBlock();

		for (int idx = 0; idx < ringOrder.length; idx++) {
			int i = ringOrder[idx][0];
			int j = ringOrder[idx][1];

			int meta;
			if (invertOrder && idx > 0) {
				meta = ringOrder.length - idx;
			} else {
				meta = idx;
			}

			int px = stargateTile.x + direction.getOffsetZ() * i;
			int py = stargateTile.y + j;
			int pz = stargateTile.z + direction.getOffsetX() * i;

			if (stargateTile.worldObj.getBlockId(px, py, pz) != stargateBlockId) {
				continue;
			}

			int rawMetadata = stargateTile.worldObj.getBlockMetadata(px, py, pz);

			int ringMetadata = rawMetadata & 0b1111;
			int directionMetadata = rawMetadata & 0b110000;

			if (ringMetadata != meta) {
				continue;
			}

			if (direction == Direction.EAST || direction == Direction.WEST) {
				if (directionMetadata != 0b010000) {
					continue;
				}
			} else {
				if (directionMetadata != 0b000000) {
					continue;
				}
			}

			stargateTile.worldObj.setBlockRaw(px, py, pz, 0);
		}

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = stargateTile.x + direction.getOffsetZ() * i;
			int py = stargateTile.y + j;
			int pz = stargateTile.z + direction.getOffsetX() * i;

			stargateTile.worldObj.notifyBlockChange(px, py, pz, 0);

			TileEntity tileEntity = stargateTile.worldObj.getTileEntity(px, py, pz);

			if (tileEntity instanceof TileEntityStargate) {
				((TileEntityStargate) tileEntity).destroyed();

				stargateTile.worldObj.removeBlockTileEntity(px, py, pz);
			}
		}
	}

	private void horizontalCheckIfStillValid() {
		if (stargateTile.worldObj == null) {
			return;
		}

		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		int stargateBlockId = getIdForStargateBlock();

		boolean isValidStructure = true;

		Direction dira = direction.getOpposite();

		for (int idx = 0; idx < ringOrder.length; idx++) {
			int i = ringOrder[idx][0];
			int j = ringOrder[idx][1];

			int meta;
			if (dira == Direction.WEST) {
				meta = ringOrder.length - idx - 12;
			} else if (dira == Direction.NORTH) {
				meta = idx - 8;
			} else if (dira == Direction.EAST) {
				meta = ringOrder.length - idx - 4;
			} else {
				meta = idx;
			}

			meta = Math.floorMod(meta, 16);

			int px = stargateTile.x + dira.getOffsetZ() * i - dira.getOffsetX() * j;
			int pz = stargateTile.z + dira.getOffsetX() * i - dira.getOffsetZ() * j;

			if (stargateTile.worldObj.getBlockId(px, stargateTile.y, pz) != stargateBlockId) {
				isValidStructure = false;
				break;
			}

			int rawMetadata = stargateTile.worldObj.getBlockMetadata(px, stargateTile.y, pz);

			int ringMetadata = rawMetadata & 0b1111;
			int directionMetadata = rawMetadata & 0b110000;

			if (ringMetadata != meta) {
				isValidStructure = false;
				break;
			}

			if (directionMetadata != 0b100000) {
				isValidStructure = false;
				break;
			}
		}

		if (isValidStructure) {
			return;
		}

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = stargateTile.x + dira.getOffsetZ() * i - dira.getOffsetX() * j;
			int pz = stargateTile.z + dira.getOffsetX() * i - dira.getOffsetZ() * j;

			BlockLogicStargate blockLogicStargate = stargateTile.worldObj.getBlockLogic(px, stargateTile.y, pz, BlockLogicStargate.class);

			if (blockLogicStargate == null) {
				continue;
			}

			blockLogicStargate.restoreOriginalBlock(stargateTile.worldObj, px, stargateTile.y, pz);
		}
	}

	private void destroyHorizontal() {
		if (stargateTile.worldObj == null) {
			return;
		}

		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		int stargateBlockId = getIdForStargateBlock();

		Direction dira = direction.getOpposite();

		for (int idx = 0; idx < ringOrder.length; idx++) {
			int i = ringOrder[idx][0];
			int j = ringOrder[idx][1];

			int meta;
			if (dira == Direction.WEST) {
				meta = ringOrder.length - idx - 12;
			} else if (dira == Direction.NORTH) {
				meta = idx - 8;
			} else if (dira == Direction.EAST) {
				meta = ringOrder.length - idx - 4;
			} else {
				meta = idx;
			}

			meta = Math.floorMod(meta, 16);

			int px = stargateTile.x + dira.getOffsetZ() * i - dira.getOffsetX() * j;
			int pz = stargateTile.z + dira.getOffsetX() * i - dira.getOffsetZ() * j;

			if (stargateTile.worldObj.getBlockId(px, stargateTile.y, pz) != stargateBlockId) {
				continue;
			}

			int rawMetadata = stargateTile.worldObj.getBlockMetadata(px, stargateTile.y, pz);

			int ringMetadata = rawMetadata & 0b1111;
			int directionMetadata = rawMetadata & 0b110000;

			if (ringMetadata != meta) {
				continue;
			}

			if (directionMetadata != 0b100000) {
				continue;
			}

			stargateTile.worldObj.setBlockRaw(px, stargateTile.y, pz, 0);
		}

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = stargateTile.x + dira.getOffsetZ() * i - dira.getOffsetX() * j;
			int pz = stargateTile.z + dira.getOffsetX() * i - dira.getOffsetZ() * j;

			stargateTile.worldObj.notifyBlockChange(px, stargateTile.y, pz, 0);

			TileEntity tileEntity = stargateTile.worldObj.getTileEntity(px, stargateTile.y, pz);

			if (tileEntity instanceof TileEntityStargate) {
				((TileEntityStargate) tileEntity).destroyed();

				stargateTile.worldObj.removeBlockTileEntity(px, stargateTile.y, pz);
			}
		}
	}

	public boolean isRingMove() {
		return ringMove;
	}

	public StargateState getState() {
		return state;
	}

	public void invalidate() {
		stopSoundAtCenter("stargate:stargate.milkyway.roll");
		stopSoundAtCenter("stargate:stargate.pegasus.roll");
		stopSoundAtCenter("stargate:stargate.universe.roll");
		stopSoundAtCenter("stargate:stargate.eventHorizon");

		if (this.stargateTile.worldObj != null && getIdForStargateBlock() != this.stargateTile.worldObj.getBlockId(this.stargateTile.x, this.stargateTile.y, this.stargateTile.z)) {
			StargateSessionManager.getInstance().removeSession(this);
		}
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void readFromNBT(CompoundTag compoundTag) {
		direction = Direction.values()[compoundTag.getIntegerOrDefault("Direction", Direction.NORTH.ordinal())];
		orientation = Direction.values()[compoundTag.getIntegerOrDefault("Orientation", Direction.NORTH.ordinal())];

		state = StargateState.values()[compoundTag.getIntegerOrDefault("State", StargateAnimation.NONE.ordinal())];
		lastState = state;
		currentDialingAddressSize = compoundTag.getShortOrDefault("DialingAddressSize", (short) 0);
		currentDialingAddress[0] = compoundTag.getIntegerOrDefault("DialingAddress0", 0);
		currentDialingAddress[1] = compoundTag.getIntegerOrDefault("DialingAddress1", 0);
		currentDialingAddress[2] = compoundTag.getIntegerOrDefault("DialingAddress2", 0);
		currentDialingAddress[3] = compoundTag.getIntegerOrDefault("DialingAddress3", 0);
		currentDialingAddress[4] = compoundTag.getIntegerOrDefault("DialingAddress4", 0);
		currentDialingAddress[5] = compoundTag.getIntegerOrDefault("DialingAddress5", 0);
		currentDialingAddress[6] = compoundTag.getIntegerOrDefault("DialingAddress6", 0);
		currentDialingAddress[7] = compoundTag.getIntegerOrDefault("DialingAddress7", 0);
		currentDialingAddress[8] = compoundTag.getIntegerOrDefault("DialingAddress8", 0);

		currentAngle = compoundTag.getDoubleOrDefault("CurrentAngle", 0);
		targetAngle = compoundTag.getDoubleOrDefault("TargetAngle", 0);
		ringDirection = compoundTag.getBooleanOrDefault("RingDirection", false);
		ringMove = compoundTag.getBooleanOrDefault("RingMove", false);

		animation = StargateAnimation.values()[compoundTag.getIntegerOrDefault("Animation", StargateAnimation.NONE.ordinal())];
		animationTick = compoundTag.getIntegerOrDefault("AnimationTick", 0);
	}

	public void writeToNBT(CompoundTag compoundTag) {
		compoundTag.putInt("Direction", direction.ordinal());
		compoundTag.putInt("Orientation", orientation.ordinal());

		compoundTag.putInt("State", state.ordinal());
		compoundTag.putShort("DialingAddressSize", currentDialingAddressSize);
		compoundTag.putInt("DialingAddress0", currentDialingAddress[0]);
		compoundTag.putInt("DialingAddress1", currentDialingAddress[1]);
		compoundTag.putInt("DialingAddress2", currentDialingAddress[2]);
		compoundTag.putInt("DialingAddress3", currentDialingAddress[3]);
		compoundTag.putInt("DialingAddress4", currentDialingAddress[4]);
		compoundTag.putInt("DialingAddress5", currentDialingAddress[5]);
		compoundTag.putInt("DialingAddress6", currentDialingAddress[6]);
		compoundTag.putInt("DialingAddress7", currentDialingAddress[7]);
		compoundTag.putInt("DialingAddress8", currentDialingAddress[8]);

		compoundTag.putDouble("CurrentAngle", currentAngle);
		compoundTag.putDouble("TargetAngle", targetAngle);
		compoundTag.putBoolean("RingDirection", ringDirection);
		compoundTag.putBoolean("RingMove", ringMove);

		compoundTag.putInt("Animation", animation.ordinal());
		compoundTag.putInt("AnimationTick", animationTick);
	}

	public double interpolatedRingAngle(double partialTicks) {
		return lastAngle + (currentAngle - lastAngle) * partialTicks;
	}

	public boolean interpolatedChevronActive(int chevron, double partialTicks) {
		boolean isLastChevronActive = !(state == StargateState.IDLE || state == StargateState.DIALLING);

		final int[] ORDER = {0, 1, 2, 3, 4, 5, 7, 8, 6};

		if (currentDialingAddressSize == 0) {
			return false;
		}

		int currentEncodedChevron = ORDER[currentDialingAddressSize - 1];

		if (isLastChevronActive) {
			currentEncodedChevron -= 1;
		}

		if (animation == StargateAnimation.ENCODE_CHEVRON && ((!isLastChevronActive && chevron == currentEncodedChevron) || chevron == 6)) {
			final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

			if (chevron == 6) {
				if (!isLastChevronActive && currentAnimationTick > 38.67) {
					return false;
				}
				return currentAnimationTick > 4.67;
			}

			return currentAnimationTick > 30.67;
		} else if (animation == StargateAnimation.FAST_ENCODE_CHEVRON && ((!isLastChevronActive && chevron == currentEncodedChevron && chevron != 6) || (isLastChevronActive && chevron == 6))) {
			double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

			if (getFamily() == StargateFamily.Pegasus) {
				currentAnimationTick += 4.0;
			}

			return currentAnimationTick > (double) StargateAnimation.FAST_ENCODE_CHEVRON.duration / 2;
		}

		if (currentDialingAddressSize == 9) {
			return true;
		}

		if (chevron == 6) {
			return isLastChevronActive;
		}

		return chevron <= currentEncodedChevron;

	}

	public int getChevronActiveSymbol(int chevron) {
		switch (chevron) {
			case 0:
				return currentDialingAddress[0];
			case 1:
				return currentDialingAddress[1];
			case 2:
				return currentDialingAddress[2];
			case 3:
				return currentDialingAddress[3];
			case 4:
				return currentDialingAddress[4];
			case 5:
				return currentDialingAddress[5];
			case 6:
				if (currentDialingAddressSize == 7) {
					return currentDialingAddress[6];
				}
				if (currentDialingAddressSize == 8) {
					return currentDialingAddress[7];
				}
				return currentDialingAddress[8];
			case 7:
				return currentDialingAddress[6];
			case 8:
				return currentDialingAddress[7];
		}

		return -1;
	}

	public double interpolatedChevronDistance(int chevron, double partialTicks) {
		if (animation != StargateAnimation.ENCODE_CHEVRON) {
			return 0;
		}

		if (chevron != 6) {
			return 0;
		}

		final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

		if (currentAnimationTick > 26.67) {
			return (1 - Math.min((currentAnimationTick - 26.67) / 6.66, 1)) * -0.025;
		}

		if (currentAnimationTick > 12.67) {
			return Math.min(((currentAnimationTick - 12.67) / 6.66), 1) * -0.025;
		}

		return 0;
	}

	public double interpolatedUnstableVortexDistance(double partialTicks) {
		if (animation != StargateAnimation.KAWOOSH) {
			return 0;
		}

		final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks - 18;

		if (currentAnimationTick < 0) {
			return 0;
		}

		final double animationProgress = Math.min((currentAnimationTick) / 34.5, 1);

		double ratio = 0.60;

		if (animationProgress < ratio) {
			return Math.sin((Math.PI / 2) * (animationProgress / ratio));
		} else {
			double x = (animationProgress - ratio) / (1 - ratio);
			return Math.cos((Math.PI / 2) * x);
		}
	}

	public double interpolatedAnimationTick(double partialTicks) {
		return lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;
	}

	public double interpolatedEventHorizonTick(double partialTicks) {
		return lastEventHorizonTick + (eventHorizonTick - lastEventHorizonTick) * partialTicks;
	}

	public double interpolatedUnstableVortexDiameter(double partialTicks) {
		if (animation != StargateAnimation.KAWOOSH) {
			return 1;
		}

		final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks - 12;

		if (currentAnimationTick < 6) {
			return 0;
		}

		if (currentAnimationTick < 24) {
			final double openAnimationProgress = Math.min((currentAnimationTick - 6) / 18, 1);

			return 0.5 + openAnimationProgress * 0.5;
		}

		if (currentAnimationTick < 48) {
			final double middleAnimationProgress = Math.min((currentAnimationTick - 24) / 24, 1);

			return 1 - middleAnimationProgress * 0.5;
		}

		final double closeAnimationProgress = Math.min((currentAnimationTick - 36) / (StargateAnimation.KAWOOSH.duration - 36), 1);

		return 0.5 + closeAnimationProgress * 0.5;
	}

	public float interpolatedEventHorizonExposure(double partialTicks) {
		if (animation == StargateAnimation.KAWOOSH) {
			final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks - 6;

			double ratio = 1 - currentAnimationTick / 18;
			float step = 0.05f;
			return (float) (Math.floor(ratio / step) * step);
		}

		if (animation != StargateAnimation.CLOSING) {
			return 0;
		}

		final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks - 10;

		return (float) currentAnimationTick / (StargateAnimation.CLOSING.duration - 10);
	}

	public float interpolatedEventHorizonFormationProgress(double partialTicks) {
		if (animation == StargateAnimation.KAWOOSH) {
			final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks - 6;

			double ratio = currentAnimationTick / 12;
			float step = 0.05f;
			return (float) (Math.floor(ratio / step) * step);
		}


		if (animation != StargateAnimation.CLOSING) {
			return 1;
		}

		final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks - 38;

		double ratio = 1 - currentAnimationTick / 13;
		float step = 0.05f;
		return (float) (Math.floor(ratio / step) * step);

	}

	public boolean interpolatedShowEventHorizon() {
		if (animation != StargateAnimation.KAWOOSH && animation != StargateAnimation.CLOSING) {
			return state != StargateState.CLOSING;
		}

		return true;
	}

	public int getCurrentSymbol() {
		return Math.floorMod((int) Math.round((((currentAngle % 360) + 360) % 360) / symbolAngle), StargateMilkyWayAddress.NUMBER_OF_SYMBOL);
	}

	public AABB getDetectionBox() {
		Direction orientation = getOrientation();

		double offset = 3.0;

		Direction direction = getDirection();
		double x1 = stargateTile.x + direction.getOffsetZ() * -3 + direction.getOffsetX() * offset;
		double y1 = stargateTile.y;
		double z1 = stargateTile.z + direction.getOffsetX() * -3 + direction.getOffsetZ() * offset;
		double x2 = stargateTile.x + direction.getOffsetZ() * 3 - direction.getOffsetX() * offset;
		double y2 = stargateTile.y + 5;
		double z2 = stargateTile.z + direction.getOffsetX() * 3 - direction.getOffsetZ() * offset;

		if (orientation != Direction.NORTH) {
			x1 = stargateTile.x - direction.getOffsetZ() * 3;
			y1 = stargateTile.y - offset;
			z1 = stargateTile.z - direction.getOffsetX() * 3;

			x2 = stargateTile.x + direction.getOffsetX() * 6 + direction.getOffsetZ() * 3;
			y2 = stargateTile.y + offset;
			z2 = stargateTile.z + direction.getOffsetX() * 3 + direction.getOffsetZ() * 6;
		}

		double minX = Math.min(x1, x2);
		double minY = Math.min(y1, y2);
		double minZ = Math.min(z1, z2);
		double maxX = Math.max(x1, x2) + (double) 1.0F;
		double maxY = Math.max(y1, y2) + (double) 1.0F;
		double maxZ = Math.max(z1, z2) + (double) 1.0F;
		return AABB.getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public void kawooshDestroyInnerBlocks() {
		if (stargateTile.worldObj == null) {
			return;
		}

		Direction direction = getDirection();
		Direction orientation = getOrientation();

		double radius = 3;

		double centerX, centerY, centerZ;

		if (orientation == Direction.NORTH) {
			centerX = stargateTile.x + 0.5;
			centerY = stargateTile.y + 3.5;
			centerZ = stargateTile.z + 0.5;
		} else {
			centerX = stargateTile.x + direction.getOffsetZ() * 0.5 + direction.getOffsetX() * 3.5;
			centerY = stargateTile.y + orientation.getOffsetY() * 0.5;
			centerZ = stargateTile.z + direction.getOffsetX() * 0.5 + direction.getOffsetZ() * 3.5;

			if (direction.getOffsetX() < 0) {
				centerX += 1;
				centerZ += 1;
			}

			if (direction.getOffsetZ() < 0) {
				centerZ += 1;
				centerX += 1;
			}
		}

		double x1, y1, z1, x2, y2, z2;

		if (orientation == Direction.NORTH) {
			double x = radius * Math.abs(direction.getOffsetZ());
			double z = radius * Math.abs(direction.getOffsetX());

			x1 = centerX - x;
			y1 = centerY - radius;
			z1 = centerZ - z;

			x2 = centerX + x;
			y2 = centerY + radius;
			z2 = centerZ + z;
		} else {
			x1 = centerX - radius;
			y1 = centerY;
			z1 = centerZ - radius;

			x2 = centerX + radius;
			y2 = centerY;
			z2 = centerZ + radius;
		}

		double minX = Math.min(x1, x2);
		double minY = Math.min(y1, y2);
		double minZ = Math.min(z1, z2);
		double maxX = Math.max(x1, x2);
		double maxY = Math.max(y1, y2);
		double maxZ = Math.max(z1, z2);

		int startX = (int) Math.floor(minX);
		int startY = (int) Math.floor(minY);
		int startZ = (int) Math.floor(minZ);
		int endX = (int) Math.ceil(maxX) - 1;
		int endY = (int) Math.ceil(maxY) - 1;
		int endZ = (int) Math.ceil(maxZ) - 1;

		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				for (int z = startZ; z <= endZ; z++) {
					double distance = Math.sqrt((x + 0.5 - centerX) * (x + 0.5 - centerX) + (y + 0.5 - centerY) * (y + 0.5 - centerY) + (z + 0.5 - centerZ) * (z + 0.5 - centerZ));

					if (distance >= 2.5) {
						continue;
					}

					if (distance <= 3 * (1 - interpolatedEventHorizonFormationProgress(1))) {
						continue;
					}

					if (stargateTile.worldObj.getBlockLogic(x, y, z, BlockLogicStargate.class) != null) {
						continue;
					}

					if (stargateTile.worldObj.isAirBlock(x, y, z)) {
						continue;
					}

					AABB boundingBox = AABB.getTemporaryBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);

					Block<?> block = stargateTile.worldObj.getBlock(x, y, z);
					if (block != null) {
						stargateTile.worldObj.collidingBoundingBoxes.clear();
						block.getCollidingBoundingBoxes(stargateTile.worldObj, x, y, z, boundingBox, stargateTile.worldObj.collidingBoundingBoxes);

						boolean shouldBreak = false;

						for (AABB aabb : stargateTile.worldObj.collidingBoundingBoxes) {
							double closestX = Math.max(aabb.minX, Math.min(centerX, aabb.maxX));
							double closestY = Math.max(aabb.minY, Math.min(centerY, aabb.maxY));
							double closestZ = Math.max(aabb.minZ, Math.min(centerZ, aabb.maxZ));

							double dx = centerX - closestX;
							double dy = centerY - closestY;
							double dz = centerZ - closestZ;
							double distanceSquared = dx * dx + dy * dy + dz * dz;

							if (distanceSquared < 4) {
								shouldBreak = true;
								break;
							}
						}

						if (shouldBreak) {
							stargateTile.worldObj.setBlockWithNotify(x, y, z, 0);
						}
					}
				}
			}
		}
	}

	public AABB getKawooshDetectionBox() {
		Direction direction = getDirection();
		Direction orientation = getOrientation();

		double radius = interpolatedUnstableVortexDiameter(1) * 1.5;
		double depth = interpolatedUnstableVortexDistance(1) * 4;

		double centerX, centerY, centerZ;

		if (orientation == Direction.NORTH) {
			centerX = stargateTile.x + 0.5;
			centerY = stargateTile.y + 3.5;
			centerZ = stargateTile.z + 0.5;
		} else {
			centerX = stargateTile.x + direction.getOffsetZ() * 0.5 + direction.getOffsetX() * 3.5;
			centerY = stargateTile.y + orientation.getOffsetY() * 0.5;
			centerZ = stargateTile.z + direction.getOffsetX() * 0.5 + direction.getOffsetZ() * 3.5;

			if (direction.getOffsetX() < 0) {
				centerX += 1;
				centerZ += 1;
			}

			if (direction.getOffsetZ() < 0) {
				centerZ += 1;
				centerX += 1;
			}
		}

		double x1, y1, z1, x2, y2, z2;

		if (orientation == Direction.NORTH) {
			double x = radius * Math.abs(direction.getOffsetZ());
			double z = radius * Math.abs(direction.getOffsetX());

			x1 = centerX - x;
			y1 = centerY - radius;
			z1 = centerZ - z;

			x2 = centerX + x - direction.getOffsetX() * depth;
			y2 = centerY + radius;
			z2 = centerZ + z - direction.getOffsetZ() * depth;
		} else {
			x1 = centerX - radius;
			y1 = centerY;
			z1 = centerZ - radius;

			x2 = centerX + radius;
			y2 = centerY + orientation.getOffsetY() * depth;
			z2 = centerZ + radius;
		}

		double minX = Math.min(x1, x2);
		double minY = Math.min(y1, y2);
		double minZ = Math.min(z1, z2);
		double maxX = Math.max(x1, x2);
		double maxY = Math.max(y1, y2);
		double maxZ = Math.max(z1, z2);

		return AABB.getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	protected void resetGateDirection() {
		ringDirection = false;
	}

	public void tick() {
		if (animation == StargateAnimation.KAWOOSH && stargateTile.worldObj != null) {
			kawooshDestroyInnerBlocks();
			AABB detectionBox = this.getKawooshDetectionBox();
			List<Entity> list = this.stargateTile.worldObj.getEntitiesWithinAABBExcludingEntity(null, detectionBox);

			for (Entity entity : list) {
				if (entity instanceof Player) {
					((Player) entity).killPlayer();
				} else {
					entity.hurt(null, 100000, null);
				}
			}

			Direction direction = getDirection();
			Direction orientation = getOrientation();

			int startX = (int) Math.floor(detectionBox.minX);
			int startY = (int) Math.floor(detectionBox.minY);
			int startZ = (int) Math.floor(detectionBox.minZ);
			int endX = (int) Math.ceil(detectionBox.maxX) - 1;
			int endY = (int) Math.ceil(detectionBox.maxY) - 1;
			int endZ = (int) Math.ceil(detectionBox.maxZ) - 1;

			for (int x = startX; x <= endX; x++) {
				for (int y = startY; y <= endY; y++) {
					for (int z = startZ; z <= endZ; z++) {
						boolean isMinX = (x == startX);
						boolean isMaxX = (x == endX);
						boolean isMinY = (y == startY);
						boolean isMaxY = (y == endY);
						boolean isMinZ = (z == startZ);
						boolean isMaxZ = (z == endZ);

						boolean isCorner = false;

						if (direction == Direction.UP || direction == Direction.DOWN) {
							if (orientation == Direction.EAST || orientation == Direction.WEST) {
								isCorner = (isMinY || isMaxY) && (isMinZ || isMaxZ) && (isMinX || isMaxX);
							} else {
								isCorner = (isMinX || isMaxX) && (isMinY || isMaxY) && (isMinZ || isMaxZ);
							}
						} else if (direction == Direction.NORTH || direction == Direction.SOUTH) {
							isCorner = (isMinX || isMaxX) && (isMinY || isMaxY) && (isMinZ || isMaxZ);
						} else if (direction == Direction.EAST || direction == Direction.WEST) {
							isCorner = (isMinY || isMaxY) && (isMinZ || isMaxZ) && (isMinX || isMaxX);
						}

						if (isCorner) {
							continue;
						}

						if (stargateTile.worldObj.isAirBlock(x, y, z)) {
							continue;
						}

						stargateTile.worldObj.setBlockWithNotify(x, y, z, 0);
					}
				}
			}
		}

		if ((state == StargateState.OPENING || state == StargateState.CONNECTED) && stargateTile.worldObj != null) {
			if (state == StargateState.CONNECTED) {
				this.teleportBlocks();
			}
			Direction orientation = getOrientation();

			Direction direction = getDirection();

			AABB detectionBox = this.getDetectionBox();
			List<Entity> list = this.stargateTile.worldObj.getEntitiesWithinAABBExcludingEntity(null, detectionBox);

			for (Entity entity : list) {
				double dx = direction.getOffsetX();
				double dz = direction.getOffsetZ();

				double cx = stargateTile.x + 0.5;
				double cy = stargateTile.y + 3.5;
				double cz = stargateTile.z + 0.5;

				double x0 = entity.xo;
				double y0 = entity.yo;
				double z0 = entity.zo;

				double x1 = entity.x;
				double y1 = entity.y;
				double z1 = entity.z;

				double nx = dx;
				double ny = 0;
				double nz = dz;

				if (orientation != Direction.NORTH) {
					cx = stargateTile.x + direction.getOffsetZ() * 0.5 + direction.getOffsetX() * 3.5;
					cy = stargateTile.y + orientation.getOffsetY() * 0.5;
					cz = stargateTile.z + direction.getOffsetX() * 0.5 + direction.getOffsetZ() * 3.5;

					if (direction.getOffsetX() < 0) {
						cx += 1;
						cz += 1;
					}

					if (direction.getOffsetZ() < 0) {
						cz += 1;
						cx += 1;
					}

					nx = 0;
					ny = -orientation.getOffsetY();
					nz = 0;
				}

				double d0 = (x0 - cx) * nx + (y0 - cy) * ny + (z0 - cz) * nz;
				double d1 = (x1 - cx) * nx + (y1 - cy) * ny + (z1 - cz) * nz;

				if (d0 * d1 < 0) {
					double t = d0 / (d0 - d1);
					double ix = x0 + t * (x1 - x0);
					double iy = y0 + t * (y1 - y0);
					double iz = z0 + t * (z1 - z0);

					double dxIntersect = ix - cx;
					double dyIntersect = iy - cy;
					double dzIntersect = iz - cz;
					double distanceSq = dxIntersect * dxIntersect + dyIntersect * dyIntersect + dzIntersect * dzIntersect;

					if (distanceSq < 7.5) {
						if (entity instanceof Player && EnvironmentHelper.isClientWorld()) {
							NetworkHandler.sendToServer(new PlayerEnterStargateMessage(stargateTile.x, stargateTile.y, stargateTile.z, stargateTile.worldObj.dimension.id, entity.xd, entity.yd, entity.zd, d0 < 0 && d1 > 0));
						} else {
							StargateSession session = StargateSessionManager.getInstance().getSession(this);

							if (session != null) {
								if (
									(session.destinationX == stargateTile.x && session.destinationY == stargateTile.y && session.destinationZ == stargateTile.z) ||
										d0 > 0 && d1 < 0
								) {
									SoundHelper.playShortSoundAt("stargate:stargate.eventHorizon.enter", SoundCategory.WORLD_SOUNDS, (float) entity.x, (float) entity.y, (float) entity.z, 1.0f, 1.0f);
									if (state == StargateState.CONNECTED || animationTick >= 18) {
										if (entity instanceof Player) {
											((Player) entity).killPlayer();
										} else {
											entity.hurt(null, 100000, null);
										}
									}
								} else if (d0 < 0 && d1 > 0) {
									if (state == StargateState.CONNECTED) {
										this.teleportEntity(entity, session);
										StargateSessionManager.getInstance().endSession(this);
									} else {
										if (animationTick >= 18) {
											if (entity instanceof Player) {
												((Player) entity).killPlayer();
											} else {
												entity.hurt(null, 100000, null);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (!EnvironmentHelper.isClientWorld()) {
			if (getFamily() == StargateFamily.Universe) {
				if (state != StargateState.IDLE && lastState == StargateState.IDLE && animation != StargateAnimation.KAWOOSH && animation != StargateAnimation.CLOSING) {
					playAnimation(StargateAnimation.UNIVERSE_START);
				}
			}

			StargateDematerializedManager.getInstance().materializeEntities(this);
			StargateDematerializedManager.getInstance().materializeBlocks(this);

			StargateSession session = StargateSessionManager.getInstance().getSession(this);

			if (
				(
					(state == StargateState.OPENING && animation != StargateAnimation.KAWOOSH) || (state == StargateState.CLOSING && animation != StargateAnimation.CLOSING) ||
						state == StargateState.CONNECTED
				) && session == null
			) {
				if (state == StargateState.CONNECTED) {
					state = StargateState.CLOSING;

					playAnimation(StargateAnimation.CLOSING);
				} else {
					state = StargateState.IDLE;
					currentDialingAddressSize = 0;

					this.resetGateDirection();

					if (stargateTile.worldObj != null) {
						stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
					}
				}
			}

			if (state != StargateState.CONNECTED && session != null && animation != StargateAnimation.KAWOOSH) {
				state = StargateState.CONNECTED;

				if (stargateTile.worldObj != null) {
					stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
				}
			}

			if (state == StargateState.OPENING && session != null && animation == StargateAnimation.KAWOOSH) {
				currentDialingAddressSize = session.dialingAddressSize;
			}

			if (state == StargateState.CONNECTED && session != null && animation != StargateAnimation.KAWOOSH) {
				boolean needUpdate = currentDialingAddressSize != session.dialingAddressSize;
				currentDialingAddressSize = session.dialingAddressSize;

				if (needUpdate && stargateTile.worldObj != null) {
					stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
				}
			}

			if ((state == StargateState.CONNECTED || state == StargateState.OPENING) && animation == StargateAnimation.CLOSING) {
				animation = StargateAnimation.NONE;
				animationTick = 0;
				lastAnimationTick = 0;

				if (stargateTile.worldObj != null) {
					stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
				}
			}
		}

		boolean eventHorizonNoise = state == StargateState.CONNECTED;

		if (!lastEventHorizonNoise && eventHorizonNoise) {
			playSoundAtCenter("stargate:stargate.eventHorizon", SoundCategory.WORLD_SOUNDS, 1.0f, 1.0f, true);
		}

		if (lastEventHorizonNoise && !eventHorizonNoise) {
			stopSoundAtCenter("stargate:stargate.eventHorizon");
		}

		lastEventHorizonNoise = eventHorizonNoise;

		if (animation == StargateAnimation.UNIVERSE_START) {
			updateAnimation();
			lastState = state;
			return;
		}

		updateRotation();

		if (ringMove) {
			lastState = state;
			return;
		}

		lastState = state;

		updateCommands();
		updateAnimation();
	}

	protected void playSoundAtCenter(String name, SoundCategory soundCategory, float volume, float pitch, boolean loop) {
		Direction direction = getDirection();
		Direction orientation = getOrientation();

		float centerX, centerY, centerZ;

		if (orientation == Direction.NORTH) {
			centerX = stargateTile.x + 0.5f;
			centerY = stargateTile.y + 3.5f;
			centerZ = stargateTile.z + 0.5f;
		} else {
			centerX = stargateTile.x + direction.getOffsetZ() * 0.5f + direction.getOffsetX() * 3.5f;
			centerY = stargateTile.y + orientation.getOffsetY() * 0.5f;
			centerZ = stargateTile.z + direction.getOffsetX() * 0.5f + direction.getOffsetZ() * 3.5f;

			if (direction.getOffsetX() < 0) {
				centerX += 1;
				centerZ += 1;
			}

			if (direction.getOffsetZ() < 0) {
				centerZ += 1;
				centerX += 1;
			}
		}

		if (loop) {
			SoundHelper.playSingleSoundAtWithLoop(name, soundCategory, centerX, centerY, centerZ, volume, pitch);
		} else {
			SoundHelper.playSingleSoundAt(name, soundCategory, centerX, centerY, centerZ, volume, pitch);
		}
	}

	protected void stopSoundAtCenter(String name) {
		Direction direction = getDirection();
		Direction orientation = getOrientation();

		float centerX, centerY, centerZ;

		if (orientation == Direction.NORTH) {
			centerX = stargateTile.x + 0.5f;
			centerY = stargateTile.y + 3.5f;
			centerZ = stargateTile.z + 0.5f;
		} else {
			centerX = stargateTile.x + direction.getOffsetZ() * 0.5f + direction.getOffsetX() * 3.5f;
			centerY = stargateTile.y + orientation.getOffsetY() * 0.5f;
			centerZ = stargateTile.z + direction.getOffsetX() * 0.5f + direction.getOffsetZ() * 3.5f;

			if (direction.getOffsetX() < 0) {
				centerX += 1;
				centerZ += 1;
			}

			if (direction.getOffsetZ() < 0) {
				centerZ += 1;
				centerX += 1;
			}
		}

		SoundHelper.stopSingleSoundAt(name, centerX, centerY, centerZ);
	}

	public void teleportEntity(Entity entity, StargateSession session) {
		Direction originDirection = getDirection();
		Direction originOrientation = getOrientation();

		Direction destinationDirection = session.destinationDirection;
		Direction destinationOrientation = session.destinationOrientation;

		double originX, originY, originZ;
		double destinationX, destinationY, destinationZ;

		if (originOrientation == Direction.NORTH) {
			originX = stargateTile.x + 0.5;
			originY = stargateTile.y + 3.5;
			originZ = stargateTile.z + 0.5;
		} else {
			originX = stargateTile.x + originDirection.getOffsetZ() * 0.5 + originDirection.getOffsetX() * 3.5;
			originY = stargateTile.y + originOrientation.getOffsetY() * 0.5;
			originZ = stargateTile.z + originDirection.getOffsetX() * 0.5 + originDirection.getOffsetZ() * 3.5;

			if (originDirection.getOffsetX() < 0) {
				originX += 1;
				originZ += 1;
			}

			if (originDirection.getOffsetZ() < 0) {
				originZ += 1;
				originX += 1;
			}
		}

		if (destinationOrientation == Direction.NORTH) {
			destinationX = session.destinationX + 0.5;
			destinationY = session.destinationY + 3.5;
			destinationZ = session.destinationZ + 0.5;
		} else {
			destinationX = session.destinationX + destinationDirection.getOffsetZ() * 0.5 + destinationDirection.getOffsetX() * 3.5;
			destinationY = session.destinationY + destinationOrientation.getOffsetY() * 0.5;
			destinationZ = session.destinationZ + destinationDirection.getOffsetX() * 0.5 + destinationDirection.getOffsetZ() * 3.5;

			if (destinationDirection.getOffsetX() < 0) {
				destinationX += 1;
				destinationZ += 1;
			}

			if (destinationDirection.getOffsetZ() < 0) {
				destinationZ += 1;
				destinationX += 1;
			}
		}

		int onx, ony, onz;
		if (originOrientation == Direction.NORTH) {
			onx = originDirection.getOffsetX();
			ony = 0;
			onz = originDirection.getOffsetZ();
		} else {
			onx = 0;
			ony = -originOrientation.getOffsetY();
			onz = 0;
		}

		int oux, ouy, ouz;
		if (ony != 0) {
			oux = originDirection.getOffsetX();
			ouy = 0;
			ouz = originDirection.getOffsetZ();
		} else {
			oux = 0;
			ouy = 1;
			ouz = 0;
		}

		int orx = ony * ouz - onz * ouy;
		int ory = 0;
		int orz = onx * ouy - ony * oux;

		int ddx, ddy, ddz;
		if (destinationOrientation != Direction.NORTH) {
			ddx = 0;
			ddy = destinationOrientation.getOffsetY();
			ddz = 0;
		} else {
			ddx = -destinationDirection.getOffsetX();
			ddy = 0;
			ddz = -destinationDirection.getOffsetZ();
		}

		int dux, duy, duz;
		if (Math.abs(ddy) == 1) {
			dux = destinationDirection.getOffsetX();
			duy = 0;
			duz = destinationDirection.getOffsetZ();
		} else {
			dux = 0;
			duy = 1;
			duz = 0;
		}
		int drx = ddy * duz - ddz * duy;
		int dry = 0;
		int drz = ddx * duy - ddy * dux;

		double dx = entity.x - originX;
		double dy = entity.y - originY;
		double dz = entity.z - originZ;

		double alpha = dx * orx + dy * ory + dz * orz;
		double beta = dx * oux + dy * ouy + dz * ouz;

		double vx = entity.xd, vy = entity.yd, vz = entity.zd;
		double valpha = vx * orx + vy * ory + vz * orz;
		double vbeta = vx * oux + vy * ouy + vz * ouz;
		double vnorm = vx * onx + vy * ony + vz * onz;

		double newX = destinationX + alpha * drx + beta * dux;
		double newY = destinationY + alpha * dry + beta * duy;
		double newZ = destinationZ + alpha * drz + beta * duz;

		double nVx = valpha * drx + vbeta * dux + vnorm * ddx;
		double nVy = valpha * dry + vbeta * duy + vnorm * ddy;
		double nVz = valpha * drz + vbeta * duz + vnorm * ddz;

		float newYaw;
		float newPitch = entity.xRot;

		if (originOrientation == Direction.NORTH && destinationOrientation == Direction.NORTH) {
			int rot = Math.floorMod(
				originDirection.getHorizontalIndex() - destinationDirection.getOpposite().getHorizontalIndex(), 4
			);
			newYaw = entity.yRot + rot * -90;
		} else {
			double horiz = Math.sqrt(nVx * nVx + nVz * nVz);
			newYaw = (float) Math.toDegrees(Math.atan2(-nVx, nVz));
			newPitch = (float) Math.toDegrees(Math.atan2(-nVy, horiz));
		}

		entity.xd = nVx;
		entity.yd = nVy;
		entity.zd = nVz;

		if (originOrientation != Direction.NORTH && destinationOrientation == Direction.NORTH && destinationY - newY > 0) {
			double distX = destinationX - newX;
			double distY = destinationY - newY;
			double distZ = destinationZ - newZ;
			double dist = distX * distX + distY * distY + distZ * distZ;

			if (dist > 1.905) {
				newY += entity.heightOffset;
			}
		}

		SoundHelper.playShortSoundAt("stargate:stargate.eventHorizon.enter", SoundCategory.WORLD_SOUNDS, (float) entity.x, (float) entity.y, (float) entity.z, 1.0f, 1.0f);
		SoundHelper.playShortSoundAt("stargate:stargate.eventHorizon.enter", SoundCategory.WORLD_SOUNDS, (float) newX, (float) newY, (float) newZ, 1.0f, 1.0f);

		if (entity.vehicle == null) {
			StargateDematerializedManager.getInstance().dematerializeEntity(newX, newY, newZ, newYaw, newPitch, session, entity);
		}
	}

	private void teleportBlocks() {
		if (EnvironmentHelper.isClientWorld()) {
			return;
		}

		if (stargateTile.worldObj == null) {
			return;
		}

		StargateSession session = StargateSessionManager.getInstance().getSession(this);

		if (session == null) {
			return;
		}

		if (session.destinationX == stargateTile.x && session.destinationY == stargateTile.y && session.destinationZ == stargateTile.z) {
			return;
		}


		Direction originDirection = getDirection();
		Direction originOrientation = getOrientation();

		Direction destinationDirection = session.destinationDirection;
		Direction destinationOrientation = session.destinationOrientation;

		int originX, originY, originZ;
		int destinationX, destinationY, destinationZ;

		if (originOrientation == Direction.NORTH) {
			originX = stargateTile.x;
			originY = stargateTile.y + 3;
			originZ = stargateTile.z;
		} else {
			originX = stargateTile.x + originDirection.getOffsetX() * 3;
			originY = stargateTile.y;
			originZ = stargateTile.z + originDirection.getOffsetZ() * 3;

			if (originDirection.getOffsetX() < 0) {
				originX += 1;
				originZ += 1;
			}

			if (originDirection.getOffsetZ() < 0) {
				originZ += 1;
				originX += 1;
			}
		}

		if (destinationOrientation == Direction.NORTH) {
			destinationX = session.destinationX;
			destinationY = session.destinationY + 3;
			destinationZ = session.destinationZ;
		} else {
			destinationX = session.destinationX + destinationDirection.getOffsetX() * 3;
			destinationY = session.destinationY;
			destinationZ = session.destinationZ + destinationDirection.getOffsetZ() * 3;

			if (destinationDirection.getOffsetX() < 0) {
				destinationX += 1;
				destinationZ += 1;
			}

			if (destinationDirection.getOffsetZ() < 0) {
				destinationZ += 1;
				destinationX += 1;
			}
		}

		int onx, ony, onz;
		if (originOrientation == Direction.NORTH) {
			onx = originDirection.getOffsetX();
			ony = 0;
			onz = originDirection.getOffsetZ();
		} else {
			onx = 0;
			ony = -originOrientation.getOffsetY();
			onz = 0;
		}

		int oux, ouy, ouz;
		if (ony != 0) {
			oux = originDirection.getOffsetX();
			ouy = 0;
			ouz = originDirection.getOffsetZ();
		} else {
			oux = 0;
			ouy = 1;
			ouz = 0;
		}

		int orx = ony * ouz - onz * ouy;
		int ory = 0;
		int orz = onx * ouy - ony * oux;

		int ddx, ddy, ddz;
		if (destinationOrientation != Direction.NORTH) {
			ddx = 0;
			ddy = destinationOrientation.getOffsetY();
			ddz = 0;
		} else {
			ddx = -destinationDirection.getOffsetX();
			ddy = 0;
			ddz = -destinationDirection.getOffsetZ();
		}

		int dux, duy, duz;
		if (Math.abs(ddy) == 1) {
			dux = destinationDirection.getOffsetX();
			duy = 0;
			duz = destinationDirection.getOffsetZ();
		} else {
			dux = 0;
			duy = 1;
			duz = 0;
		}
		int drx = ddy * duz - ddz * duy;
		int dry = 0;
		int drz = ddx * duy - ddy * dux;

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if ((i == -2 || i == 2) && (j == -2 || j == 2)) {
					continue;
				}

				int x = originX + i * originDirection.getOffsetZ();
				int y = originY + j;
				int z = originZ + i * originDirection.getOffsetX();

				if (originOrientation != Direction.NORTH) {
					x = originX + i * originDirection.getOffsetZ() + j * originDirection.getOffsetX();
					y = originY;
					z = originZ + i * originDirection.getOffsetX() + j * originDirection.getOffsetZ();
				}

				if (stargateTile.worldObj.isAirBlock(x, y, z)) {
					continue;
				}

				int id = stargateTile.worldObj.getBlockId(x, y, z);

				int meta = stargateTile.worldObj.getBlockMetadata(x, y, z);

				if (id == StargateBlocks.STARGATE_MILKYWAY.id()) {
					continue;
				}

				if (id == StargateBlocks.STARGATE_PEGASUS.id()) {
					continue;
				}

				if (id == StargateBlocks.STARGATE_UNIVERSE.id()) {
					continue;
				}

				if (id == Blocks.PISTON_MOVING.id()) {
					continue;
				}

				AABB boundingBox = AABB.getTemporaryBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);

				boolean shouldTeleport = false;

				@Nullable Block<?> block = stargateTile.worldObj.getBlock(x, y, z);

				if (block != null) {
					stargateTile.worldObj.collidingBoundingBoxes.clear();
					block.getCollidingBoundingBoxes(stargateTile.worldObj, x, y, z, boundingBox, stargateTile.worldObj.collidingBoundingBoxes);


					for (AABB aabb : stargateTile.worldObj.collidingBoundingBoxes) {
						double closestX = Math.max(aabb.minX, Math.min(originX, aabb.maxX));
						double closestY = Math.max(aabb.minY, Math.min(originY, aabb.maxY));
						double closestZ = Math.max(aabb.minZ, Math.min(originZ, aabb.maxZ));

						double dx = originX - closestX;
						double dy = originY - closestY;
						double dz = originZ - closestZ;
						double distanceSquared = dx * dx + dy * dy + dz * dz;

						if (distanceSquared < 2.25) {
							shouldTeleport = true;
							break;
						}
					}

				}

				if (!shouldTeleport) {
					continue;
				}

				int dx = x - originX;
				int dy = y - originY;
				int dz = z - originZ;

				int alpha = dx * orx + dy * ory + dz * orz;
				int beta = dx * oux + dy * ouy + dz * ouz;

				int newX = destinationX + alpha * drx + beta * dux;
				int newY = destinationY + alpha * dry + beta * duy;
				int newZ = destinationZ + alpha * drz + beta * duz;

				if (block != null && block.getLogic() instanceof BlockLogicRotatable) {
					Direction blockDirection = BlockLogicRotatable.getDirectionFromMeta(meta);

					if (originOrientation != Direction.NORTH && destinationOrientation == Direction.NORTH) {
						blockDirection = destinationDirection.getOpposite();
					} else if (originOrientation == Direction.NORTH && destinationOrientation != Direction.NORTH) {
						blockDirection = originDirection;
					} else {
						blockDirection = blockDirection.rotate(destinationDirection.getHorizontalIndex() - originDirection.getOpposite().getHorizontalIndex());
					}

					int newMeta = BlockLogicRotatable.setDirection(meta, blockDirection);

					stargateTile.worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
				}

				if (block != null && block.getLogic() instanceof BlockLogicVeryRotatable) {
					Direction blockDirection = BlockLogicVeryRotatable.metaToDirection(meta);

					if (originOrientation != Direction.NORTH && destinationOrientation == Direction.NORTH) {
						blockDirection = destinationDirection.getOpposite();
					} else if (originOrientation == Direction.NORTH && destinationOrientation != Direction.NORTH) {
						blockDirection = originDirection;
					} else {
						blockDirection = blockDirection.rotate(destinationDirection.getHorizontalIndex() - originDirection.getOpposite().getHorizontalIndex());
					}

					int newMeta = BlockLogicVeryRotatable.setDirection(meta, blockDirection);

					stargateTile.worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
				}

				if (block != null && block.getLogic() instanceof BlockLogicFullyRotatable) {
					Direction blockDirection = BlockLogicFullyRotatable.metaToDirection(meta);

					if (originOrientation != Direction.NORTH && destinationOrientation == Direction.NORTH) {
						blockDirection = destinationDirection.getOpposite();
					} else if (originOrientation == Direction.NORTH && destinationOrientation != Direction.NORTH) {
						blockDirection = originDirection;
					} else {
						blockDirection = blockDirection.rotate(destinationDirection.getHorizontalIndex() - originDirection.getOpposite().getHorizontalIndex());
					}

					int newMeta = meta & -8 | BlockLogicFullyRotatable.directionToMeta(blockDirection);

					stargateTile.worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
				}

				if (block != null && block.getLogic() instanceof BlockLogicAxisAligned) {
					Axis blockAxis = BlockLogicAxisAligned.metaToAxis(meta);

					if (originOrientation != Direction.NORTH && destinationOrientation == Direction.NORTH) {
						blockAxis = destinationDirection.getAxis();
					} else if (originOrientation == Direction.NORTH && destinationOrientation != Direction.NORTH) {
						blockAxis = Axis.Y;
					} else {
						if (destinationDirection.getAxis() != originDirection.getAxis()) {
							if (blockAxis == Axis.X) {
								blockAxis = Axis.Z;
							} else if (blockAxis == Axis.Z) {
								blockAxis = Axis.X;
							}
						}
					}

					int newMeta = BlockLogicAxisAligned.axisToMeta(blockAxis);

					stargateTile.worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
				}

				StargateDematerializedManager.getInstance().dematerializeBlock(session.destinationX, session.destinationY, session.destinationZ, session.destinationDim, stargateTile.worldObj, x, y, z, newX, newY, newZ);
				StargateSessionManager.getInstance().endSession(this);
			}
		}
	}

	protected void updateRotation() {
		if (!lastRingMove && ringMove) {
			playSoundAtCenter("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, 1.0f, 1.0f, false);
		}

		if (lastRingMove && !ringMove) {
			stopSoundAtCenter("stargate:stargate.milkyway.roll");
		}

		lastRingMove = ringMove;
		lastAngle = currentAngle;

		if (ringMoveForEver) {
			if (ringDirection) {
				targetAngle = computeTargetAngle(getCurrentSymbol() - 1);
			} else {
				targetAngle = computeTargetAngle(getCurrentSymbol() + 1);
			}
		}

		double angleDistance = angularDistance(targetAngle, currentAngle);

		if (ringMoveForEver) {
			angleDistance += 100;
		}

		if (angleDistance < 0.01) {
			ringMove = false;
			return;
		}

		ringMove = true;

		double angleSpeed = Math.min(angleDistance, 1.5);

		if (ringDirection) {
			currentAngle -= angleSpeed;
		} else {
			currentAngle += angleSpeed;
		}
	}

	private void updateAnimation() {
		eventHorizon.applyRandomImpulse();
		eventHorizon.updateEventHorizon();

		lastEventHorizonTick = eventHorizonTick;
		eventHorizonTick++;

		if (lastAnimation != animation) {
			if (animation == StargateAnimation.KAWOOSH) {
				if (getFamily() == StargateFamily.Pegasus) {
					SoundHelper.playShortSoundAt("stargate:stargate.pegasus.evenHorizon.open", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
				} else if (getFamily() == StargateFamily.Universe) {
					SoundHelper.playShortSoundAt("stargate:stargate.universe.evenHorizon.open", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
				} else {
					SoundHelper.playShortSoundAt("stargate:stargate.milkyway.evenHorizon.open", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
				}
			}
			if (animation == StargateAnimation.CLOSING) {
				SoundHelper.playShortSoundAt("stargate:stargate.evenHorizon.close", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
			}
			if (animation == StargateAnimation.CANCEL) {
				if (getFamily() == StargateFamily.Pegasus) {
					SoundHelper.playShortSoundAt("stargate:stargate.pegasus.dial.fail", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
				} else if (getFamily() == StargateFamily.Universe) {
					SoundHelper.playShortSoundAt("stargate:stargate.universe.dial.fail", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
				} else {
					SoundHelper.playShortSoundAt("stargate:stargate.milkyway.dial.fail", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
				}
			}
		}

		lastAnimation = animation;

		if (animation == StargateAnimation.NONE) {
			return;
		}


		if (animation == StargateAnimation.ENCODE_CHEVRON) {
			boolean isLastChevronActive = !(state == StargateState.IDLE || state == StargateState.DIALLING);

			if (animationTick == 4) {
				SoundHelper.playShortSoundAt("stargate:stargate.milkyway.encode.lock", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
			}
			if (animationTick == 26 && !isLastChevronActive) {
				SoundHelper.playShortSoundAt("stargate:stargate.milkyway.encode.slow", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
			}
		}

		if (animation == StargateAnimation.FAST_ENCODE_CHEVRON) {
			if (getFamily() == StargateFamily.Pegasus) {
				if (animationTick == 0) {
					SoundHelper.playShortSoundAt("stargate:stargate.pegasus.encode.fast", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
				}
			} else if (animationTick == 4) {
				SoundHelper.playShortSoundAt("stargate:stargate.milkyway.encode.fast", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
			}
		}

		if (animation == StargateAnimation.UNIVERSE_START) {
			if (animationTick == 0) {
				SoundHelper.playShortSoundAt("stargate:stargate.universe.dial.start", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
			}
		}

		if (animation == StargateAnimation.UNIVERSE_ENCODE_CHEVRON || animation == StargateAnimation.UNIVERSE_FAST_ENCODE_CHEVRON) {
			if (animationTick == 0) {
				SoundHelper.playShortSoundAt("stargate:stargate.universe.encode.fast", SoundCategory.WORLD_SOUNDS, stargateTile.x, stargateTile.y, stargateTile.z, 1.0f, 1.0f);
			}
		}

		// Wait for anim completion
		lastAnimationTick = animationTick;
		if (++animationTick >= animation.duration) {
			animation = StargateAnimation.NONE;
			animationTick = 0;
			lastAnimationTick = 0;
		}
	}

	public void playAnimation(StargateAnimation animation) {
		if (EnvironmentHelper.isClientWorld()) {
			throw new UnsupportedOperationException("Cannot play animations on the client");
		}

		this.animation = animation;
		animationTick = 0;
		lastAnimationTick = 0;

		if (stargateTile.worldObj != null) {
			stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
		}
	}

	private void updateCommands() {
		if (animation != StargateAnimation.NONE || commandQueue.isEmpty()) {
			return;
		}

		Runnable command = commandQueue.poll();
		if (command == null) {
			return;
		}

		command.run();
	}

	public void encode() {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}

			int currentSymbol = getCurrentSymbol();

			for (int i = 0; i < currentDialingAddressSize; i++) {
				if (currentDialingAddress[i] == currentSymbol) {
					return;
				}
			}

			ringDirection = !ringDirection;
			currentDialingAddress[currentDialingAddressSize] = currentSymbol;
			currentDialingAddressSize += 1;

			if (currentSymbol == 0 || currentDialingAddressSize == 9) {
				state = StargateState.AWAIT;
			} else {
				state = StargateState.DIALLING;
			}

			playAnimation(StargateAnimation.ENCODE_CHEVRON);
		});
	}

	public void moveToSymbol(int symbol) {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}
			targetAngle = computeTargetAngle(symbol);

			if (stargateTile.worldObj != null) {
				stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
			}
		});
	}

	public void rotateClockwise(Runnable action) {
		if (getFamily() == StargateFamily.Pegasus) {
			return;
		}
		if (ringMoveForEver) {
			action.run();
			return;
		}
		commandQueue.add(() -> {
			ringMoveForEver = true;
			ringDirection = true;
			action.run();
		});
	}

	public void rotateCounterClockwise(Runnable action) {
		if (getFamily() == StargateFamily.Pegasus) {
			return;
		}
		if (ringMoveForEver) {
			action.run();
			return;
		}
		commandQueue.add(() -> {
			ringMoveForEver = true;
			ringDirection = false;
			action.run();
		});

	}

	public void stopRotation() {
		if (getFamily() == StargateFamily.Pegasus) {
			return;
		}
		ringMoveForEver = false;
	}

	public void fastEncode(int symbol) {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}

			for (int i = 0; i < currentDialingAddressSize; i++) {
				if (currentDialingAddress[i] == symbol) {
					return;
				}
			}

			ringDirection = !ringDirection;
			currentDialingAddress[currentDialingAddressSize] = symbol;
			currentDialingAddressSize += 1;

			if (symbol == 0 || currentDialingAddressSize == 9) {
				state = StargateState.AWAIT;
			} else {
				state = StargateState.DIALLING;
			}

			stopSoundAtCenter("stargate:stargate.milkyway.roll");
			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}

	public void removeSymbol(int symbol) {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING || state == StargateState.AWAIT)) {
				return;
			}

			int symbolIndex = -1;
			for (int i = 0; i < currentDialingAddressSize; i++) {
				if (currentDialingAddress[i] == symbol) {
					symbolIndex = i;
					break;
				}
			}

			if (symbolIndex == -1) {
				return;
			}

			for (int i = symbolIndex; i < currentDialingAddressSize - 1; i++) {
				currentDialingAddress[i] = currentDialingAddress[i + 1];
			}

			currentDialingAddressSize -= 1;
			ringDirection = !ringDirection;

			if (currentDialingAddressSize == 0) {
				state = StargateState.IDLE;
			} else {
				int lastSymbol = currentDialingAddress[currentDialingAddressSize - 1];
				if (lastSymbol == 0 || currentDialingAddressSize == 9) {
					state = StargateState.AWAIT;
				} else {
					state = StargateState.DIALLING;
				}
			}
			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}

	public void clearAddress() {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING || state == StargateState.AWAIT)) {
				return;
			}

			currentDialingAddressSize = 0;

			state = StargateState.IDLE;
		});
	}

	public void dial() {
		commandQueue.add(() -> {
			if (state != StargateState.AWAIT) {
				return;
			}

			StargateAddress originAddress = getAddress();

			{
				if (originAddress == null) {
					cancelDial();
					return;
				}

				for (int cx = originAddress.getStartChunkX(); cx <= originAddress.getEndChunkX(); cx++) {
					for (int cz = originAddress.getStartChunkZ(); cz <= originAddress.getEndChunkZ(); cz++) {
						List<TileEntity> tileEntities = StargateChunkLoader.loadTileEntities(stargateTile.worldObj, originAddress.getDim(), cx, cz);

						if (tileEntities == null) {
							continue;
						}

						for (TileEntity tileEntity : tileEntities) {
							if (tileEntity instanceof TileEntityStargate) {
								StargateComponent destinationGate = ((TileEntityStargate) tileEntity).getStargateComponent();

								if (destinationGate != null && this != destinationGate) {
									if (destinationGate.getFamily() == getFamily()) {
										StargateSession currentPresentSession = StargateSessionManager.getInstance().getSession(destinationGate);

										if (currentPresentSession != null) {
											cancelDial();
											return;
										}
									}
								}
							}
						}
					}
				}
			}

			if (currentDialingAddressSize < 7) {
				cancelDial();
				return;
			}

			int[] originRawAddress = originAddress.encodeAddress();

			if (currentDialingAddressSize == 7) {
				currentDialingAddress[6] = originRawAddress[6];
				currentDialingAddress[7] = 0;
				currentDialingAddress[8] = 0;
			} else if (currentDialingAddressSize == 8) {
				currentDialingAddress[7] = 0;
				currentDialingAddress[8] = 0;
			}

			StargateAddress destinationAddress = StargateAddress.createAddressFromEncoded(currentDialingAddress, getFamily());

			if (destinationAddress == null) {
				cancelDial();
				return;
			}

			List<StargateLocation> locations = new ArrayList<>();

			for (int cx = destinationAddress.getStartChunkX(); cx <= destinationAddress.getEndChunkX(); cx++) {
				for (int cz = destinationAddress.getStartChunkZ(); cz <= destinationAddress.getEndChunkZ(); cz++) {
					List<TileEntity> tileEntities = StargateChunkLoader.loadTileEntities(stargateTile.worldObj, destinationAddress.getDim(), cx, cz);

					if (tileEntities == null) {
						continue;
					}

					for (TileEntity tileEntity : tileEntities) {
						if (tileEntity instanceof TileEntityStargate) {
							StargateComponent destinationGate = ((TileEntityStargate) tileEntity).getStargateComponent();

							if (destinationGate != null && this != destinationGate) {
								if (destinationGate.getFamily() == getFamily()) {
									StargateSession currentPresentSession = StargateSessionManager.getInstance().getSession(destinationGate);

									if (currentPresentSession != null) {
										cancelDial();
										return;
									}
								} else if (currentDialingAddressSize == 7) {
									continue;
								}

								StargateAddress realDestinationAddress = destinationGate.getAddress();

								if (realDestinationAddress != null && Arrays.equals(originRawAddress, realDestinationAddress.encodeAddress())) {
									continue;
								}

								locations.add(new StargateLocation(
									tileEntity.x,
									tileEntity.y,
									tileEntity.z,
									destinationAddress.getDim(),
									destinationAddress,
									((TileEntityStargate) tileEntity).hasDHD(),
									destinationGate.getFamily()
								));
							}
						}
					}
				}
			}

			if (locations.isEmpty()) {
				cancelDial();
				return;
			}

			StargateLocation target = locations.get(0);
			int closestDistance = Integer.MAX_VALUE;

			for (StargateLocation location : locations) {
				int diffX = destinationAddress.getBlockX() - location.x;
				int diffY = 255 - location.y;
				int diffZ = destinationAddress.getBlockZ() - location.z;
				int distance = diffX * diffX + diffY * diffY + diffZ * diffZ;

				boolean shouldUpdate = false;

				if (location.hasDHD && !target.hasDHD) {
					shouldUpdate = true;
				} else if (location.hasDHD == target.hasDHD) {
					int candidateFamilyPriority = location.family == StargateFamily.Pegasus ? 2 : (location.family == StargateFamily.MilkyWay ? 1 : 0);
					int targetFamilyPriority = target.family == StargateFamily.Pegasus ? 2 : (target.family == StargateFamily.MilkyWay ? 1 : 0);

					if (candidateFamilyPriority > targetFamilyPriority) {
						shouldUpdate = true;
					} else if (candidateFamilyPriority == targetFamilyPriority && distance < closestDistance) {
						shouldUpdate = true;
					}
				}

				if (shouldUpdate) {
					target = location;
					closestDistance = distance;
				}
			}

			List<TileEntity> tileEntities = StargateChunkLoader.loadTileEntities(stargateTile.worldObj, target.dim, Math.floorDiv(target.x, 16), Math.floorDiv(target.z, 16));

			if (tileEntities == null) {
				cancelDial();
				return;
			}

			TileEntity targetTileEntity = null;
			for (TileEntity tileEntity : tileEntities) {
				if (tileEntity.x == target.x && tileEntity.y == target.y && tileEntity.z == target.z) {
					targetTileEntity = tileEntity;
					break;
				}
			}

			if (!(targetTileEntity instanceof TileEntityStargate)) {
				cancelDial();
				return;
			}

			StargateComponent destinationGate = ((TileEntityStargate) targetTileEntity).getStargateComponent();

			if (destinationGate == null) {
				cancelDial();
				return;
			}

			StargateSession currentPresentSession = StargateSessionManager.getInstance().getSession(destinationGate);

			if (currentPresentSession != null) {
				cancelDial();
				return;
			}

			StargateSessionManager.getInstance().createSession(this, destinationGate, currentDialingAddressSize);

			destinationGate.openGate();
			openGate();
		});
	}

	public void cancelDial() {
		if (state != StargateState.AWAIT && state != StargateState.DIALLING) {
			return;
		}

		state = StargateState.IDLE;
		currentDialingAddressSize = 0;

		if (stargateTile.worldObj != null) {
			stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
		}

		playAnimation(StargateAnimation.CANCEL);

		resetGateDirection();
	}

	private void openGate() {
		state = StargateState.OPENING;

		playAnimation(StargateAnimation.KAWOOSH);
	}

	public void closeGate() {
		if (isReceiverGate()) {
			return;
		}

		if (state != StargateState.CONNECTED) {
			return;
		}

		StargateSession session = StargateSessionManager.getInstance().getSession(this);

		if (session == null) {
			state = StargateState.CLOSING;

			playAnimation(StargateAnimation.CLOSING);
			return;
		}

		StargateSessionManager.getInstance().removeSession(this);
	}

	public boolean isReceiverGate() {
		if (state == StargateState.CLOSING) {
			return wasRecieverGate;
		}

		if (state != StargateState.CONNECTED) {
			wasRecieverGate = false;
			return false;
		}

		StargateSession session = StargateSessionManager.getInstance().getSession(this);

		if (session == null) {
			return wasRecieverGate;
		}

		if (stargateTile.worldObj == null) {
			return wasRecieverGate;
		}

		wasRecieverGate = session.destinationX == stargateTile.x &&
			session.destinationY == stargateTile.y &&
			session.destinationZ == stargateTile.z &&
			session.destinationDim == stargateTile.worldObj.dimension.id;

		return wasRecieverGate;
	}

	public abstract StargateFamily getFamily();

	public Direction getOrientation() {
		return orientation;
	}

	public void setOrientation(Direction orientation) {
		this.orientation = orientation;
	}

	@Nullable
	public StargateAddress getAddress() {
		return getAddressWithFamily(getFamily());
	}

	@Nullable
	public StargateAddress getAddressWithFamily(StargateFamily family) {
		return StargateAddress.createAddressFromBlock(stargateTile.x, stargateTile.z, stargateTile.worldObj.dimension.id, family);
	}

	public int getLightmap() {
		if (stargateTile.worldObj == null) {
			return LightmapHelper.getLightmapCoord(15, 15);
		}

		Direction originDirection = getDirection();
		Direction originOrientation = getOrientation();

		int originX, originY, originZ;

		if (originOrientation == Direction.NORTH) {
			originX = stargateTile.x;
			originY = stargateTile.y + 3;
			originZ = stargateTile.z;
		} else {
			originX = stargateTile.x + originDirection.getOffsetX() * 3;
			originY = stargateTile.y;
			originZ = stargateTile.z + originDirection.getOffsetZ() * 3;

			if (originDirection.getOffsetX() < 0) {
				originX += 1;
				originZ += 1;
			}

			if (originDirection.getOffsetZ() < 0) {
				originZ += 1;
				originX += 1;
			}
		}

		return stargateTile.worldObj.getLightmapCoord(originX, originY, originZ, stargateTile.worldObj.getBlockLightValue(originX, originY, originZ));
	}

	public void waitCommandQueue(Runnable action) {
		commandQueue.add(action);
	}

	public int getOpenTimeDuration() {
		if (state != StargateState.CONNECTED) {
			return 0;
		}

		StargateSession session = StargateSessionManager.getInstance().getSession(this);

		if (session == null) {
			return 0;
		}

		return session.openTick;
	}
}
