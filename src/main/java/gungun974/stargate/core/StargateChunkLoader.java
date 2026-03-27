package gungun974.stargate.core;

import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import gungun974.stargate.IWorldDirNameAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.reader.ChunkReader;
import net.minecraft.core.world.chunk.reader.ChunkReaderLegacy;
import net.minecraft.core.world.chunk.reader.ChunkReaderVersion1;
import net.minecraft.core.world.chunk.reader.ChunkReaderVersion2;
import net.minecraft.core.world.save.mcregion.RegionFileCache;
import net.minecraft.server.MinecraftServer;
import turniplabs.halplibe.helper.EnvironmentHelper;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StargateChunkLoader {
	private static final Map<Integer, Map<ChunkCoordinate, List<TileEntity>>> loadedTileEntities = new HashMap<>();
	private static final Map<Integer, Map<ChunkCoordinate, List<TileEntity>>> unloadedTileEntities = new HashMap<>();

	public static void clearAll() {
		loadedTileEntities.clear();
		unloadedTileEntities.clear();
	}

	public static void loadTileEntity(TileEntity tileEntity) {
		if (tileEntity.worldObj == null || tileEntity.worldObj.dimension == null) {
			return;
		}

		int dimension = tileEntity.worldObj.dimension.id;
		ChunkCoordinate chunkCoord = new ChunkCoordinate(tileEntity.x >> 4, tileEntity.z >> 4);

		Map<ChunkCoordinate, List<TileEntity>> dimLoadedMap = loadedTileEntities.computeIfAbsent(dimension, k -> new HashMap<>());
		List<TileEntity> loadedList = dimLoadedMap.computeIfAbsent(chunkCoord, k -> new ArrayList<>());

		if (!loadedList.contains(tileEntity)) {
			loadedList.add(tileEntity);
		}

		Map<ChunkCoordinate, List<TileEntity>> dimUnloadedMap = unloadedTileEntities.get(dimension);
		if (dimUnloadedMap != null) {
			List<TileEntity> unloadedList = dimUnloadedMap.get(chunkCoord);
			if (unloadedList != null) {
				unloadedList.remove(tileEntity);
				if (unloadedList.isEmpty()) {
					dimUnloadedMap.remove(chunkCoord);
					if (dimUnloadedMap.isEmpty()) {
						unloadedTileEntities.remove(dimension);
					}
				}
			}
		}
	}

	public static void unloadTileEntity(TileEntity tileEntity) {
		if (tileEntity.worldObj == null || tileEntity.worldObj.dimension == null) {
			return;
		}

		int dimension = tileEntity.worldObj.dimension.id;
		ChunkCoordinate chunkCoord = new ChunkCoordinate(tileEntity.x >> 4, tileEntity.z >> 4);

		Map<ChunkCoordinate, List<TileEntity>> dimLoadedMap = loadedTileEntities.get(dimension);
		if (dimLoadedMap != null) {
			List<TileEntity> loadedList = dimLoadedMap.get(chunkCoord);
			if (loadedList != null) {
				loadedList.remove(tileEntity);
				if (loadedList.isEmpty()) {
					dimLoadedMap.remove(chunkCoord);
					if (dimLoadedMap.isEmpty()) {
						loadedTileEntities.remove(dimension);
					}
				}
			}
		}

		Map<ChunkCoordinate, List<TileEntity>> dimUnloadedMap = unloadedTileEntities.computeIfAbsent(dimension, k -> new HashMap<>());
		List<TileEntity> unloadedList = dimUnloadedMap.computeIfAbsent(chunkCoord, k -> new ArrayList<>());
		if (!unloadedList.contains(tileEntity)) {
			unloadedList.add(tileEntity);
		}
	}

	private static boolean containsTileEntity(List<TileEntity> list, TileEntity tileEntity) {
		for (TileEntity te : list) {
			if (te.x == tileEntity.x && te.y == tileEntity.y && te.z == tileEntity.z
				&& te.getClass().equals(tileEntity.getClass())) {
				return true;
			}
		}
		return false;
	}

	public static File getWorldDir() {
		if (EnvironmentHelper.isServerEnvironment()) {
			return new File(MinecraftServer.getInstance().getMinecraftDir(), ((IWorldDirNameAccess) MinecraftServer.getInstance()).bta_stargate$getWorldDirName());
		}
		return new File(Minecraft.getMinecraft().getMinecraftDir(), "saves/" + ((IWorldDirNameAccess) Minecraft.getMinecraft()).bta_stargate$getWorldDirName());
	}

	private static ChunkReader getChunkReaderByVersion(CompoundTag tag, int version) {
		switch (version) {
			case 1:
				return new ChunkReaderVersion1(null, tag);
			case 2:
				return new ChunkReaderVersion2(null, tag);
			default:
				return new ChunkReaderLegacy(null, tag);
		}
	}

	@Nullable
	static public List<TileEntity> loadTileEntities(World world, int dimension, int chunkX, int chunkZ) {
		if (world == null) {
			return loadTileEntitiesFromDisk(dimension, chunkX, chunkZ);
		}
		if (world.dimension.id != dimension) {
			return loadTileEntitiesFromDisk(dimension, chunkX, chunkZ);
		}

		Chunk worldChunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

		if (worldChunk.isChunkEmpty()) {
			return loadTileEntitiesFromDisk(dimension, chunkX, chunkZ);
		}

		return new ArrayList<>(worldChunk.tileEntityMap.values());
	}

	static private List<TileEntity> loadTileEntitiesFromDisk(int dimension, int chunkX, int chunkZ) {
		File dimensionDir = new File(StargateChunkLoader.getWorldDir(), "dimensions/" + dimension);

		DataInputStream regionStream = RegionFileCache.getChunkInputStream(dimensionDir, chunkX, chunkZ);
		if (regionStream != null) {
			try {
				CompoundTag tag = NbtIo.read(regionStream);

				if (!tag.containsKey("Level")) {
					return loadLiveTileEntities(dimension, chunkX, chunkZ);
				}

				World dummyWorld = new World();

				dummyWorld.dimension = Dimension.getDimensionList().get(dimension);

				if (dummyWorld.dimension == null) {
					return loadLiveTileEntities(dimension, chunkX, chunkZ);
				}

				CompoundTag levelTag = tag.getCompound("Level");
				int version = levelTag.getIntegerOrDefault("Version", -1);
				ChunkReader reader = getChunkReaderByVersion(levelTag, version);
				int x = reader.getX();
				int z = reader.getZ();

				return loadTileEntitiesFromCompound(levelTag, dummyWorld, dimension, x, z);
			} catch (IOException e) {
				return loadLiveTileEntities(dimension, chunkX, chunkZ);
			}
		}

		return loadLiveTileEntities(dimension, chunkX, chunkZ);
	}

	static private List<TileEntity> loadLiveTileEntities(int dimension, int chunkX, int chunkZ) {
		List<TileEntity> result = new ArrayList<>();

		World dummyWorld = new World();
		dummyWorld.dimension = Dimension.getDimensionList().get(dimension);

		if (dummyWorld.dimension == null) {
			return result;
		}

		ChunkCoordinate chunkCoord = new ChunkCoordinate(chunkX, chunkZ);

		Map<ChunkCoordinate, List<TileEntity>> dimLoadedMap = loadedTileEntities.get(dimension);
		List<TileEntity> loadedList = dimLoadedMap != null ? dimLoadedMap.get(chunkCoord) : null;
		List<TileEntity> loadedListCopy = loadedList != null ? new ArrayList<>(loadedList) : null;

		if (loadedListCopy != null) {
			for (TileEntity liveTileEntity : loadedListCopy) {
				CompoundTag entityTag = new CompoundTag();
				liveTileEntity.writeToNBT(entityTag);
				TileEntity tileEntity = TileEntityDispatcher.createAndLoadEntity(entityTag);
				tileEntity.worldObj = dummyWorld;
				result.add(tileEntity);
			}
		}

		return result;
	}

	static private List<TileEntity> loadTileEntitiesFromCompound(CompoundTag tag, World dummyWorld, int dimension, int x, int z) {
		List<TileEntity> result = new ArrayList<>();

		ChunkCoordinate chunkCoord = new ChunkCoordinate(x, z);

		Map<ChunkCoordinate, List<TileEntity>> dimLoadedMap = loadedTileEntities.get(dimension);
		Map<ChunkCoordinate, List<TileEntity>> dimUnloadedMap = unloadedTileEntities.get(dimension);

		List<TileEntity> loadedList = dimLoadedMap != null ? dimLoadedMap.get(chunkCoord) : null;
		List<TileEntity> unloadedList = dimUnloadedMap != null ? dimUnloadedMap.get(chunkCoord) : null;

		List<TileEntity> loadedListCopy = loadedList != null ? new ArrayList<>(loadedList) : null;
		List<TileEntity> unloadedListCopy = unloadedList != null ? new ArrayList<>(unloadedList) : null;

		ListTag tileEntityListTag = tag.getList("TileEntities");
		if (tileEntityListTag != null) {
			for (Tag<?> tileEntityTagBase : tileEntityListTag) {
				if (tileEntityTagBase instanceof CompoundTag) {
					CompoundTag tileEntityTag = (CompoundTag) tileEntityTagBase;
					TileEntity tileEntity = TileEntityDispatcher.createAndLoadEntity(tileEntityTag);
					if (tileEntity != null) {
						boolean shouldSkip = false;

						if (unloadedListCopy != null && containsTileEntity(unloadedListCopy, tileEntity)) {
							shouldSkip = true;
						}

						if (loadedListCopy != null && containsTileEntity(loadedListCopy, tileEntity)) {
							shouldSkip = true;
						}

						if (!shouldSkip) {
							tileEntity.worldObj = dummyWorld;
							result.add(tileEntity);
						}
					}
				}
			}
		}

		if (loadedListCopy != null) {
			for (TileEntity liveTileEntity : loadedListCopy) {
				CompoundTag entityTag = new CompoundTag();
				liveTileEntity.writeToNBT(entityTag);
				TileEntity tileEntity = TileEntityDispatcher.createAndLoadEntity(entityTag);
				tileEntity.worldObj = dummyWorld;
				result.add(tileEntity);
			}
		}

		return result;
	}
}
