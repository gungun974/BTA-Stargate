package gungun974.stargate.gate.tiles;

import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.components.StargateUniverseComponent;

public class TileEntityStargateUniverse extends TileEntityStargate {
	@Override
	protected StargateComponent provideStargateComponent() {
		return new StargateUniverseComponent(this);
	}
}
