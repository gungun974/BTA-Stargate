package gungun974.stargate.gate.tiles;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.core.StargateNetworkManager;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import gungun974.stargate.gate.blocks.BlockLogicStargate;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.components.StargateComponent;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkPosition;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class TileEntityStargate extends TileEntity {
	private final CamouflageComponent camouflageComponent = new CamouflageComponent();
	public int dim;
	private Role role = Role.RING;
	private StargateComponent stargateComponent;
	private boolean hasDHD = false;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;

		if (role == Role.CORE) {
			stargateComponent = provideStargateComponent();
			StargateNetworkManager.getInstance().registerPublicStargate(this);
		} else {
			StargateNetworkManager.getInstance().unregisterPublicStargate(this);
			stargateComponent = null;
		}
	}

	public boolean hasDHD() {
		return hasDHD;
	}

	@Override
	public void validate() {
		super.validate();
		StargateNetworkManager.getInstance().registerPublicStargate(this);
	}

	public void destroyed() {
		StargateNetworkManager.getInstance().unregisterPublicStargate(this);
		if (stargateComponent != null) {
			stargateComponent.invalidate();
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

	@Override
	public void readFromNBT(CompoundTag compoundTag) {
		role = Role.values()[compoundTag.getIntegerOrDefault("Role", role.ordinal())];
		hasDHD = compoundTag.getBooleanOrDefault("HasDHD", false);
		dim = compoundTag.getIntegerOrDefault("Dim", 0);

		if (worldObj != null) {
			dim = worldObj.dimension.id;
		}

		setRole(role);

		if (stargateComponent != null) {
			stargateComponent.readFromNBT(compoundTag);
		}
		camouflageComponent.readFromNBT(compoundTag);
		super.readFromNBT(compoundTag);
	}

	@Override
	public void writeToNBT(CompoundTag compoundTag) {
		compoundTag.putInt("Role", role.ordinal());
		compoundTag.putBoolean("HasDHD", hasDHD);
		compoundTag.putInt("Dim", dim);

		if (worldObj != null) {
			dim = worldObj.dimension.id;
		}

		if (stargateComponent != null) {
			stargateComponent.writeToNBT(compoundTag);
		}
		camouflageComponent.writeToNBT(compoundTag);
		super.writeToNBT(compoundTag);
	}

	@Override
	public void tick() {
		detectDHD();

		if (stargateComponent != null) {
			stargateComponent.tick();
		}
	}

	private void detectDHD() {
		if (worldObj == null) {
			return;
		}

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

					if (tileEntity instanceof TileEntityDHD) {
						if (((TileEntityDHD) tileEntity).findLinkedGate() == this) {
							hasDHD = true;
							return;
						}
					}
				}
			}
		}

		hasDHD = false;
	}

	@Nullable
	public StargateComponent getStargateComponent() {
		return stargateComponent;
	}

	@Nullable
	public StargateComponent findMainStargateComponent() {
		if (worldObj == null) {
			return getStargateComponent();
		}

		BlockLogicStargate blockLogicStargate = worldObj.getBlockLogic(x, y, z, BlockLogicStargate.class);
		if (blockLogicStargate == null) {
			return getStargateComponent();
		}

		TileEntityStargate stargate = blockLogicStargate.findMainTileEntityStargate(worldObj, x, y, z);
		if (stargate == null) {
			return getStargateComponent();
		}

		return stargate.getStargateComponent();
	}

	public CamouflageComponent getCamouflageComponent() {
		return camouflageComponent;
	}

	protected abstract StargateComponent provideStargateComponent();

	public enum Role {
		CORE,
		RING,
	}
}
