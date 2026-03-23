package gungun974.stargate.dhd.tiles;

import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkPosition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TileEntityDHD extends TileEntity {
	private boolean linked = false;
	private TileEntityStargate linkedGate = null;
	private int linkedGateX = 0;
	private int linkedGateY = 0;
	private int linkedGateZ = 0;

	@Nullable
	private TileEntityStargate findClosestGate() {
		if (worldObj == null) {
			return null;
		}

		List<TileEntityStargate> gates = new ArrayList<>();

		int chunkX = Math.floorDiv(x, 16);
		int chunkZ = Math.floorDiv(z, 16);

		for (int cx = chunkX - 1; cx <= chunkX + 1; cx++) {
			for (int cz = chunkZ - 1; cz <= chunkZ + 1; cz++) {
				Chunk chunk = worldObj.getChunkFromChunkCoords(cx, cz);

				if (chunk == null) {
					continue;
				}

				for (Map.Entry<ChunkPosition, TileEntity> chunkPositionTileEntityEntry : chunk.tileEntityMap.entrySet()) {
					TileEntity tileEntity = chunkPositionTileEntityEntry.getValue();

					if (tileEntity instanceof TileEntityStargate) {
						StargateComponent gate = ((TileEntityStargate) tileEntity).getStargateComponent();

						if (gate != null) {
							gates.add((TileEntityStargate) tileEntity);
						}
					}
				}
			}
		}

		if (gates.isEmpty()) {
			return null;
		}

		TileEntityStargate target = gates.get(0);
		int closestDistance = Integer.MAX_VALUE;

		for (TileEntityStargate gate : gates) {
			int diffX = gate.x - x;
			int diffY = gate.y - y;
			int diffZ = gate.z - z;
			int distance = diffX * diffX + diffY * diffY + diffZ * diffZ;

			if (distance < closestDistance) {
				target = gate;
				closestDistance = distance;
			}
		}

		return target;
	}

	@Nullable
	public TileEntityStargate findLinkedGate() {
		if (linkedGate != null) {
			if (!linkedGate.isInvalid()) {
				if (linkedGate.getStargateComponent() != null) {
					return linkedGate;
				}
			}

			linkedGate = null;
		}

		if (worldObj == null) {
			return null;
		}

		if (linked) {
			TileEntity tile = worldObj.getTileEntity(linkedGateX, linkedGateY, linkedGateZ);

			if (!tile.isInvalid() && tile instanceof TileEntityStargate) {
				TileEntityStargate gate = (TileEntityStargate) tile;

				if (gate.getStargateComponent() != null) {
					linkedGate = gate;
					return gate;
				}
			}

			linked = false;
		}

		TileEntityStargate gate = findClosestGate();

		if (gate == null) {
			return null;
		}

		linked = true;
		linkedGateX = gate.x;
		linkedGateY = gate.y;
		linkedGateZ = gate.z;
		linkedGate = gate;

		return gate;
	}

	public void encode(int s) {
		TileEntityStargate tile = findLinkedGate();

		if (tile == null) {
			return;
		}

		StargateComponent gate = tile.getStargateComponent();

		if (gate == null) {
			return;
		}

		for (int currentDialingAddress : gate.getCurrentDialingAddress()) {
			if (currentDialingAddress == s) {
				gate.removeSymbol(s);
				return;
			}
		}

		gate.fastEncode(s);
	}

	public void dial() {
		TileEntityStargate tile = findLinkedGate();

		if (tile == null) {
			return;
		}

		StargateComponent gate = tile.getStargateComponent();

		if (gate == null) {
			return;
		}

		switch (gate.getState()) {
			case IDLE:
			case DIALLING:
			case AWAIT:
				gate.dial();
			case OPENING:
			case CONNECTED:
			case CLOSING:
				gate.closeGate();
		}
	}

	public int getLightmap() {
		if (worldObj == null) {
			return LightmapHelper.getLightmapCoord(15, 15);
		}

		return worldObj.getLightmapCoord(x, y, z, worldObj.getBlockLightValue(x, y, z));
	}
}
