package gungun974.stargate.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.world.season.SeasonManager;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.weather.WeatherManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProxyWorld implements WorldSource {
	public final WorldSource world;
	private final Map<ChunkCoordinates, BlockEntry> blockEntryMap = new HashMap<>();
	private Entity lightRefEntity = null;

	public ProxyWorld(WorldSource world) {
		this.world = world;
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

	public Collection<BlockEntry> getEntries() {
		return this.blockEntryMap.values();
	}

	public void clear() {
		this.blockEntryMap.clear();
	}

	@Override
	public int getHeightBlocks() {
		return this.world.getHeightBlocks();
	}

	@Override
	public @NotNull Block getBlockType(@NotNull TilePosc tilePos) {
		ChunkCoordinates c = new ChunkCoordinates(tilePos.x(), tilePos.y(), tilePos.z());
		if (this.blockEntryMap.containsKey(c)) {
			Block<?> block = Blocks.getBlock(this.blockEntryMap.get(c).blockID);
			return block != null ? block : Blocks.AIR;
		}
		return this.world.getBlockType(tilePos);
	}

	@Override
	public @Nullable TileEntity getTileEntity(@NotNull TilePosc tilePos) {
		ChunkCoordinates c = new ChunkCoordinates(tilePos.x(), tilePos.y(), tilePos.z());
		return this.blockEntryMap.containsKey(c) ? this.blockEntryMap.get(c).tileEntity : this.world.getTileEntity(tilePos);
	}

	@Override
	public int getBlockData(@NotNull TilePosc tilePos) {
		ChunkCoordinates c = new ChunkCoordinates(tilePos.x(), tilePos.y(), tilePos.z());
		return this.blockEntryMap.containsKey(c) ? this.blockEntryMap.get(c).blockMeta : this.world.getBlockData(tilePos);
	}

	@Override
	public float getBrightness(@NotNull TilePosc tilePos, int lightEmission) {
		return this.lightRefEntity != null ? this.lightRefEntity.getBrightness(1.0F) : this.world.getBrightness(tilePos, lightEmission);
	}

	@Override
	public byte getLightIndex(@NotNull TilePosc tilePos, int lightEmission) {
		return this.lightRefEntity != null ? this.lightRefEntity.getLightIndex(1.0F) : this.world.getLightIndex(tilePos, lightEmission);
	}

	@Override
	public float getLightBrightness(@NotNull TilePosc tilePos) {
		return this.world.getLightBrightness(tilePos);
	}

	@Override
	public @NotNull Material getBlockMaterial(@NotNull TilePosc tilePos) {
		return this.getBlockType(tilePos).getMaterial();
	}

	@Override
	public boolean isBlockOpaqueCube(@NotNull TilePosc tilePos) {
		return this.getBlockType(tilePos).isSolidRender();
	}

	@Override
	public boolean isBlockNormalCube(@NotNull TilePosc tilePos) {
		Block<?> block = this.getBlockType(tilePos);
		return block.getMaterial().isSolidBlocking() && block.renderAsNormalBlockOnCondition(this, tilePos);
	}

	@Override
	public double getBlockTemperature(@NotNull TilePosc tilePos) {
		return this.world.getBlockTemperature(tilePos);
	}

	@Override
	public double getBlockHumidity(@NotNull TilePosc tilePos) {
		return this.world.getBlockHumidity(tilePos);
	}

	@Override
	public double getBlockVariety(@NotNull TilePosc tilePos) {
		return this.world.getBlockVariety(tilePos);
	}

	@Override
	public @NotNull SeasonManager getSeasonManager() {
		return this.world.getSeasonManager();
	}

	@Override
	public @NotNull WeatherManager getWeatherManager() {
		return this.world.getWeatherManager();
	}

	@Override
	public @NotNull Biome getBlockBiome(@NotNull TilePosc tilePos) {
		return this.world.getBlockBiome(tilePos);
	}

	@Override
	public int getSavedLightValue(@NotNull LightLayer layer, @NotNull TilePosc tilePos) {
		return this.world.getSavedLightValue(layer, tilePos);
	}

	@Override
	public byte getSavedLightIndex(@NotNull TilePosc tilePos) {
		return this.world.getSavedLightIndex(tilePos);
	}

	@Override
	public @NotNull Dimension getDimension() {
		return this.world.getDimension();
	}

	@Override
	public @NotNull WorldType getWorldType() {
		return this.world.getWorldType();
	}

	public record BlockEntry(int blockID, int blockMeta, @Nullable TileEntity tileEntity, int x, int y, int z) {
	}
}
