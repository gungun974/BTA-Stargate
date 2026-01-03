package gungun974.stargate.core;

import javax.annotation.Nullable;

public class StargatePegasusAddress extends StargateAddress {
	private static final long[] A = {1709L, 3547L, 5179L, 6823L, 8369L};
	private static final long[] B = {983L, 2411L, 3967L, 5501L, 7793L};

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
	protected long feistelF(long r, int round, long S) {
		long t = (r * r) + (A[round] * r) + B[round];
		return mod(t, S);
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
