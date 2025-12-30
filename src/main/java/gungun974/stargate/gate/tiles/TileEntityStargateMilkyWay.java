package gungun974.stargate.gate.tiles;

import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.components.StargateMilkyWayComponent;

public class TileEntityStargateMilkyWay extends TileEntityStargate {
	@Override
	protected StargateComponent provideStargateComponent() {
		return new StargateMilkyWayComponent(this);
	}
}
