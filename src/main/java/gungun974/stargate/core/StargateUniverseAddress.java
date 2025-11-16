package gungun974.stargate.core;

import javax.annotation.Nullable;

public class StargateUniverseAddress extends StargateAddress {
	public static int NUMBER_OF_SYMBOL = 36;

	@Nullable
	public static StargateUniverseAddress createAddressFromBlock(int bx, int bz, int dim) {
		try {
			StargateUniverseAddress address = new StargateUniverseAddress();
			address.setAddressFromBlock(bx, bz, dim);
			return address;
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Nullable
	public static StargateUniverseAddress createAddressFromEncoded(int[] rawAddress) {
		try {
			StargateUniverseAddress address = new StargateUniverseAddress();
			address.setAddressFromEncoded(rawAddress);
			return address;
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	protected int numberOfSymbol() {
		return NUMBER_OF_SYMBOL;
	}

	@Override
	protected int distanceBetweenGate() {
		return 736;
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.Universe;
	}
}
