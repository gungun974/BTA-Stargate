package gungun974.stargate.gate.blocks.core;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.StargateBlocks;
import gungun974.stargate.StargateMod;
import gungun974.stargate.core.StargateAddress;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import javax.annotation.Nullable;

public class TileEntityStargateCore extends TileEntity {
	public final static double symbolAngle = 360.0 / StargateAddress.NUMBER_OF_SYMBOL;

	private double currentAngle = 0;
	private double lastAngle = 0;

	private double targetAngle = 0;

	private boolean ringDirection = false;

	private boolean assembled = false;

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

	private int getCurrentChevron() {
		return (int) (Math.abs(currentAngle % 360) / symbolAngle);
	}

	@Override
	public void tick() {
		lastAngle = currentAngle;

		if (currentAngle == targetAngle) {
			return;
		}

		double angleDistance = angularDistance(targetAngle, currentAngle);

		double angleSpeed = Math.min(angleDistance, 1);

		if (ringDirection) {
			currentAngle -= angleSpeed;
		} else {
			currentAngle += angleSpeed;
		}
	}

	public void encode() {
		ringDirection = !ringDirection;
	}

	public void autoDial() {
		targetAngle = 38 * symbolAngle;
		StargateMod.LOGGER.info("{}", targetAngle);
	}
}
