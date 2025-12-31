package gungun974.stargate.core;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

public class VirtualWorld extends World {

	public VirtualWorld(World world) {
		super(world, world.dimension);
	}

	@Override
	public Chunk getChunkFromChunkCoords(int x, int z) {
		return new VirtualChunk(this.chunkProvider.provideChunk(x, z));
	}

	private static class VirtualChunk extends Chunk {
		public VirtualChunk(Chunk chunk) {
			super(chunk.world, chunk.xPosition, chunk.zPosition);
			isLoaded = chunk.isLoaded;
			heightMap = chunk.heightMap;
			lowestY = chunk.lowestY;
			tileEntityMap = chunk.tileEntityMap;
			temperature = chunk.temperature;
			humidity = chunk.humidity;
			variety = chunk.variety;
			averageBlockHeight = chunk.averageBlockHeight;
			isTerrainPopulated = chunk.isTerrainPopulated;
			isModified = chunk.isModified;
			neverSave = true;
			hasEntities = chunk.hasEntities;
			lastSaveTime = chunk.lastSaveTime;

			for (int i = 0; i < this.sections.length; ++i) {
				//this.sections[i] = new ChunkSection(this, i);
				this.sections[i] = chunk.getSection(i);
			}
		}
	}
}
