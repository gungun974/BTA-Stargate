package gungun974.stargate.core;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import gungun974.stargate.StargateMod;
import gungun974.stargate.gate.components.StargateComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.PacketGameRule;
import net.minecraft.core.net.packet.PacketPlayerGamemode;
import net.minecraft.core.net.packet.PacketRespawn;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.world.WorldServer;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.*;

public class StargateDematerializedManager {
	private static StargateDematerializedManager instance;

	private final List<StargateDematerializedEntity> dematerializedEntities = new ArrayList<>();
	private final List<StargateDematerializedBlock> dematerializedBlocks = new ArrayList<>();
	private Map<String, Entity> teleportedPlayers = new HashMap<>();

	private StargateDematerializedManager() {
	}

	public static synchronized StargateDematerializedManager getInstance() {
		if (instance == null) {
			instance = new StargateDematerializedManager();
		}

		return instance;
	}

	@Environment(EnvType.SERVER)
	private static Player serverTeleport(Player rawPlayer, double newX, double newY, double newZ, float newYaw, float newPitch, int dimension) {
		PlayerServer player = (PlayerServer) rawPlayer;

		if (player.dimension == dimension) {
			player.teleport(newX, newY, newZ, newYaw, newPitch);
			return player;
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

		return player;
	}

	@Environment(EnvType.CLIENT)
	static private Player singlePlayerTeleport(Player player, double newX, double newY, double newZ, float newYaw, float newPitch, int dimension) {
		if (player.dimension == dimension) {
			player.absMoveTo(newX, newY, newZ, newYaw, newPitch);
			return player;
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

		return player;
	}

	public @NotNull CompoundTag createNBTData() {
		CompoundTag rootTag = new CompoundTag();

		CompoundTag dematerializedEntitiesTag = new CompoundTag();

		for (int i = 0; i < dematerializedEntities.size(); i++) {
			StargateDematerializedEntity dematerializedEntity = dematerializedEntities.get(i);

			CompoundTag dematerializedEntityTag = new CompoundTag();

			dematerializedEntityTag.putInt("DestinationX", dematerializedEntity.destinationX);
			dematerializedEntityTag.putInt("DestinationY", dematerializedEntity.destinationY);
			dematerializedEntityTag.putInt("DestinationZ", dematerializedEntity.destinationZ);
			dematerializedEntityTag.putInt("DestinationDim", dematerializedEntity.destinationDim);

			dematerializedEntityTag.putCompound("Entity", dematerializedEntity.dematerializedData);

			dematerializedEntityTag.putString("EntityId", dematerializedEntity.entityId);
			dematerializedEntityTag.putString("PassengerId", dematerializedEntity.passengerId);

			dematerializedEntitiesTag.put(String.valueOf(i), dematerializedEntityTag);
		}

		rootTag.putCompound("Entities", dematerializedEntitiesTag);

		CompoundTag dematerializedBlocksTag = new CompoundTag();

		for (int i = 0; i < dematerializedBlocks.size(); i++) {
			StargateDematerializedBlock dematerializedBlock = dematerializedBlocks.get(i);

			CompoundTag dematerializedBlockTag = new CompoundTag();

			dematerializedBlockTag.putInt("DestinationX", dematerializedBlock.destinationX);
			dematerializedBlockTag.putInt("DestinationY", dematerializedBlock.destinationY);
			dematerializedBlockTag.putInt("DestinationZ", dematerializedBlock.destinationZ);
			dematerializedBlockTag.putInt("DestinationDim", dematerializedBlock.destinationDim);

			dematerializedBlockTag.putInt("Id", dematerializedBlock.dematerializedId);
			dematerializedBlockTag.putInt("Meta", dematerializedBlock.dematerializedMeta);

			if (dematerializedBlock.dematerializedTile != null) {
				CompoundTag dematerializedData = new CompoundTag();

				dematerializedBlock.dematerializedTile.writeToNBT(dematerializedData);

				dematerializedBlockTag.putCompound("Tile", dematerializedData);
			}

			dematerializedBlockTag.putInt("X", dematerializedBlock.dematerializedX);
			dematerializedBlockTag.putInt("Y", dematerializedBlock.dematerializedY);
			dematerializedBlockTag.putInt("Z", dematerializedBlock.dematerializedZ);

			dematerializedBlocksTag.put(String.valueOf(i), dematerializedBlockTag);
		}

		rootTag.putCompound("Blocks", dematerializedBlocksTag);

		return rootTag;
	}

	public void loadNBTData(CompoundTag rootTag) {
		dematerializedEntities.clear();

		try {
			CompoundTag dematerializedEntitiesTag = rootTag.getCompound("Entities");

			for (Map.Entry<String, Tag<?>> entry : dematerializedEntitiesTag.getValue().entrySet()) {
				try {
					final Tag<?> tag = entry.getValue();
					if (!(tag instanceof CompoundTag)) {
						continue;
					}

					final CompoundTag dematerializedEntityTag = (CompoundTag) tag;

					dematerializedEntities.add(new StargateDematerializedEntity(
						dematerializedEntityTag.getInteger("DestinationX"),
						dematerializedEntityTag.getInteger("DestinationY"),
						dematerializedEntityTag.getInteger("DestinationZ"),
						dematerializedEntityTag.getInteger("DestinationDim"),
						dematerializedEntityTag.getCompound("Entity"),
						dematerializedEntityTag.getString("EntityId"),
						dematerializedEntityTag.getString("PassengerId")
					));
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}

		dematerializedBlocks.clear();

		try {
			CompoundTag dematerializedBlocksTag = rootTag.getCompound("Blocks");

			for (Map.Entry<String, Tag<?>> entry : dematerializedBlocksTag.getValue().entrySet()) {
				try {
					final Tag<?> tag = entry.getValue();
					if (!(tag instanceof CompoundTag)) {
						continue;
					}

					final CompoundTag dematerializedBlockTag = (CompoundTag) tag;

					TileEntity tileEntity = null;

					if (dematerializedBlockTag.containsKey("Tile")) {
						tileEntity = TileEntityDispatcher.createAndLoadEntity(
							dematerializedBlockTag.getCompound("Tile")
						);
					}

					dematerializedBlocks.add(new StargateDematerializedBlock(
						dematerializedBlockTag.getInteger("DestinationX"),
						dematerializedBlockTag.getInteger("DestinationY"),
						dematerializedBlockTag.getInteger("DestinationZ"),
						dematerializedBlockTag.getInteger("DestinationDim"),
						dematerializedBlockTag.getInteger("Id"),
						dematerializedBlockTag.getInteger("Meta"),
						tileEntity,
						dematerializedBlockTag.getInteger("X"),
						dematerializedBlockTag.getInteger("Y"),
						dematerializedBlockTag.getInteger("Z")
					));
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
	}

	public String dematerializeEntity(
		double newX, double newY, double newZ, float newYaw, float newPitch, StargateSession session, Entity entity
	) {
		boolean isPlayer = EnvironmentHelper.isServerEnvironment() && entity instanceof Player || EnvironmentHelper.isSinglePlayer() && entity instanceof Player;

		CompoundTag dematerializedData = new CompoundTag();

		if (!isPlayer) {
			entity.absMoveTo(newX, newY, newZ, newYaw, newPitch);
			entity.save(dematerializedData);
		}

		Entity rider = entity.ejectRider();

		String entityId = System.nanoTime() + "-" + entity.id;
		String passengerId = "";

		if (rider != null) {
			passengerId = this.dematerializeEntity(newX, newY, newZ, rider.yRot + 180, rider.xRot, session, rider);
		}

		if (EnvironmentHelper.isServerEnvironment() && entity instanceof Player) {
			Entity player = serverTeleport((Player) entity, newX, newY, newZ, newYaw, newPitch, session.destinationDim);
			teleportedPlayers.put(entityId, player);
		} else if (EnvironmentHelper.isSinglePlayer() && entity instanceof Player) {
			Entity player = singlePlayerTeleport((Player) entity, newX, newY, newZ, newYaw, newPitch, session.destinationDim);
			teleportedPlayers.put(entityId, player);
		} else {
			if (!entity.removed) {
				dematerializedEntities.add(new StargateDematerializedEntity(
					session.destinationX,
					session.destinationY,
					session.destinationZ,
					session.destinationDim,
					dematerializedData,
					entityId,
					passengerId
				));
			}

			entity.remove();
		}

		return entityId;
	}

	public void materializeEntities(StargateComponent gate) {
		TileEntity tile = gate.stargateTile;

		if (tile == null) {
			return;
		}

		if (tile.worldObj == null) {
			return;
		}

		int dim = tile.worldObj.dimension.id;

		Iterator<StargateDematerializedEntity> iterator = dematerializedEntities.iterator();

		Map<String, Entity> entities = new HashMap<>();

		while (iterator.hasNext()) {
			StargateDematerializedEntity dematerializedEntity = iterator.next();

			if (tile.x != dematerializedEntity.destinationX || tile.y != dematerializedEntity.destinationY || tile.z != dematerializedEntity.destinationZ || dim != dematerializedEntity.destinationDim) {
				continue;
			}

			Entity materializedEntity = EntityDispatcher.createEntityFromNBT(dematerializedEntity.dematerializedData, tile.worldObj);
			if (materializedEntity == null) {
				iterator.remove();
				continue;
			}
			tile.worldObj.entityJoinedWorld(materializedEntity);

			entities.put(dematerializedEntity.entityId, materializedEntity);

			if (entities.containsKey(dematerializedEntity.passengerId)) {
				entities.get(dematerializedEntity.passengerId).startRiding(materializedEntity);
			}

			if (teleportedPlayers.containsKey(dematerializedEntity.passengerId)) {
				Entity rider = teleportedPlayers.get(dematerializedEntity.passengerId);
				rider.startRiding(materializedEntity);
			}

			iterator.remove();
		}
	}

	public void dematerializeBlock(
		int destinationX,
		int destinationY,
		int destinationZ,
		int destinationDim,
		World world,
		int sourceX,
		int sourceY,
		int sourceZ,
		int targetX,
		int targetY,
		int targetZ
	) {
		int id = world.getBlockId(sourceX, sourceY, sourceZ);
		int meta = world.getBlockMetadata(sourceX, sourceY, sourceZ);

		TileEntity tileEntity = world.getTileEntity(sourceX, sourceY, sourceZ);

		world.removeBlockTileEntity(sourceX, sourceY, sourceZ);
		world.setBlockWithNotify(sourceX, sourceY, sourceZ, 0);

		if (tileEntity != null) {
			tileEntity.validate();
		}

		dematerializedBlocks.add(new StargateDematerializedBlock(
			destinationX,
			destinationY,
			destinationZ,
			destinationDim,
			id,
			meta,
			tileEntity,
			targetX,
			targetY,
			targetZ
		));
	}

	public void materializeBlocks(StargateComponent gate) {
		TileEntity tile = gate.stargateTile;

		if (tile == null) {
			return;
		}

		if (tile.worldObj == null) {
			return;
		}

		int dim = tile.worldObj.dimension.id;

		Iterator<StargateDematerializedBlock> iterator = dematerializedBlocks.iterator();

		while (iterator.hasNext()) {
			StargateDematerializedBlock dematerializedBlock = iterator.next();

			if (tile.x != dematerializedBlock.destinationX || tile.y != dematerializedBlock.destinationY || tile.z != dematerializedBlock.destinationZ || dim != dematerializedBlock.destinationDim) {
				continue;
			}

			int id = tile.worldObj.getBlockId(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ
			);

			int meta = tile.worldObj.getBlockMetadata(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ
			);

			if (id == dematerializedBlock.dematerializedId && meta == dematerializedBlock.dematerializedId && dematerializedBlock.dematerializedTile == null) {
				iterator.remove();
				continue;
			}

			tile.worldObj.setBlockAndMetadataRaw(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ,
				dematerializedBlock.dematerializedId,
				dematerializedBlock.dematerializedMeta
			);

			if (dematerializedBlock.dematerializedTile != null) {
				tile.worldObj.replaceBlockTileEntity(
					dematerializedBlock.dematerializedX,
					dematerializedBlock.dematerializedY,
					dematerializedBlock.dematerializedZ,
					dematerializedBlock.dematerializedTile
				);
			}

			tile.worldObj.notifyBlockChange(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ,
				dematerializedBlock.dematerializedId
			);

			iterator.remove();
		}
	}
}
