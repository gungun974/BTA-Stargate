package gungun974.stargate.core;

import gungun974.stargate.StargateMod;

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
			int[] decodedAddress = new int[rawAddress.length];
			for (int i = 0; i < rawAddress.length; i++) {
				if (rawAddress[i] >= 13) {
					decodedAddress[i] = rawAddress[i] - 1;
				} else {
					decodedAddress[i] = rawAddress[i];
				}
			}
			address.setAddressFromEncoded(decodedAddress);
			return address;
		} catch (RuntimeException e) {
			StargateMod.LOGGER.info("{}", e);
			return null;
		}
	}

	@Override
	public int[] encodeAddress() {
		int[] internalAddress = super.encodeAddress();
		int[] encodedAddress = new int[internalAddress.length];
		for (int i = 0; i < internalAddress.length; i++) {
			if (internalAddress[i] >= 12) {
				encodedAddress[i] = internalAddress[i] + 1;
			} else {
				encodedAddress[i] = internalAddress[i];
			}
		}
		return encodedAddress;
	}

	@Override
	protected int numberOfSymbol() {
		return NUMBER_OF_SYMBOL - 1;
	}

	@Override
	protected int distanceBetweenGate() {
		return 64;
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.MilkyWay;
	}
}
