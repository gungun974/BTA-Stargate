package gungun974.stargate.gate.components;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.tiles.TileEntityStargate;

public class StargateMilkyWayComponent extends StargateComponent {
	public StargateMilkyWayComponent(TileEntityStargate stargateTile) {
		super(stargateTile);
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.MilkyWay;
	}
}
