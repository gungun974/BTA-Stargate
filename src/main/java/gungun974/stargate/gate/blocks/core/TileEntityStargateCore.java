package gungun974.stargate.gate.blocks.core;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.StargateBlocks;
import gungun974.stargate.StargateMod;
import gungun974.stargate.core.SoundHelper;
import gungun974.stargate.core.StargateAddress;
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
	private double currentAngle = 0;
	private double lastAngle = 0;
	private double targetAngle = 0;
	private boolean ringDirection = false;
	private boolean assembled = false;
	private StargateAnimation animation = StargateAnimation.NONE;
	private int animationTick = 0;
	private int lastAnimationTick = 0;

	private boolean shouldDial = false;
	private short currentDialingAddressSize = 0;
	private int[] currentDialingAddress = new int[9];

	@Nullable
	public static TileEntityStargateCore findStargateCore(WorldSource worldSource, int x, int y, int z) {
		{
			TileEntity tileEntity = worldSource.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
		}

		for (int i = -2; i <= 2; i++) {
			if (i != 0) {
				TileEntity tileEntity = worldSource.getTileEntity(x, y, z + i);
				if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
			}
			{
				TileEntity tileEntity = worldSource.getTileEntity(x, y - 4, z + i);
				if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
			}

			if (i != 0) {
				TileEntity tileEntity = worldSource.getTileEntity(x + i, y, z);
				if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
			}
			{
				TileEntity tileEntity = worldSource.getTileEntity(x + i, y - 4, z);
				if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
			}
		}

		for (int yy = y - 1; yy >= y - 3; yy--) {
			TileEntity tileEntity = worldSource.getTileEntity(x, yy, z + 2);
			if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

			tileEntity = worldSource.getTileEntity(x, yy, z - 2);
			if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

			tileEntity = worldSource.getTileEntity(x + 2, yy, z);
			if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;

			tileEntity = worldSource.getTileEntity(x - 2, yy, z);
			if (tileEntity instanceof TileEntityStargateCore) return (TileEntityStargateCore) tileEntity;
		}

		return null;
	}

	public static double angularDistance(double angle1, double angle2) {
		double diff = Math.abs(angle1 - angle2) % 360.0;
		return diff > 180.0 ? 360.0 - diff : diff;
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
		World world = worldObj;
		if (world == null) {
			return false;
		}

		Direction direction = BlockLogicStargateCore.getDirectionFromMeta(
			world.getBlockMetadata(x, y, z)
		);

		for (int j = 0; j < 5; j++) {
			for (int i = -2; i < 3; i++) {
				if (i == 0 && j == 0) {
					continue;
				}

				if (i != -2 && i != 2 && j > 0 && j < 4) {
					continue;
				}

				int id = world.getBlockId(x + direction.getOffsetZ() * i, y + j, z + direction.getOffsetX() * i);

				if (id != StargateBlocks.STARGATE_RING.id()) {
					return false;
				}

				int meta = world.getBlockMetadata(x + direction.getOffsetZ() * i, y + j, z + direction.getOffsetX() * i);

				if (meta != ((i + (j + 1) & 1) & 1)) {
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

		super.readFromNBT(compoundTag);
	}

	@Override
	public void writeToNBT(CompoundTag compoundTag) {
		compoundTag.putBoolean("Assembled", assembled);

		super.writeToNBT(compoundTag);
	}

	public double interpolatedRingAngle(double partialTicks) {
		return lastAngle + (currentAngle - lastAngle) * partialTicks;
	}

	public boolean interpolatedChevronActive(int chevron, double partialTicks) {
		int currentEncodedChevron = 6 < currentDialingAddressSize ? currentDialingAddressSize + 1 : currentDialingAddressSize;

		currentEncodedChevron = shouldDial && currentDialingAddressSize != 9 ? currentEncodedChevron - 1 : currentEncodedChevron;

		if (animation == StargateAnimation.ENCODE_CHEVRON) {
			if (chevron < currentEncodedChevron - 1 && chevron != 6) {
				return true;
			}

			if (chevron != currentEncodedChevron - 1 && chevron != 6) {
				return false;
			}

			final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

			if (chevron == 6) {
				if (!shouldDial && currentAnimationTick > 38.67) {
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

			if (chevron == 6 && !shouldDial) {
				return false;
			}

			return currentAnimationTick > (double) StargateAnimation.FAST_ENCODE_CHEVRON.duration / 2;
		} else {
			if (chevron == 6) {
				return shouldDial;
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
		if (animation == StargateAnimation.NONE) {
			return;
		}

		if (animation == StargateAnimation.ENCODE_CHEVRON) {
			if (animationTick == 4) {
				SoundHelper.playShortSoundAt("stargate:stargate.milkyway.encode.lock", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			}
			if (animationTick == 26 && !this.shouldDial) {
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
			if (shouldDial) {
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
				this.shouldDial = true;
			}

			SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, x, y, z);
			playAnimation(StargateAnimation.ENCODE_CHEVRON);
		});
	}

	public void moveToSymbol(int symbol) {
		commandQueue.add(() -> {
			if (shouldDial) {
				return;
			}
			SoundHelper.playSingleSoundAt("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			targetAngle = symbol * symbolAngle;
		});
	}

	public void fastEncode(int symbol) {
		commandQueue.add(() -> {
			if (shouldDial) {
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
				this.shouldDial = true;
			}

			SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, x, y, z);
			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}

	public void autoDial() {
		moveToSymbol(26);
		encode();
		moveToSymbol(6);
		encode();
		moveToSymbol(14);
		encode();
		moveToSymbol(31);
		encode();
		moveToSymbol(11);
		encode();
		moveToSymbol(29);
		encode();
		moveToSymbol(0);
		encode();
	}
}
