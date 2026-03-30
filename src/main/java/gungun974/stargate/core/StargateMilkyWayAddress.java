package gungun974.stargate.core;

import javax.annotation.Nullable;

public class StargateMilkyWayAddress extends StargateAddress {
	public static int NUMBER_OF_SYMBOL = 39;

	@Nullable
	public static StargateMilkyWayAddress createAddressFromBlock(int bx, int bz, int dim) {
		try {
			StargateMilkyWayAddress address = new StargateMilkyWayAddress();
			address.setAddressFromBlock(bx, bz, dim);
			return address;
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Nullable
	public static StargateMilkyWayAddress createAddressFromEncoded(int[] rawAddress) {
		try {
			StargateMilkyWayAddress address = new StargateMilkyWayAddress();
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
		return 64;
	}

	@Override
	protected int getSpecialSymbol() {
		return 12;
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.MilkyWay;
	}
}
