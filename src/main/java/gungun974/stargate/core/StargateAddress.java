package gungun974.stargate.core;

import gungun974.stargate.StargateMod;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class StargateAddress {
	static final int MIN_COORD = -22291;
	static final int MAX_COORD = 22291;
	static final int DIM_MIN = 0;
	static final int DIM_MAX = 31;

	private static final int FEISTEL_ROUNDS = 5;
	private static final long[] A = {1123L, 2377L, 3451L, 4567L, 8911L};
	private static final long[] B = {541L, 1777L, 3221L, 4153L, 6661L};
	public static int NUMBER_OF_SYMBOL = 39;

	public final int x;
	public final int z;
	public final int dim;
	public final int gx;
	public final int gz;

	private StargateAddress(int x, int z, int dim, int gx, int gz) {
		this.x = x;
		this.z = z;
		this.dim = dim;
		this.gx = gx;
		this.gz = gz;
	}

	@Nullable
	public static StargateAddress createAddressFromBlock(int bx, int bz, int dim) {
		int x = Math.floorDiv(bx, distanceBetweenGate());
		int z = Math.floorDiv(bz, distanceBetweenGate());

		int gx = Math.floorDiv(mod(bx, distanceBetweenGate()), 5);
		int gz = Math.floorDiv(mod(bz, distanceBetweenGate()), 5);

		if (x < MIN_COORD || x > MAX_COORD || z < MIN_COORD || z > MAX_COORD
			|| dim < DIM_MIN || dim > DIM_MAX) {
			return null;
		}

		return new StargateAddress(x, z, dim, gx, gz);
	}

	private static int mod(int a, int m) {
		int r = a % m;
		return (r < 0) ? r + m : r;
	}

	private static long mod(long a, long m) {
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

	private static int numberOfSymbol() {
		return 39;
	}

	private static int distanceBetweenGate() {
		return 64;
	}

	private static long feistelF(long r, int round, long S) {
		long t = (r * r) + (A[round] * r) + B[round];
		return mod(t, S);
	}

	private static long[] feistelMix(long ix, long iz, long S) {
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

	private static long[] feistelUnmix(long Lp, long Rp, long S) {
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

	@Nullable
	static public StargateAddress createAddressFromEncoded(int[] address) {
		if (address == null || address.length < 8) {
			return null;
		}

		List<Integer> symbols = new LinkedList<>();
		for (int i = 1; i < numberOfSymbol(); i++) symbols.add(i);

		final int N = symbols.size();

		final long S = (long) MAX_COORD - (long) MIN_COORD + 1L;
		final long plane = S * S;

		if (partialPermutation(N, 6) < plane || (N - 6) < (DIM_MAX - DIM_MIN + 1)) {
			return null;
		}

		long rankXZ = 0L;
		long mult = 1L;
		for (int i = 0; i < 6; i++) {
			int base = symbols.size();
			int idx = symbols.indexOf(address[i]);
			if (idx < 0) {
				return null;
			}
			rankXZ += (long) idx * mult;
			mult *= base;
			symbols.remove(idx);
		}

		if (rankXZ < 0 || rankXZ >= plane) {
			return null;
		}

		int idxDim = symbols.indexOf(address[6]);
		if (idxDim < 0) {
			StargateMod.LOGGER.info("{}", idxDim);
			return null;
		}
		int dim = idxDim + DIM_MIN;
		if (dim > DIM_MAX) {
			return null;
		}
		symbols.remove(idxDim);

		long uz = rankXZ / S;
		long ux = rankXZ % S;

		long[] unmixed = feistelUnmix(ux, uz, S);
		long ix = unmixed[0];
		long iz = unmixed[1];

		int x = (int) (ix + MIN_COORD);
		int z = (int) (iz + MIN_COORD);

		int idxLocal = symbols.indexOf(address[7]);
		if (idxLocal < 0) {
			StargateMod.LOGGER.info("G");
			return null;
		}

		int gx = idxLocal / 5;
		int gz = idxLocal % 5;

		return new StargateAddress(x, z, dim, gx, gz);
	}

	public int getBlockX() {
		return x * distanceBetweenGate() + distanceBetweenGate() / 2;
	}

	public int getBlockZ() {
		return z * distanceBetweenGate() + distanceBetweenGate() / 2;
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

	public int[] encodeAddress() {
		List<Integer> symbols = new LinkedList<>();
		int[] address = new int[9];

		for (int i = 1; i < numberOfSymbol(); i++) symbols.add(i);

		final long S = (long) MAX_COORD - (long) MIN_COORD + 1L;

		long ix = (long) x - MIN_COORD;
		long iz = (long) z - MIN_COORD;

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
}
