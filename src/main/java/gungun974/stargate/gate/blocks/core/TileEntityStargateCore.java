package gungun974.stargate.gate.blocks.core;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.StargateBlocks;
import gungun974.stargate.StargateMod;
import gungun974.stargate.core.SoundHelper;
import gungun974.stargate.core.StargateAddress;
import gungun974.stargate.core.StargateState;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import turniplabs.halplibe.helper.EnvironmentHelper;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;

public class TileEntityStargateCore extends TileEntity {
	public final static double symbolAngle = 360.0 / StargateAddress.NUMBER_OF_SYMBOL;
	private final Queue<Runnable> commandQueue = new ArrayDeque<>();
	public StargateEventHorizon eventHorizon = new StargateEventHorizon();
	private double currentAngle = 0;
	private double lastAngle = 0;
	private double targetAngle = 0;
	private boolean ringDirection = false;
	private boolean assembled = false;
	private Direction orientation = Direction.NORTH;
	private StargateAnimation animation = StargateAnimation.NONE;
	private int animationTick = 0;
	private int lastAnimationTick = 0;
	private int eventHorizonTick = 0;
	private int lasteventHorizonTick = 0;
	private StargateState state = StargateState.IDLE;
	private short currentDialingAddressSize = 0;
	private int[] currentDialingAddress = new int[9];

	@Nullable
	public static TileEntityStargateCore findStargateCore(WorldSource worldSource, int x, int y, int z) {
		TileEntity tileEntity = worldSource.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

		for (Direction direction : Direction.directions) {
			if (direction == Direction.DOWN) {
				continue;
			}

			for (int j = 0; j < 7; j++) {
				int i;
				switch (j) {
					case 0:
						i = 1;
						break;
					case 1:
					case 5:
						i = 2;
						break;
					case 2:
					case 3:
					case 4:
						i = 3;
						break;
					case 6:
						i = 1;
						tileEntity = worldSource.getTileEntity(x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j);
						if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
						break;
					default:
						continue;
				}

				if (direction == Direction.UP) {
					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j + i
					);
					if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j - i
					);
					if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j + i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j
					);
					if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j - i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j
					);
				} else {
					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j + direction.getOffsetZ() * i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j + direction.getOffsetX() * i
					);
					if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j - direction.getOffsetZ() * i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j - direction.getOffsetX() * i
					);

				}
				if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
			}
		}

		return null;
	}

	public static boolean isPartOfAssembled(WorldSource worldSource, int x, int y, int z) {
		TileEntity tileEntity = worldSource.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityStargateCore && ((TileEntityStargateCore) tileEntity).assembled)
			return true;

		for (Direction direction : Direction.directions) {
			if (direction == Direction.DOWN) {
				continue;
			}

			for (int j = 0; j < 7; j++) {
				int i;
				switch (j) {
					case 0:
						i = 1;
						break;
					case 1:
					case 5:
						i = 2;
						break;
					case 2:
					case 3:
					case 4:
						i = 3;
						break;
					case 6:
						i = 1;
						tileEntity = worldSource.getTileEntity(x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j);
						if (tileEntity instanceof TileEntityStargateCore && ((TileEntityStargateCore) tileEntity).assembled)
							return true;
						break;
					default:
						continue;
				}

				if (direction == Direction.UP) {
					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j + i
					);
					if (tileEntity instanceof TileEntityStargateCore && ((TileEntityStargateCore) tileEntity).assembled)
						return true;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j - i
					);
					if (tileEntity instanceof TileEntityStargateCore && ((TileEntityStargateCore) tileEntity).assembled)
						return true;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j + i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j
					);
					if (tileEntity instanceof TileEntityStargateCore && ((TileEntityStargateCore) tileEntity).assembled)
						return true;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j - i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j
					);
				} else {
					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j + direction.getOffsetZ() * i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j + direction.getOffsetX() * i
					);
					if (tileEntity instanceof TileEntityStargateCore && ((TileEntityStargateCore) tileEntity).assembled)
						return true;

					tileEntity = worldSource.getTileEntity(
						x - direction.getOffsetX() * j - direction.getOffsetZ() * i,
						y - direction.getOffsetY() * j,
						z - direction.getOffsetZ() * j - direction.getOffsetX() * i
					);

				}
				if (tileEntity instanceof TileEntityStargateCore && ((TileEntityStargateCore) tileEntity).assembled)
					return true;
			}
		}

		return false;
	}

	public static double angularDistance(double angle1, double angle2) {
		double diff = Math.abs(angle1 - angle2) % 360.0;
		return diff > 180.0 ? 360.0 - diff : diff;
	}

	public StargateState getState() {
		return state;
	}

	@Override
	public void invalidate() {
		SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, x, y, z);
		super.invalidate();
	}

	public boolean isAssembled() {
		return assembled;
	}

	public Direction getDirection() {
		World world = worldObj;
		if (world == null) {
			return Direction.NORTH;
		}

		return BlockLogicStargateCore.getDirectionFromMeta(world.getBlockMetadata(x, y, z)).getOpposite();
	}

	private boolean isValidStructure() {
		if (isValidStructureVertical()) {
			orientation = Direction.NORTH;
			return true;
		}
		if (isValidStructureHorizontal()) {
			orientation = Direction.UP;
			return true;
		}
		return false;
	}

	private boolean isValidStructureVertical() {
		World world = worldObj;
		if (world == null) {
			return false;
		}

		Direction direction = BlockLogicStargateCore.getDirectionFromMeta(
			world.getBlockMetadata(x, y, z)
		);

		for (int j = 0; j < 7; j++) {
			for (int i = -4; i < 5; i++) {
				switch (j) {
					case 0:
						if (Math.abs(i) != 1) continue;
						break;
					case 1:
					case 5:
						if (Math.abs(i) != 2) continue;
						break;
					case 2:
					case 3:
					case 4:
						if (Math.abs(i) != 3) continue;
						break;
					case 6:
						if (Math.abs(i) > 1) continue;
						break;
					default:
						continue;
				}

				int px = x + direction.getOffsetZ() * i;
				int py = y + j;
				int pz = z + direction.getOffsetX() * i;

				int id = world.getBlockId(px, py, pz);

				if (id != StargateBlocks.STARGATE_RING.id()) {
					return false;
				}

				int meta = world.getBlockMetadata(px, py, pz);

				if (j == 0 || j == 1 || j == 3 || j == 5 || (j == 6 && i == 0)) {
					if (meta != 1) {
						return false;
					}
				} else if (meta != 0) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidStructureHorizontal() {
		World world = worldObj;
		if (world == null) {
			return false;
		}

		Direction direction = BlockLogicStargateCore.getDirectionFromMeta(
			world.getBlockMetadata(x, y, z)
		);

		for (int j = 0; j < 7; j++) {
			for (int i = -4; i < 5; i++) {
				switch (j) {
					case 0:
						if (Math.abs(i) != 1) continue;
						break;
					case 1:
					case 5:
						if (Math.abs(i) != 2) continue;
						break;
					case 2:
					case 3:
					case 4:
						if (Math.abs(i) != 3) continue;
						break;
					case 6:
						if (Math.abs(i) > 1) continue;
						break;
					default:
						continue;
				}

				int px = x + direction.getOffsetZ() * i - direction.getOffsetX() * j;
				int py = y;
				int pz = z + direction.getOffsetX() * i - direction.getOffsetZ() * j;

				int id = world.getBlockId(px, py, pz);

				if (id != StargateBlocks.STARGATE_RING.id()) {
					return false;
				}

				int meta = world.getBlockMetadata(px, py, pz);

				if (j == 0 || j == 1 || j == 3 || j == 5 || (j == 6 && i == 0)) {
					if (meta != 1) {
						return false;
					}
				} else if (meta != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void checkAndAssemble() {
		boolean shouldAssembled = isValidStructure();

		if (shouldAssembled && !assembled) {
			// ASSEMBLE
			this.assembled = true;
			StargateMod.LOGGER.info("Assemble Stargate");
		}

		if (!shouldAssembled && assembled) {
			// DISASSEMBLE
			this.assembled = false;
			StargateMod.LOGGER.info("Disassemble Stargate");
		}
	}

	@Override
	public void readFromNBT(CompoundTag compoundTag) {
		assembled = compoundTag.getBooleanOrDefault("Assembled", false);
		orientation = Direction.values()[compoundTag.getIntegerOrDefault("Orientation", Direction.NORTH.ordinal())];

		super.readFromNBT(compoundTag);
	}

	@Override
	public void writeToNBT(CompoundTag compoundTag) {
		compoundTag.putBoolean("Assembled", assembled);
		compoundTag.putInt("Orientation", orientation.ordinal());

		super.writeToNBT(compoundTag);
	}

	public double interpolatedRingAngle(double partialTicks) {
		return lastAngle + (currentAngle - lastAngle) * partialTicks;
	}

	public boolean interpolatedChevronActive(int chevron, double partialTicks) {
		int currentEncodedChevron = 6 < currentDialingAddressSize ? currentDialingAddressSize + 1 : currentDialingAddressSize;

		boolean isLastChevronActive = !(state == StargateState.IDLE || state == StargateState.DIALLING);

		currentEncodedChevron = isLastChevronActive && currentDialingAddressSize != 9 ? currentEncodedChevron - 1 : currentEncodedChevron;

		if (animation == StargateAnimation.ENCODE_CHEVRON) {
			if (chevron < currentEncodedChevron - 1 && chevron != 6) {
				return true;
			}

			if (chevron != currentEncodedChevron - 1 && chevron != 6) {
				return false;
			}

			final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

			if (chevron == 6) {
				if (!isLastChevronActive && currentAnimationTick > 38.67) {
					return false;
				}
				return currentAnimationTick > 4.67;
			}

			return currentAnimationTick > 30.67;
		} else if (animation == StargateAnimation.FAST_ENCODE_CHEVRON) {
			if (chevron < currentEncodedChevron - 1 && chevron != 6) {
				return true;
			}

			if (chevron != currentEncodedChevron - 1 && chevron != 6) {
				return false;
			}

			final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

			if (chevron == 6 && !isLastChevronActive) {
				return false;
			}

			return currentAnimationTick > (double) StargateAnimation.FAST_ENCODE_CHEVRON.duration / 2;
		} else {
			if (chevron == 6) {
				return isLastChevronActive;
			}

			return chevron < currentEncodedChevron;
		}

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
			return (1 - Math.min((currentAnimationTick - 26.67) / 6.66, 1)) * -0.125;
		}

		if (currentAnimationTick > 12.67) {
			return Math.min(((currentAnimationTick - 12.67) / 6.66), 1) * -0.125;
		}

		return 0;
	}

	public double easeInExpo(double x) {
		return x == 0 ? 0 : Math.pow(2, 10 * x - 10);
	}

	public double easeOutExpo(double x) {
		return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);

	}

	public double interpolatedUnstableVortexDistance(double partialTicks) {
		if (animation != StargateAnimation.KAWOOSH) {
			return 0;
		}

		final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

		if (currentAnimationTick < 18) {
			return 0;
		}

		if (currentAnimationTick < 36) {
			final double openAnimationProgress = Math.min((currentAnimationTick - 18) / 6, 1);

			return easeOutExpo(openAnimationProgress);
		}

		final double closeAnimationProgress = Math.min((currentAnimationTick - 36) / (StargateAnimation.KAWOOSH.duration - 36), 1);

		return 1 - easeInExpo(closeAnimationProgress);

	}

	public double interpolatedAnimationTick(double partialTicks) {
		return lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;
	}

	public double interpolatedEventHorizonTick(double partialTicks) {
		return lasteventHorizonTick + (eventHorizonTick - lasteventHorizonTick) * partialTicks;
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
			final double openAnimationProgress = Math.min((currentAnimationTick - 6) / 24, 1);

			return 0.5 + easeOutExpo(openAnimationProgress) * 0.5;
		}

		if (currentAnimationTick < 48) {
			final double middleAnimationProgress = Math.min((currentAnimationTick - 24) / 24, 1);

			return 1 - middleAnimationProgress * 0.5;
		}

		final double closeAnimationProgress = Math.min((currentAnimationTick - 36) / (StargateAnimation.KAWOOSH.duration - 36), 1);

		return 0.5 + easeInExpo(closeAnimationProgress) * 0.5;
	}

	public boolean interpolatedShowEventHorizon(double partialTicks) {
		if (animation != StargateAnimation.KAWOOSH) {
			return true;
		}

		final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks - 12;

		return currentAnimationTick >= 6;
	}

	private int getCurrentSymbol() {
		return (int) Math.round((((currentAngle % 360) + 360) % 360) / symbolAngle);
	}

	@Override
	public void tick() {
		updateRotation();

		double angleDistance = angularDistance(targetAngle, currentAngle);
		if (angleDistance > 0.01) {
			return;
		}

		updateCommands();
		updateAnimation();
	}

	private void updateRotation() {
		lastAngle = currentAngle;

		double angleDistance = angularDistance(targetAngle, currentAngle);

		if (angleDistance < 0.01) {
			return;
		}

		double angleSpeed = Math.min(angleDistance, 1);

		if (ringDirection) {
			currentAngle -= angleSpeed;
		} else {
			currentAngle += angleSpeed;
		}
	}

	private void updateAnimation() {
		eventHorizon.applyRandomImpulse();
		eventHorizon.updateEventHorizon();

		lasteventHorizonTick = eventHorizonTick;
		eventHorizonTick++;

		if (animation == StargateAnimation.NONE) {
			return;
		}

		if (animation == StargateAnimation.ENCODE_CHEVRON) {
			boolean isLastChevronActive = !(state == StargateState.IDLE || state == StargateState.DIALLING);

			if (animationTick == 4) {
				SoundHelper.playShortSoundAt("stargate:stargate.milkyway.encode.lock", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			}
			if (animationTick == 26 && !isLastChevronActive) {
				SoundHelper.playShortSoundAt("stargate:stargate.milkyway.encode.slow", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			}
		}

		if (animation == StargateAnimation.FAST_ENCODE_CHEVRON) {
			if (animationTick == 4) {
				SoundHelper.playShortSoundAt("stargate:stargate.milkyway.encode.fast", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
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

			SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, x, y, z);
			playAnimation(StargateAnimation.ENCODE_CHEVRON);
		});
	}

	public void moveToSymbol(int symbol) {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}
			SoundHelper.playSingleSoundAt("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			targetAngle = symbol * symbolAngle;
		});
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

			SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, x, y, z);
			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}

	public void dial() {
		commandQueue.add(() -> {
			if (state != StargateState.AWAIT) {
				return;
			}

			state = StargateState.OPENING;

			SoundHelper.playShortSoundAt("stargate:stargate.milkyway.evenHorizon.open", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			playAnimation(StargateAnimation.KAWOOSH);
		});
	}

	public void autoDial() {
//		moveToSymbol(26);
//		encode();
//		moveToSymbol(6);
//		encode();
//		moveToSymbol(14);
//		encode();
//		moveToSymbol(31);
//		encode();
//		moveToSymbol(11);
//		encode();
//		moveToSymbol(29);
//		encode();
//		moveToSymbol(0);
//		encode();

		fastEncode(26);
		fastEncode(6);
		fastEncode(14);
		fastEncode(31);
		fastEncode(11);
		fastEncode(29);
		fastEncode(0);
		dial();
	}

	public Direction getOrientation() {
		return orientation;
	}

	public void setOrientation(Direction orientation) {
		this.orientation = orientation;
	}
}
