package gungun974.stargate.gate.blocks.core;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.StargateBlocks;
import gungun974.stargate.StargateMod;
import gungun974.stargate.core.*;
import gungun974.stargate.network.server.PlayerEnterStargateMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.block.*;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.*;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.world.WorldServer;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.*;

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
	private boolean lastEventHorizonNoise = false;
	private boolean assembled = false;
	private Direction direction = Direction.NORTH;
	private Direction orientation = Direction.NORTH;
	private StargateAnimation lastAnimation = StargateAnimation.NONE;
	private StargateAnimation animation = StargateAnimation.NONE;
	private int animationTick = 0;
	private int lastAnimationTick = 0;
	private int eventHorizonTick = 0;
	private int lastEventHorizonTick = 0;
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

	@Environment(EnvType.SERVER)
	private static void serverTeleport(Player rawPlayer, double newX, double newY, double newZ, float newYaw, float newPitch, int dimension) {
		PlayerServer player = (PlayerServer) rawPlayer;

		if (player.dimension == dimension) {
			player.teleport(newX, newY, newZ, newYaw, newPitch);
			return;
		}

		MinecraftServer ms = MinecraftServer.getInstance();

		WorldServer worldServerOrigin = ms.getDimensionWorld(player.dimension);
		player.dimension = dimension;
		WorldServer worldServerDestination = ms.getDimensionWorld(dimension);
		player.playerNetServerHandler.sendPacket(new PacketRespawn((byte) dimension, (byte) Registries.WORLD_TYPES.getNumericIdOfItem(worldServerDestination.worldType)));
		worldServerOrigin.removePlayer(player);
		player.removed = false;
		player.teleport(newX, newY, newZ, newYaw, newPitch);

		player.dimensionEnterCoordinate = new ChunkCoordinates((int) newX, (int) newY, (int) newZ);

		if (player.isAlive()) {
			worldServerOrigin.updateEntityWithOptionalForce(player, false);
		}

		if (player.isAlive()) {
			worldServerDestination.entityJoinedWorld(player);
			player.teleport(newX, newY, newZ, newYaw, newPitch);
			worldServerDestination.updateEntityWithOptionalForce(player, false);
		}

		ms.playerList.syncPlayerDimension(player);
		player.playerNetServerHandler.teleportAndRotate(newX, newY, newZ, newYaw, newPitch);
		ms.playerList.sendPacketToAllPlayers(new PacketPlayerGamemode(player.id, player.gamemode.getId()));
		player.setWorld(worldServerDestination);
		ms.playerList.setTime(player, worldServerDestination);
		ms.playerList.initializePlayerObject(player);
		player.playerNetServerHandler.sendPacket(new PacketGameRule(ms.getDimensionWorld(0).getLevelData().getGameRules()));
	}

	@Environment(EnvType.CLIENT)
	static private void singlePlayerTeleport(Player player, double newX, double newY, double newZ, float newYaw, float newPitch, int dimension) {
		if (player.dimension == dimension) {
			player.absMoveTo(newX, newY, newZ, newYaw, newPitch);
			return;
		}

		Dimension lastDim = Dimension.getDimensionList().get(player.dimension);
		Dimension newDim = Dimension.getDimensionList().get(dimension);

		StargateMod.LOGGER.info("Switching to dimension \"{}\"!!", newDim.getTranslatedName());

		player.dimension = dimension;

		Minecraft mc = Minecraft.getMinecraft();

		mc.currentWorld.setEntityDead(player);
		mc.thePlayer.removed = false;
		player.absMoveTo(newX, newY, newZ, newYaw, newPitch);
		if (player.isAlive()) {
			mc.currentWorld.updateEntityWithOptionalForce(player, false);
		}

		WorldClient world = new WorldClient(mc.currentWorld, newDim);
		if (newDim == lastDim.homeDim) {
			mc.changeWorld(world, "Leaving " + lastDim.getTranslatedName(), player);
		} else {
			mc.changeWorld(world, "Entering " + newDim.getTranslatedName(), player);
		}

		player.world = mc.currentWorld;
		if (player.isAlive()) {
			player.absMoveTo(newX, newY, newZ, newYaw, newPitch);
			mc.currentWorld.updateEntityWithOptionalForce(player, false);
		}

	}

	public StargateState getState() {
		return state;
	}

	@Override
	public void invalidate() {
		stopSoundAtCenter("stargate:stargate.milkyway.roll");
		stopSoundAtCenter("stargate:stargate.eventHorizon");
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
		return direction;
	}

	private boolean isValidStructure() {
		World world = worldObj;

		if (world == null) {
			direction = Direction.NORTH;
		} else {
			direction = BlockLogicStargateCore.getDirectionFromMeta(world.getBlockMetadata(x, y, z)).getOpposite();
		}

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

			stopSoundAtCenter("stargate:stargate.milkyway.roll");
			StargateSessionManager.getInstance().removeSession(this);
		}
	}

	@Override
	public void readFromNBT(CompoundTag compoundTag) {
		assembled = compoundTag.getBooleanOrDefault("Assembled", false);
		direction = Direction.values()[compoundTag.getIntegerOrDefault("Direction", Direction.NORTH.ordinal())];
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

		super.writeToNBT(compoundTag);
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
			final double currentAnimationTick = lastAnimationTick + (animationTick - lastAnimationTick) * partialTicks;

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

	private int getCurrentSymbol() {
		return (int) Math.round((((currentAngle % 360) + 360) % 360) / symbolAngle);
	}

	public AABB getDetectionBox() {
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
		if (state == StargateState.CONNECTED && worldObj != null) {
			this.teleportBlocks();
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
						if (entity instanceof Player && EnvironmentHelper.isClientWorld()) {
							NetworkHandler.sendToServer(new PlayerEnterStargateMessage(x, y, z, worldObj.dimension.id, entity.xd, entity.yd, entity.zd, d0 < 0 && d1 > 0));
						} else {
							StargateSession session = StargateSessionManager.getInstance().getSession(this);

							if (session != null) {
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
									StargateSessionManager.getInstance().endSession(this);
								}
							}
						}
					}
				}
			}
		}

		if (!EnvironmentHelper.isClientWorld()) {
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

					if (worldObj != null) {
						worldObj.markBlockNeedsUpdate(x, y, z);
					}
				}
			}

			if (state != StargateState.CONNECTED && session != null && animation != StargateAnimation.KAWOOSH) {
				state = StargateState.CONNECTED;

				if (worldObj != null) {
					worldObj.markBlockNeedsUpdate(x, y, z);
				}
			}

			if (state == StargateState.CONNECTED && session != null && animation != StargateAnimation.KAWOOSH) {
				boolean needUpdate = currentDialingAddressSize != session.dialingAddressSize;
				currentDialingAddressSize = session.dialingAddressSize;

				if (needUpdate && worldObj != null) {
					worldObj.markBlockNeedsUpdate(x, y, z);
				}
			}

			if ((state == StargateState.CONNECTED || state == StargateState.OPENING) && animation == StargateAnimation.CLOSING) {
				animation = StargateAnimation.NONE;
				animationTick = 0;
				lastAnimationTick = 0;

				if (worldObj != null) {
					worldObj.markBlockNeedsUpdate(x, y, z);
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

	private void stopSoundAtCenter(String name) {
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

		if (EnvironmentHelper.isServerEnvironment() && entity instanceof Player) {
			serverTeleport((Player) entity, newX, newY, newZ, newYaw, newPitch, session.destinationDim);
		} else if (EnvironmentHelper.isSinglePlayer() && entity instanceof Player) {
			singlePlayerTeleport((Player) entity, newX, newY, newZ, newYaw, newPitch, session.destinationDim);
		} else {
			entity.absMoveTo(newX, newY, newZ, newYaw, newPitch);
			StargateDematerializedManager.getInstance().dematerializeEntity(session.destinationX, session.destinationY, session.destinationZ, session.destinationDim, entity);
		}
	}

	private void teleportBlocks() {
		if (EnvironmentHelper.isClientWorld()) {
			return;
		}

		if (worldObj == null) {
			return;
		}

		StargateSession session = StargateSessionManager.getInstance().getSession(this);

		if (session == null) {
			return;
		}

		if (session.destinationX == x && session.destinationY == y && session.destinationZ == z) {
			return;
		}


		Direction originDirection = getDirection();
		Direction originOrientation = getOrientation();

		Direction destinationDirection = session.destinationDirection;
		Direction destinationOrientation = session.destinationOrientation;

		int originX, originY, originZ;
		int destinationX, destinationY, destinationZ;

		if (originOrientation == Direction.NORTH) {
			originX = x;
			originY = y + 3;
			originZ = z;
		} else {
			originX = x + originDirection.getOffsetX() * 3;
			originY = y;
			originZ = z + originDirection.getOffsetZ() * 3;

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

				if (worldObj.isAirBlock(x, y, z)) {
					continue;
				}

				int id = worldObj.getBlockId(x, y, z);

				int meta = worldObj.getBlockMetadata(x, y, z);

				if (id == Blocks.PISTON_MOVING.id()) {
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

				@Nullable Block<?> block = worldObj.getBlock(x, y, z);

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

					worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
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

					worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
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

					worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
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

					worldObj.setBlockAndMetadataRaw(x, y, z, id, newMeta);
				}

				StargateDematerializedManager.getInstance().dematerializeBlock(session.destinationX, session.destinationY, session.destinationZ, session.destinationDim, worldObj, x, y, z, newX, newY, newZ);
				StargateSessionManager.getInstance().endSession(this);
			}
		}
	}

	private void updateRotation() {
		if (!lastRingMove && ringMove) {
			playSoundAtCenter("stargate:stargate.milkyway.roll", SoundCategory.WORLD_SOUNDS, 1.0f, 1.0f, false);
		}

		if (lastRingMove && !ringMove) {
			stopSoundAtCenter("stargate:stargate.milkyway.roll");
		}

		lastRingMove = ringMove;
		lastAngle = currentAngle;

		double angleDistance = angularDistance(targetAngle, currentAngle);

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
				SoundHelper.playShortSoundAt("stargate:stargate.evenHorizon.open", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
			}
			if (animation == StargateAnimation.CLOSING) {
				SoundHelper.playShortSoundAt("stargate:stargate.evenHorizon.close", SoundCategory.WORLD_SOUNDS, x, y, z, 1.0f, 1.0f);
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

			stopSoundAtCenter("stargate:stargate.milkyway.roll");
			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}

	public void dial() {
		commandQueue.add(() -> {
			if (state != StargateState.AWAIT) {
				return;
			}

			if (currentDialingAddressSize < 7) {
				cancelDial(x, y, z);
				return;
			}

			if (currentDialingAddressSize == 7) {
				int[] originAddress = this.getAddress().encodeAddress();

				currentDialingAddress[6] = originAddress[6];
				currentDialingAddress[7] = -1;
				currentDialingAddress[8] = 0;
			} else if (currentDialingAddressSize == 8) {
				currentDialingAddress[7] = -1;
				currentDialingAddress[8] = 0;
			}

			StargateAddress destinationAddress = StargateAddress.createAddressFromEncoded(currentDialingAddress);

			if (destinationAddress == null) {
				cancelDial(x, y, z);
				return;
			}

			List<StargateLocation> locations = new ArrayList<>();

			for (int cx = destinationAddress.getStartChunkX(); cx <= destinationAddress.getEndChunkX(); cx++) {
				for (int cz = destinationAddress.getStartChunkZ(); cz <= destinationAddress.getEndChunkZ(); cz++) {
					Chunk chunk = StargateChunkLoader.loadChunk(worldObj, destinationAddress.dim, cx, cz);

					if (chunk == null) {
						continue;
					}

					for (Map.Entry<ChunkPosition, TileEntity> chunkPositionTileEntityEntry : chunk.tileEntityMap.entrySet()) {
						TileEntity tileEntity = chunkPositionTileEntityEntry.getValue();

						if (tileEntity instanceof TileEntityStargateCore) {
							TileEntityStargateCore destinationGate = (TileEntityStargateCore) tileEntity;

							if (this != destinationGate && destinationGate.isAssembled()) {
								locations.add(new StargateLocation(
									tileEntity.x,
									tileEntity.y,
									tileEntity.z,
									destinationAddress.dim,
									destinationAddress
								));
							}
						}
					}
				}
			}

			if (locations.isEmpty()) {
				cancelDial(x, y, z);
				return;
			}

			StargateLocation target = locations.get(0);
			int closestDistance = Integer.MAX_VALUE;

			for (StargateLocation location : locations) {
				int diffX = destinationAddress.getBlockX() - location.x;
				int diffY = 255 - location.y;
				int diffZ = destinationAddress.getBlockZ() - location.z;
				int distance = diffX * diffX + diffY * diffY + diffZ * diffZ;

				if (distance < closestDistance) {
					target = location;
					closestDistance = distance;
				}
			}

			Chunk chunk = StargateChunkLoader.loadChunk(worldObj, target.dim, Math.floorDiv(target.x, 16), Math.floorDiv(target.z, 16));

			if (chunk == null) {
				cancelDial(x, y, z);
				return;
			}

			TileEntityStargateCore destinationGate = (TileEntityStargateCore) chunk.getTileEntity(target.x & 15, target.y, target.z & 15);

			StargateSession currentPresentSession = StargateSessionManager.getInstance().getSession(destinationGate);

			if (currentPresentSession != null) {
				cancelDial(x, y, z);
				return;
			}

			StargateSessionManager.getInstance().createSession(this, destinationGate, currentDialingAddressSize);

			destinationGate.openGate();
			openGate();
		});
	}

	private void cancelDial(int x, int y, int z) {
		state = StargateState.IDLE;
		currentDialingAddressSize = 0;

		if (worldObj != null) {
			worldObj.markBlockNeedsUpdate(x, y, z);
		}
	}

	private void openGate() {
		state = StargateState.OPENING;

		playAnimation(StargateAnimation.KAWOOSH);
	}

	public void closeGate() {
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

		if (x == 9989 && y == 7 && z == 9) {
			fastEncode(27);
			fastEncode(31);
			fastEncode(36);
			fastEncode(13);
			fastEncode(34);
			fastEncode(9);
			fastEncode(1);
			fastEncode(38);
			fastEncode(0);
		} else {
			fastEncode(17);
			fastEncode(8);
			fastEncode(31);
			fastEncode(27);
			fastEncode(26);
			fastEncode(21);
			//fastEncode(1);
			//fastEncode(9);
			fastEncode(0);
		}

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

	public int getLightmap() {
		if (worldObj == null) {
			return LightmapHelper.getLightmapCoord(15, 15);
		}

		Direction originDirection = getDirection();
		Direction originOrientation = getOrientation();

		int originX, originY, originZ;

		if (originOrientation == Direction.NORTH) {
			originX = x;
			originY = y + 3;
			originZ = z;
		} else {
			originX = x + originDirection.getOffsetX() * 3;
			originY = y;
			originZ = z + originDirection.getOffsetZ() * 3;

			if (originDirection.getOffsetX() < 0) {
				originX += 1;
				originZ += 1;
			}

			if (originDirection.getOffsetZ() < 0) {
				originZ += 1;
				originX += 1;
			}
		}

		return worldObj.getLightmapCoord(originX, originY, originZ, worldObj.getBlockLightValue(originX, originY, originZ));
	}
}
