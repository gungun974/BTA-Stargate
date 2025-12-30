package gungun974.stargate.gate.components;

import gungun974.stargate.core.StargateFamily;
import net.minecraft.core.block.entity.TileEntity;

public class StargateMilkyWayComponent extends StargateComponent {
	public StargateMilkyWayComponent(TileEntity stargateTile) {
		super(stargateTile);
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.MilkyWay;
	}
}
