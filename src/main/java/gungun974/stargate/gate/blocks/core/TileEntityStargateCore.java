package gungun974.stargate.gate.blocks.core;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.StargateBlocks;
import gungun974.stargate.StargateMod;
import gungun974.stargate.core.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.Chunk;
import turniplabs.halplibe.helper.EnvironmentHelper;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class TileEntityStargateCore extends TileEntity {
	public final static double symbolAngle = 360.0 / StargateAddress.NUMBER_OF_SYMBOL;
	private final Queue<Runnable> commandQueue = new ArrayDeque<>();
	public StargateEventHorizon eventHorizon = new StargateEventHorizon();
	private double currentAngle = 0;
	private double lastAngle = 0;
	private double targetAngle = 0;
	private boolean ringDirection = false;
	private boolean lastRingMove = false;
	private boolean ringMove = false;
	private boolean lastEventHoirzonNoise = false;
	private boolean eventHoirzonNoise = false;
	private boolean assembled = false;
	private Direction orientation = Direction.NORTH;
	private StargateAnimation lastAnimation = StargateAnimation.NONE;
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
		SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", x, y, z);
		SoundHelper.stopSingleSoundAt("stargate:stargate.eventHorizon", x, y, z);
		StargateSessionManager.getInstance().removeSession(this);
		super.invalidate();
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
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

			SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", x, y, z);
			StargateSessionManager.getInstance().removeSession(this);
		}
	}

	@Override
	public void readFromNBT(CompoundTag compoundTag) {
		assembled = compoundTag.getBooleanOrDefault("Assembled", false);
		orientation = Direction.values()[compoundTag.getIntegerOrDefault("Orientation", Direction.NORTH.ordinal())];

		state = StargateState.values()[compoundTag.getIntegerOrDefault("State", StargateAnimation.NONE.ordinal())];
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

		super.readFromNBT(compoundTag);
	}

	@Override
	public void writeToNBT(CompoundTag compoundTag) {
		compoundTag.putBoolean("Assembled", assembled);
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

	private AABB getDetectionBox() {
		Direction orientation = getOrientation();

		double offset = 3.0;

		Direction direction = getDirection();
		double x1 = x + direction.getOffsetZ() * -3 + direction.getOffsetX() * offset;
		double y1 = y;
		double z1 = z + direction.getOffsetX() * -3 + direction.getOffsetZ() * offset;
		double x2 = x + direction.getOffsetZ() * 3 - direction.getOffsetX() * offset;
		double y2 = y + 5;
		double z2 = z + direction.getOffsetX() * 3 - direction.getOffsetZ() * offset;

		if (orientation != Direction.NORTH) {
			x1 = x - direction.getOffsetZ() * 3;
			y1 = y - offset;
			z1 = z - direction.getOffsetX() * 3;

			x2 = x + direction.getOffsetX() * 6 + direction.getOffsetZ() * 3;
			y2 = y + offset;
			z2 = z + direction.getOffsetX() * 3 + direction.getOffsetZ() * 6;
		}

		double minX = Math.min(x1, x2);
		double minY = Math.min(y1, y2);
		double minZ = Math.min(z1, z2);
		double maxX = Math.max(x1, x2) + (double) 1.0F;
		double maxY = Math.max(y1, y2) + (double) 1.0F;
		double maxZ = Math.max(z1, z2) + (double) 1.0F;
		return AABB.getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ);
	}


	@Override
	public void tick() {
		if (!EnvironmentHelper.isClientWorld()) {
			StargateSession session = StargateSessionManager.getInstance().getSession(this);

			if (state == StargateState.CONNECTED && session != null && worldObj != null) {

				Direction orientation = getOrientation();

				Direction direction = getDirection();

				AABB detectionBox = this.getDetectionBox();
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, detectionBox);

				for (Entity entity : list) {
					double dx = direction.getOffsetX();
					double dz = direction.getOffsetZ();

					double cx = x + 0.5;
					double cy = y + 3.5;
					double cz = z + 0.5;

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
						cx = x + direction.getOffsetZ() * 0.5 + direction.getOffsetX() * 3.5;
						cy = y + orientation.getOffsetY() * 0.5;
						cz = z + direction.getOffsetX() * 0.5 + direction.getOffsetZ() * 3.5;

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
							if (
								(session.destinationX == x && session.destinationY == y && session.destinationZ == z) ||
									d0 > 0 && d1 < 0
							) {
								SoundHelper.playShortSoundAt("stargate:stargate.eventHorizon.enter", SoundCategory.WORLD_SOUNDS, (float) entity.x, (float) entity.y, (float) entity.z, 1.0f, 1.0f);
								if (entity instanceof Player) {
									((Player) entity).killPlayer();
								} else {
									entity.remove();
								}
							} else if (d0 < 0 && d1 > 0) {
								this.teleportEntity(entity, session);
							}
						}
					}
				}

			}

			if (
				(
					(state == StargateState.OPENING && animation != StargateAnimation.KAWOOSH) ||
						state == StargateState.CONNECTED
				) && session == null
			) {
				state = StargateState.IDLE;
				currentDialingAddressSize = 0;

				if (worldObj != null) {
					worldObj.markBlockNeedsUpdate(x, y, z);
				}
			}

			if (state != StargateState.CONNECTED && session != null && animation != StargateAnimation.KAWOOSH) {
				state = StargateState.CONNECTED;
				currentDialingAddressSize = session.dialingAddressSize;
				currentDialingAddress = session.originAddress.encodeAddress();

				if (worldObj != null) {
					worldObj.markBlockNeedsUpdate(x, y, z);
				}
			}
		}

		eventHoirzonNoise = state == StargateState.CONNECTED;

		if (!lastEventHoirzonNoise && eventHoirzonNoise) {
			playSoundAtCenter("stargate:stargate.eventHorizon", SoundCategory.WORLD_SOUNDS, 1.0f, 1.0f, true);
		}

		if (lastEventHoirzonNoise && !eventHoirzonNoise) {
			SoundHelper.stopSingleSoundAt("stargate:stargate.eventHorizon", x, y, z);
		}

		lastEventHoirzonNoise = eventHoirzonNoise;

		updateRotation();

		if (ringMove) {
			return;
		}

		updateCommands();
		updateAnimation();
	}

	private void playSoundAtCenter(String name, SoundCategory soundCategory, float volume, float pitch, boolean loop) {
		Direction direction = getDirection();
		Direction orientation = getOrientation();

		float centerX, centerY, centerZ;

		if (orientation == Direction.NORTH) {
			centerX = x + 0.5f;
			centerY = y + 3.5f;
			centerZ = z + 0.5f;
		} else {
			centerX = x + direction.getOffsetZ() * 0.5f + direction.getOffsetX() * 3.5f;
			centerY = y + orientation.getOffsetY() * 0.5f;
			centerZ = z + direction.getOffsetX() * 0.5f + direction.getOffsetZ() * 3.5f;

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

	private void teleportEntity(Entity entity, StargateSession session) {
		Direction originDirection = getDirection();
		Direction originOrientation = getOrientation();

		Direction destinationDirection = session.destinationDirection;
		Direction destinationOrientation = session.destinationOrientation;

		double originX, originY, originZ;
		double destinationX, destinationY, destinationZ;

		if (originOrientation == Direction.NORTH) {
			originX = x + 0.5;
			originY = y + 3.5;
			originZ = z + 0.5;
		} else {
			originX = x + originDirection.getOffsetZ() * 0.5 + originDirection.getOffsetX() * 3.5;
			originY = y + originOrientation.getOffsetY() * 0.5;
			originZ = z + originDirection.getOffsetX() * 0.5 + originDirection.getOffsetZ() * 3.5;

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

		entity.absMoveTo(newX, newY, newZ, newYaw, newPitch);
	}


	private void updateRotation() {
		if (!lastRingMove && ringMove) {
			playSoundAtCenter("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, 1.0f, 1.0f, false);
		}

		if (lastRingMove && !ringMove) {
			SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", x, y, z);
		}

		lastRingMove = ringMove;
		lastAngle = currentAngle;

		double angleDistance = angularDistance(targetAngle, currentAngle);

		if (angleDistance < 0.01) {
			ringMove = false;
			return;
		}

		ringMove = true;

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

		if (lastAnimation != animation) {
			if (animation == StargateAnimation.KAWOOSH) {
				SoundHelper.playShortSoundAt("stargate:stargate.evenHorizon.open", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			}
		}

		lastAnimation = animation;

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

		if (worldObj != null) {
			worldObj.markBlockNeedsUpdate(x, y, z);
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
			targetAngle = symbol * symbolAngle;

			if (worldObj != null) {
				worldObj.markBlockNeedsUpdate(x, y, z);
			}
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

			SoundHelper.stopSingleSoundAt("stargate:stargate.milkyway.roll", x, y, z);
			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}

	public void dial() {
		commandQueue.add(() -> {
			if (state != StargateState.AWAIT) {
				return;
			}

			int x = -23;
			int y = 7;
			int z = -14;
			int dim = 0;

			if (EnvironmentHelper.isSinglePlayer()) {
				x = -3;
				y = 7;
				z = 6;

				x = 10;
				y = 7;
				z = 39;
			}

			Chunk chunk = StargateChunkLoader.loadChunk(worldObj, dim, Math.floorDiv(x, 16), Math.floorDiv(z, 16));

			if (chunk == null) {
				state = StargateState.IDLE;
				return;
			}

			TileEntityStargateCore destinationGate = (TileEntityStargateCore) chunk.getTileEntity(x & 15, y, z & 15);

			if (this == destinationGate || destinationGate == null || !destinationGate.isAssembled()) {
				state = StargateState.IDLE;
				return;
			}

			StargateSessionManager.getInstance().createSession(this, destinationGate, currentDialingAddressSize);

			destinationGate.openGate();
			openGate();
		});
	}

	private void openGate() {
		state = StargateState.OPENING;

		playAnimation(StargateAnimation.KAWOOSH);
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

		StargateMod.LOGGER.info("State: {}", state);

		StargateAddress stargateAddress = StargateAddress.createAddressFromBlock(x, y, worldObj.dimension.id);

		StargateMod.LOGGER.info("Gate address : {}", stargateAddress.encodeAddress());
	}

	public Direction getOrientation() {
		return orientation;
	}

	public StargateAddress getAddress() {
		return StargateAddress.createAddressFromBlock(x, y, worldObj.dimension.id);
	}
}
