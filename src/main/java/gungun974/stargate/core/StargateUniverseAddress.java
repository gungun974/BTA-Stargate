package gungun974.stargate.core;

import javax.annotation.Nullable;

public class StargateUniverseAddress extends StargateAddress {
	private static final long[] A = {2749L, 4391L, 5843L, 7219L, 9437L};
	private static final long[] B = {1259L, 2803L, 4657L, 6173L, 8527L};

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
		return 736;
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.Universe;
	}
}
