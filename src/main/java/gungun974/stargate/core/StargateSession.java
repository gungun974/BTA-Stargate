package gungun974.stargate.core;

import net.minecraft.core.util.helper.Direction;

public class StargateSession {
	final public int originX;
	final public int originY;
	final public int originZ;
	final public int originDim;
	final public Direction originDirection;
	final public Direction originOrientation;
	final public StargateAddress originAddress;

	final public int destinationX;
	final public int destinationY;
	final public int destinationZ;
	final public int destinationDim;
	final public Direction destinationDirection;
	final public Direction destinationOrientation;
	final public StargateAddress destinationAddress;

	final public short dialingAddressSize;

	public StargateSession(
		int originX,
		int originY,
		int originZ,
		int originDim,
		Direction originDirection,
		Direction originOrientation,
		StargateAddress originAddress,
		int destinationX,
		int destinationY,
		int destinationZ,
		int destinationDim,
		Direction destinationDirection,
		Direction destinationOrientation,
		StargateAddress destinationAddress,
		short dialingAddressSize
	) {
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
		this.originDim = originDim;
		this.originDirection = originDirection;
		this.originOrientation = originOrientation;
		this.originAddress = originAddress;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		this.destinationZ = destinationZ;
		this.destinationDim = destinationDim;
		this.destinationDirection = destinationDirection;
		this.destinationOrientation = destinationOrientation;
		this.destinationAddress = destinationAddress;
		this.dialingAddressSize = dialingAddressSize;
	}
}
