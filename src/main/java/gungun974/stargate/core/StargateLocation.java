package gungun974.stargate.core;

public class StargateLocation {
	public final int x;
	public final int y;
	public final int z;
	public final int dim;
	public final StargateAddress address;

	public StargateLocation(int x, int y, int z, int dim, StargateAddress address) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
		this.address = address;
	}

}
