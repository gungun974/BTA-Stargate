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
import net.minecraft.core.world.chunk.ChunkLoaderLegacy;
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
import java.util.Arrays;
import java.util.Map;

public class StargateChunkLoader {
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
	static public Chunk loadChunk(World world, int dimension, int chunkX, int chunkZ) {
		if (world == null) {
			return loadChunkFromDisk(dimension, chunkX, chunkZ);
		}
		if (world.dimension.id != dimension) {
			return loadChunkFromDisk(dimension, chunkX, chunkZ);
		}

		Chunk worldChunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

		if (worldChunk.isChunkEmpty()) {
			return loadChunkFromDisk(dimension, chunkX, chunkZ);
		}

		return worldChunk;
	}

	static private Chunk loadChunkFromDisk(int dimension, int chunkX, int chunkZ) {
		File dimensionDir = new File(StargateChunkLoader.getWorldDir(), "dimensions/" + dimension);

		DataInputStream regionStream = RegionFileCache.getChunkInputStream(dimensionDir, chunkX, chunkZ);
		if (regionStream != null) {
			try {
				CompoundTag tag = NbtIo.read(regionStream);

				if (!tag.containsKey("Level")) {
					return null;
				}

				World dummyWorld = new World();

				dummyWorld.dimension = Dimension.getDimensionList().get(dimension);

				if (dummyWorld.dimension == null) {
					return null;
				}

				return loadChunkFromCompound(tag.getCompound("Level"), dummyWorld);
			} catch (IOException e) {
				return null;
			}
		}

		return null;
	}

	static private Chunk loadChunkFromCompound(CompoundTag tag, World dummyWorld) {
		int version = tag.getIntegerOrDefault("Version", -1);
		ChunkReader reader = getChunkReaderByVersion(tag, version);
		int x = reader.getX();
		int z = reader.getZ();
		Chunk chunk = new Chunk(dummyWorld, x, z);
		chunk.heightMap = reader.getHeightMap();
		chunk.averageBlockHeight = reader.getAverageBlockHeight();
		chunk.isTerrainPopulated = reader.getIsTerrainPopulated();
		chunk.temperature = reader.getTemperatureMap();
		chunk.humidity = reader.getHumidityMap();
		Map<Integer, String> biomeRegistry = reader.getBiomeRegistry();

		for (int i = 0; i < 16; ++i) {
			ChunkLoaderLegacy.loadChunkSectionFromCompound(chunk.getSection(i), reader, biomeRegistry);
		}

		if (chunk.heightMap == null) {
			chunk.heightMap = new short[256];
			chunk.recalcHeightmap();
		}

		if (chunk.temperature == null || chunk.temperature.length == 0) {
			chunk.temperature = new double[256];
			Arrays.fill(chunk.temperature, Double.NEGATIVE_INFINITY);
		}

		if (chunk.humidity == null || chunk.humidity.length == 0) {
			chunk.humidity = new double[256];
			Arrays.fill(chunk.humidity, Double.NEGATIVE_INFINITY);
		}

		ListTag tileEntityListTag = tag.getList("TileEntities");
		if (tileEntityListTag != null) {
			for (Tag<?> tileEntityTagBase : tileEntityListTag) {
				if (tileEntityTagBase instanceof CompoundTag) {
					CompoundTag tileEntityTag = (CompoundTag) tileEntityTagBase;
					TileEntity tileEntity = TileEntityDispatcher.createAndLoadEntity(tileEntityTag);
					if (tileEntity != null) {
						chunk.addTileEntity(tileEntity);
						tileEntity.worldObj = dummyWorld;
					}
				}
			}
		}

		return chunk;
	}
}
