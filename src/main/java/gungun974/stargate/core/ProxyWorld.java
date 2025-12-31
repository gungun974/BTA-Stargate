package gungun974.stargate.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.BlocksContainer;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.season.SeasonManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProxyWorld implements WorldSource {
	public final WorldSource world;
	private final Map blockEntryMap = new HashMap();
	private Entity lightRefEntity = null;

	public ProxyWorld(WorldSource world) {
		this.world = world;
	}

	public void setLightReferenceEntity(Entity entity) {
		this.lightRefEntity = entity;
	}

	public void setBlock(int x, int y, int z, int blockID, int blockMeta, TileEntity entity) {
		this.blockEntryMap.put(new ChunkCoordinates(x, y, z), new BlocksContainer.BlockEntry(blockID, blockMeta, entity, x, y, z));
	}

	public BlocksContainer.BlockEntry getEntry(int x, int y, int z) {
		return (BlocksContainer.BlockEntry) this.blockEntryMap.get(new ChunkCoordinates(x, y, z));
	}

	public Collection getEntries() {
		return this.blockEntryMap.values();
	}

	public void clear() {
		this.blockEntryMap.clear();
	}

	public int getBlockId(int x, int y, int z) {
		ChunkCoordinates c = new ChunkCoordinates(x, y, z);
		return this.blockEntryMap.containsKey(c) ? ((BlocksContainer.BlockEntry) this.blockEntryMap.get(c)).blockID : this.world.getBlockId(x, y, z);
	}

	public @Nullable Block getBlock(int x, int y, int z) {
		return Blocks.getBlock(this.getBlockId(x, y, z));
	}

	public TileEntity getTileEntity(int x, int y, int z) {
		ChunkCoordinates c = new ChunkCoordinates(x, y, z);
		return this.blockEntryMap.containsKey(c) ? ((BlocksContainer.BlockEntry) this.blockEntryMap.get(c)).tileEntity : this.world.getTileEntity(x, y, z);
	}

	public float getBrightness(int x, int y, int z, int blockLightValue) {
		return this.lightRefEntity != null ? this.lightRefEntity.getBrightness(1.0F) : this.world.getBrightness(x, y, z, blockLightValue);
	}

	public int getLightmapCoord(int x, int y, int z, int blockLightValue) {
		return this.lightRefEntity != null ? this.lightRefEntity.getLightmapCoord(1.0F) : this.world.getLightmapCoord(x, y, z, blockLightValue);
	}

	public int getLightmapCoord(int skylight, int blocklight) {
		return this.world.getLightmapCoord(skylight, blocklight);
	}

	public float getLightBrightness(int x, int y, int z) {
		return this.world.getLightBrightness(x, y, z);
	}

	public int getBlockMetadata(int x, int y, int z) {
		ChunkCoordinates c = new ChunkCoordinates(x, y, z);
		return this.blockEntryMap.containsKey(c) ? ((BlocksContainer.BlockEntry) this.blockEntryMap.get(c)).blockMeta : this.world.getBlockMetadata(x, y, z);
	}

	public Material getBlockMaterial(int x, int y, int z) {
		Block<?> b = this.getBlock(x, y, z);
		return b == null ? Material.air : b.getMaterial();
	}

	public boolean isBlockOpaqueCube(int x, int y, int z) {
		Block<?> b = this.getBlock(x, y, z);
		return b != null && b.isSolidRender();
	}

	public boolean isBlockNormalCube(int x, int y, int z) {
		Block<?> b = this.getBlock(x, y, z);
		return b != null && b.getMaterial().isSolidBlocking() && b.renderAsNormalBlockOnCondition(this, x, y, z);
	}

	public double getBlockTemperature(int x, int z) {
		return this.world.getBlockTemperature(x, z);
	}

	public double getBlockHumidity(int x, int z) {
		return this.world.getBlockHumidity(x, z);
	}

	public SeasonManager getSeasonManager() {
		return this.world.getSeasonManager();
	}

	public Biome getBlockBiome(int x, int y, int z) {
		return this.world.getBlockBiome(x, y, z);
	}

	public int getSavedLightValue(LightLayer layer, int x, int y, int z) {
		return this.world.getSavedLightValue(layer, x, y, z);
	}

	public boolean isRetro() {
		return this.world.isRetro();
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
