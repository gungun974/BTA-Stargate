package gungun974.stargate.gate.tiles;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.gate.components.StargateComponent;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;

public abstract class TileEntityStargate extends TileEntity {
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
		super.readFromNBT(compoundTag);
	}

	@Override
	public void writeToNBT(CompoundTag compoundTag) {
		compoundTag.putInt("Role", role.ordinal());

		if (stargateComponent != null) {
			stargateComponent.writeToNBT(compoundTag);
		}
		super.writeToNBT(compoundTag);
	}

	@Override
	public void tick() {
		if (stargateComponent != null) {
			stargateComponent.tick();
		}
	}

	public StargateComponent getStargateComponent() {
		return stargateComponent;
	}

	protected abstract StargateComponent provideStargateComponent();

	public enum Role {
		CORE,
		RING,
	}
}
