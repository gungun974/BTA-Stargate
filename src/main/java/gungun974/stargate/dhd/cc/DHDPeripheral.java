package gungun974.stargate.dhd.cc;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IPeripheral;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import gungun974.stargate.gate.cc.BaseStargatePeripheral;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import org.jetbrains.annotations.NotNull;

public class DHDPeripheral extends BaseStargatePeripheral {
	private final TileEntityDHD dhd;

	public DHDPeripheral(TileEntityDHD dhd) {
		this.dhd = dhd;
	}

	@Override
	protected StargateComponent getStargateComponent() throws LuaException {
		TileEntityStargate stargate = dhd.findLinkedGate();

		if (stargate == null) {
			throw new LuaException("DHD is not linked");
		}

		StargateComponent stargateComponent = stargate.findMainStargateComponent();

		if (stargateComponent == null) {
			throw new LuaException("Stargate controller not found");
		}

		return stargateComponent;
	}

	@Override
	protected boolean shouldAllowFastEncode() throws LuaException {
		return true;
	}

	@Override
	public @NotNull String getType() {
		return "dhd";
	}

	@Override
	public boolean equals(IPeripheral other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DHDPeripheral)) {
			return false;
		}

		return dhd.findLinkedGate() == ((DHDPeripheral) other).dhd.findLinkedGate();
	}
}
