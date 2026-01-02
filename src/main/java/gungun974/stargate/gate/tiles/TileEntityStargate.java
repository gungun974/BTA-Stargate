package gungun974.stargate.gate.tiles;

import com.mojang.nbt.tags.CompoundTag;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import gungun974.stargate.gate.blocks.BlockLogicStargate;
import gungun974.stargate.gate.cc.StargatePeripheral;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.components.StargateComponent;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.util.helper.Direction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityStargate extends TileEntity implements IPeripheralTile {
	private final CamouflageComponent camouflageComponent = new CamouflageComponent();
	private Role role = Role.RING;
	private StargateComponent stargateComponent;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;

		if (role == Role.CORE) {
			stargateComponent = provideStargateComponent();
		} else {
			stargateComponent = null;
		}
	}

	@Override
	public void invalidate() {
		if (stargateComponent != null) {
			stargateComponent.invalidate();
		}
		super.invalidate();
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

	@Override
	public void readFromNBT(CompoundTag compoundTag) {
		role = Role.values()[compoundTag.getIntegerOrDefault("Role", role.ordinal())];

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

		if (stargateComponent != null) {
			stargateComponent.writeToNBT(compoundTag);
		}
		camouflageComponent.writeToNBT(compoundTag);
		super.writeToNBT(compoundTag);
	}

	@Override
	public void tick() {
		if (stargateComponent != null) {
			stargateComponent.tick();
		}
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

	@Nonnull
	@Override
	public IPeripheral getPeripheral(@NotNull Direction side) {
		return new StargatePeripheral(this);
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
