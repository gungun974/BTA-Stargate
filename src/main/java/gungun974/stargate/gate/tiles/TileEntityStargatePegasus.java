package gungun974.stargate.gate.tiles;

import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.components.StargatePegasusComponent;

public class TileEntityStargatePegasus extends TileEntityStargate {
	@Override
	protected StargateComponent provideStargateComponent() {
		return new StargatePegasusComponent(this);
	}
}
