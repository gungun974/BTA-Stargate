package gungun974.stargate.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VirtualWorld extends World {

	private final Map<ChunkCoordinates, BlockEntry> blockEntryMap = new HashMap();
	private Entity lightRefEntity = null;

	public VirtualWorld(World world) {
		super();
		this.sleepPercent = 100;
		//this.lockTimestamp = world.lockTimestamp;
		this.saveHandler = world.saveHandler;
		this.levelData = world.getLevelData();
		this.dimensionData = world.dimensionData;
		this.weatherManager = world.weatherManager;

		this.worldType = world.worldType;
		this.savedDataStorage = world.savedDataStorage;
		this.dimension = world.dimension;
		this.chunkProvider = world.chunkProvider;
		this.seasonManager = world.seasonManager;
		this.biomeProvider = world.biomeProvider;
		this.auroraProvider = world.auroraProvider;
	}

	public void setLightReferenceEntity(Entity entity) {
		this.lightRefEntity = entity;
	}

	public void setBlock(int x, int y, int z, int blockID, int blockMeta, TileEntity entity) {
		this.blockEntryMap.put(new ChunkCoordinates(x, y, z), new BlockEntry(blockID, blockMeta, entity, x, y, z));
	}

	public BlockEntry getEntry(int x, int y, int z) {
		return this.blockEntryMap.get(new ChunkCoordinates(x, y, z));
	}

	public Collection getEntries() {
		return this.blockEntryMap.values();
	}

	public void clear() {
		this.blockEntryMap.clear();
	}

	@Override
	public boolean setBlockAndMetadata(int x, int y, int z, int id, int meta) {
		if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
			if (y < 0) {
				return false;
			} else if (y >= this.getHeightBlocks()) {
				return false;
			} else {
				setBlock(x, y, z, id, meta, null);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean setBlockAndMetadataRaw(int x, int y, int z, int id, int meta) {
		if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
			if (y < 0) {
				return false;
			} else if (y >= this.getHeightBlocks()) {
				return false;
			} else {
				setBlock(x, y, z, id, meta, null);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean setBlockMetadata(int x, int y, int z, int meta) {
		if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
			if (y < 0) {
				return false;
			} else if (y >= this.getHeightBlocks()) {
				return false;
			} else {
				setBlock(x, y, z, this.getBlockId(x, y, z), meta, null);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id) {
		if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
			if (y < 0) {
				return false;
			} else if (y >= this.getHeightBlocks()) {
				return false;
			} else {
				setBlock(x, y, z, id, getBlockMetadata(x, y, z), null);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void setTileEntity(int x, int y, int z, TileEntity tileEntity) {
	}

	@Override
	public void removeBlockTileEntity(int x, int y, int z) {
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		ChunkCoordinates c = new ChunkCoordinates(x, y, z);
		return this.blockEntryMap.containsKey(c) ? this.blockEntryMap.get(c).blockID : super.getBlockId(x, y, z);
	}

	@Override
	public @Nullable Block getBlock(int x, int y, int z) {
		return Blocks.getBlock(this.getBlockId(x, y, z));
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		ChunkCoordinates c = new ChunkCoordinates(x, y, z);
		return this.blockEntryMap.containsKey(c) ? this.blockEntryMap.get(c).tileEntity : super.getTileEntity(x, y, z);
	}

	@Override
	public float getBrightness(int x, int y, int z, int blockLightValue) {
		return this.lightRefEntity != null ? this.lightRefEntity.getBrightness(1.0F) : super.getBrightness(x, y, z, blockLightValue);
	}

	@Override
	public int getLightmapCoord(int x, int y, int z, int blockLightValue) {
		return this.lightRefEntity != null ? this.lightRefEntity.getLightmapCoord(1.0F) : super.getLightmapCoord(x, y, z, blockLightValue);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		ChunkCoordinates c = new ChunkCoordinates(x, y, z);
		return this.blockEntryMap.containsKey(c) ? this.blockEntryMap.get(c).blockMeta : super.getBlockMetadata(x, y, z);
	}

	@Override
	public Material getBlockMaterial(int x, int y, int z) {
		Block<?> b = this.getBlock(x, y, z);
		return b == null ? Material.air : b.getMaterial();
	}

	@Override
	public boolean isBlockOpaqueCube(int x, int y, int z) {
		Block<?> b = this.getBlock(x, y, z);
		return b != null && b.isSolidRender();
	}

	@Override
	public boolean isBlockNormalCube(int x, int y, int z) {
		Block<?> b = this.getBlock(x, y, z);
		return b != null && b.getMaterial().isSolidBlocking() && b.renderAsNormalBlockOnCondition(this, x, y, z);
	}

	public static class BlockEntry {
		public final int blockID;
		public final int blockMeta;
		public final @Nullable TileEntity tileEntity;
		public final int x;
		public final int y;
		public final int z;

		public BlockEntry(int blockID, int blockMeta, @Nullable TileEntity tileEntity, int x, int y, int z) {
			this.blockID = blockID;
			this.blockMeta = blockMeta;
			this.tileEntity = tileEntity;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
