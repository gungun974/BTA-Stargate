package gungun974.stargate.core;

import gungun974.stargate.gate.tiles.TileEntityStargate;

public class StargateLocation {
	public final int x;
	public final int y;
	public final int z;
	public final int dim;
	public final StargateAddress address;
	public final boolean hasDHD;
	public final StargateFamily family;
	public final TileEntityStargate gate;

	public StargateLocation(int x, int y, int z, int dim, StargateAddress address, boolean hasDHD, StargateFamily family, TileEntityStargate gate) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
		this.address = address;
		this.hasDHD = hasDHD;
		this.family = family;
		this.gate = gate;
	}

}
