package gungun974.stargate.core;

public class StargateSession {
	final public int originX;
	final public int originY;
	final public int originZ;
	final public int originDim;
	final public StargateAddress originAddress;

	final public int destinationX;
	final public int destinationY;
	final public int destinationZ;
	final public int destinationDim;
	final public StargateAddress destinationAddress;

	final public short dialingAddressSize;

	public StargateSession(int originX, int originY, int originZ, int originDim, StargateAddress originAddress, int destinationX, int destinationY, int destinationZ, int destinationDim, StargateAddress destinationAddress, short dialingAddressSize) {
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
		this.originDim = originDim;
		this.originAddress = originAddress;
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		this.destinationZ = destinationZ;
		this.destinationDim = destinationDim;
		this.destinationAddress = destinationAddress;
		this.dialingAddressSize = dialingAddressSize;
	}
}
