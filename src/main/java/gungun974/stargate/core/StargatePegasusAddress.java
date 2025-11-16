package gungun974.stargate.core;

import javax.annotation.Nullable;

public class StargatePegasusAddress extends StargateAddress {
	public static int NUMBER_OF_SYMBOL = 36;

	@Nullable
	public static StargatePegasusAddress createAddressFromBlock(int bx, int bz, int dim) {
		try {
			StargatePegasusAddress address = new StargatePegasusAddress();
			address.setAddressFromBlock(bx, bz, dim);
			return address;
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Nullable
	public static StargatePegasusAddress createAddressFromEncoded(int[] rawAddress) {
		try {
			StargatePegasusAddress address = new StargatePegasusAddress();
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
		return 128;
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.Pegasus;
	}
}
