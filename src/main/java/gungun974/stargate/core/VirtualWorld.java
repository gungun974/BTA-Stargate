package gungun974.stargate.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.provider.ChunkProvider;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.core.world.save.LevelStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VirtualWorld extends World {

	private static final ThreadLocal<World> PARENT_REF = new ThreadLocal<>();
	private final Map<ChunkCoordinates, BlockEntry> blockEntryMap = new HashMap<>();
	private Entity lightRefEntity = null;
	public VirtualWorld(World parent) {
		super(captureParent(parent), parent.getDimension());
		PARENT_REF.remove();
		this.noNeighborUpdate = true;
	}

	private static World captureParent(World parent) {
		PARENT_REF.set(parent);
		return parent;
	}

	@Override
	protected @NotNull ChunkProvider createChunkProvider(@NotNull LevelStorage levelStorage) {
		return PARENT_REF.get().getChunkProvider();
	}

	@Override
	public void setLightningFlicker(int value) {
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
	public boolean setBlockTypeData(@NotNull TilePosc tilePos, @NotNull Block block, int data) {
		if (!tilePos.inBounds(this)) return false;
		setBlock(tilePos.x(), tilePos.y(), tilePos.z(), block.id(), data, null);
		return true;
	}

	@Override
	public boolean setBlockTypeDataRaw(@NotNull TilePosc tilePos, @NotNull Block block, int data) {
		if (!tilePos.inBounds(this)) return false;
		setBlock(tilePos.x(), tilePos.y(), tilePos.z(), block.id(), data, null);
		return true;
	}

	@Override
	public boolean setBlockType(@NotNull TilePosc tilePos, @NotNull Block block) {
		if (!tilePos.inBounds(this)) return false;
		setBlock(tilePos.x(), tilePos.y(), tilePos.z(), block.id(), getBlockData(tilePos), null);
		return true;
	}

	@Override
	public boolean setBlockData(@NotNull TilePosc tilePos, int data) {
		if (!tilePos.inBounds(this)) return false;
		setBlock(tilePos.x(), tilePos.y(), tilePos.z(), getBlockType(tilePos).id(), data, null);
		return true;
	}

	@Override
	public void setTileEntity(@NotNull TilePosc tilePos, @NotNull TileEntity tileEntity) {
	}

	@Override
	public void removeTileEntity(@NotNull TilePosc tilePos) {
	}

	@Override
	public @NotNull Block getBlockType(@NotNull TilePosc tilePos) {
		ChunkCoordinates c = new ChunkCoordinates(tilePos.x(), tilePos.y(), tilePos.z());
		if (this.blockEntryMap.containsKey(c)) {
			Block<?> block = Blocks.getBlock(this.blockEntryMap.get(c).blockID);
			return block != null ? block : Blocks.AIR;
		}
		return super.getBlockType(tilePos);
	}

	@Override
	public int getBlockData(@NotNull TilePosc tilePos) {
		ChunkCoordinates c = new ChunkCoordinates(tilePos.x(), tilePos.y(), tilePos.z());
		return this.blockEntryMap.containsKey(c) ? this.blockEntryMap.get(c).blockMeta : super.getBlockData(tilePos);
	}

	@Override
	public @Nullable TileEntity getTileEntity(@NotNull TilePosc tilePos) {
		ChunkCoordinates c = new ChunkCoordinates(tilePos.x(), tilePos.y(), tilePos.z());
		return this.blockEntryMap.containsKey(c) ? this.blockEntryMap.get(c).tileEntity : super.getTileEntity(tilePos);
	}

	@Override
	public float getBrightness(@NotNull TilePosc tilePos, int lightEmission) {
		return this.lightRefEntity != null ? this.lightRefEntity.getBrightness(1.0F) : super.getBrightness(tilePos, lightEmission);
	}

	@Override
	public byte getLightIndex(@NotNull TilePosc tilePos, int lightEmission) {
		return this.lightRefEntity != null ? this.lightRefEntity.getLightIndex(1.0F) : super.getLightIndex(tilePos, lightEmission);
	}

	public record BlockEntry(int blockID, int blockMeta, @Nullable TileEntity tileEntity, int x, int y, int z) {
	}
}
