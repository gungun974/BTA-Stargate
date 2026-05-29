package gungun974.stargate.core;

import gungun974.stargate.gate.tiles.TileEntityStargate;

public record StargateLocation(int x, int y, int z, int dim, StargateAddress address, boolean hasDHD,
							   StargateFamily family, TileEntityStargate gate) {

}
