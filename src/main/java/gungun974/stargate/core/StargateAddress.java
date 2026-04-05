package gungun974.stargate.core;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public abstract class StargateAddress {
	private static final int FEISTEL_ROUNDS = 5;
	private static final long[] A = {1123L, 2377L, 3451L, 4567L, 8911L};
	private static final long[] B = {541L, 1777L, 3221L, 4153L, 6661L};

	private final int computedMinCoord;
	private final int computedMaxCoord;
	private final int computedMaxDim;

	private int x;
	private int z;
	private int dim;
	private int gx;
	private int gz;

	protected StargateAddress() {
		int n = numberOfSymbol();

		long quantity = partialPermutation(n - 1, 6);

		int maxCoord = (int) (Math.sqrt(quantity) / 2);

		this.computedMaxCoord = maxCoord;
		this.computedMinCoord = -maxCoord;
		this.computedMaxDim = n - 8;
	}

	private static int mod(int a, int m) {
		int r = a % m;
		return (r < 0) ? r + m : r;
	}

	protected static long mod(long a, long m) {
		long r = a % m;
		return (r < 0) ? r + m : r;
	}

	private static long partialPermutation(int n, int k) {
		long res = 1L;
		for (int i = 0; i < k; i++) {
			int f = n - i;
			if (f <= 0) return 0L;
			if (res > Long.MAX_VALUE / f) return Long.MAX_VALUE;
			res *= f;
		}
		return res;
	}

	@Nullable
	public static StargateAddress createAddressFromBlock(int x, int y, int dim, StargateFamily family) {
		switch (family) {
			case MilkyWay:
				return StargateMilkyWayAddress.createAddressFromBlock(x, y, dim);
			case Pegasus:
				return StargatePegasusAddress.createAddressFromBlock(x, y, dim);
			case Universe:
				return StargateUniverseAddress.createAddressFromBlock(x, y, dim);
			default:
				throw new IllegalStateException("Unhandled family: " + family);
		}
	}

	public static StargateAddress createAddressFromEncoded(int[] rawAddress, StargateFamily family) {
		switch (family) {
			case MilkyWay:
				return StargateMilkyWayAddress.createAddressFromEncoded(rawAddress);
			case Pegasus:
				return StargatePegasusAddress.createAddressFromEncoded(rawAddress);
			case Universe:
				return StargateUniverseAddress.createAddressFromEncoded(rawAddress);
			default:
				throw new IllegalStateException("Unhandled family: " + family);
		}
	}

	private long[] feistelMix(long ix, long iz, long S) {
		long L = mod(ix, S);
		long R = mod(iz, S);
		for (int i = 0; i < FEISTEL_ROUNDS; i++) {
			long F = feistelF(R, i, S);
			long newR = mod(L + F, S);
			L = R;
			R = newR;
		}
		return new long[]{L, R};
	}

	private long[] feistelUnmix(long Lp, long Rp, long S) {
		long L = mod(Lp, S);
		long R = mod(Rp, S);
		for (int i = FEISTEL_ROUNDS - 1; i >= 0; i--) {
			long prevR = L;
			long F = feistelF(prevR, i, S);
			L = mod(R - F, S);
			R = prevR;
		}
		return new long[]{L, R};
	}

	protected long feistelF(long r, int round, long S) {
		long t = (r * r) + (A[round] * r) + B[round];
		return mod(t, S);
	}

	protected void setAddressFromEncoded(int[] address) {
		if (address == null || address.length < 7) {
			throw new IllegalArgumentException();
		}

		List<Integer> symbols = new LinkedList<>();
		for (int i = 1; i < numberOfSymbol(); i++) symbols.add(i);

		final int N = symbols.size();

		final long S = (long) computedMaxCoord - (long) computedMinCoord + 1L;
		final long plane = S * S;

		long rankXZ = 0L;
		long mult = 1L;
		for (int i = 0; i < 6; i++) {
			int base = symbols.size();
			int idx = symbols.indexOf(address[i]);
			if (idx < 0) {
				throw new IllegalArgumentException();
			}
			rankXZ += (long) idx * mult;
			mult *= base;
			symbols.remove(idx);
		}

		if (rankXZ < 0 || rankXZ >= plane) {
			throw new IllegalArgumentException();
		}

		int idxDim = symbols.indexOf(address[6]);
		if (idxDim < 0) {
			throw new IllegalArgumentException();
		}
		int dim = idxDim;
		if (dim > computedMaxDim) {
			throw new IllegalArgumentException();
		}
		symbols.remove(idxDim);

		long uz = rankXZ / S;
		long ux = rankXZ % S;

		long[] unmixed = feistelUnmix(ux, uz, S);
		long ix = unmixed[0];
		long iz = unmixed[1];

		int x = (int) (ix + computedMinCoord);
		int z = (int) (iz + computedMinCoord);

		int idxLocal = symbols.indexOf(address[7]);
		this.x = x;
		this.z = z;
		this.dim = dim;

		if (idxLocal < 0) {
			this.gx = 2;
			this.gz = 2;
			return;
		}

		this.gx = idxLocal / 5;
		this.gz = idxLocal % 5;
	}

	protected void setAddressFromBlock(int bx, int bz, int dim) {
		int x = Math.floorDiv(bx, distanceBetweenGate());
		int z = Math.floorDiv(bz, distanceBetweenGate());

		int gx = Math.floorDiv(mod(bx, distanceBetweenGate()), 5);
		int gz = Math.floorDiv(mod(bz, distanceBetweenGate()), 5);

		if (x < computedMinCoord || x > computedMaxCoord || z < computedMinCoord || z > computedMaxCoord
			|| dim < 0 || dim > computedMaxDim) {
			throw new IllegalArgumentException();
		}

		this.x = x;
		this.z = z;
		this.dim = dim;
		this.gx = gx;
		this.gz = gz;
	}

	protected abstract int numberOfSymbol();

	protected abstract int distanceBetweenGate();

	public int getBlockX() {
		return x * distanceBetweenGate() + distanceBetweenGate() * gx / 5;
	}

	public int getBlockZ() {
		return z * distanceBetweenGate() + distanceBetweenGate() * gz / 5;
	}

	public int getStartChunkX() {
		return x * distanceBetweenGate() / 16;
	}

	public int getStartChunkZ() {
		return z * distanceBetweenGate() / 16;
	}

	public int getEndChunkX() {
		return (x + 1) * distanceBetweenGate() / 16 - 1;
	}

	public int getEndChunkZ() {
		return (z + 1) * distanceBetweenGate() / 16 - 1;
	}

	public int getDim() {
		return dim;
	}

	public int[] encodeAddress() {
		List<Integer> symbols = new LinkedList<>();
		int[] address = new int[9];

		for (int i = 1; i < numberOfSymbol(); i++) symbols.add(i);

		final long S = (long) computedMaxCoord - (long) computedMinCoord + 1L;

		long ix = (long) x - computedMinCoord;
		long iz = (long) z - computedMinCoord;

		long[] mixed = feistelMix(ix, iz, S);
		long ux = mixed[0];
		long uz = mixed[1];

		long rankXZ = ux + S * uz;

		for (int i = 0; i < 6; i++) {
			int base = symbols.size();
			int idx = (int) (rankXZ % base);
			rankXZ /= base;
			address[i] = symbols.remove(idx);
		}

		address[6] = symbols.remove(dim);

		int remaining = symbols.size();
		int encoded = mod((gx * 5) + gz, remaining);
		address[7] = symbols.remove(encoded);

		address[8] = 0;

		return address;
	}

	public abstract StargateFamily getFamily();

	public boolean isSameRegion(StargateAddress targetAddress) {
		return this.x == targetAddress.x && this.z == targetAddress.z && this.dim == targetAddress.dim;
	}

	public boolean isSameSubRegion(StargateAddress targetAddress) {
		return this.gx == targetAddress.gx && this.gz == targetAddress.gz;
	}
}
