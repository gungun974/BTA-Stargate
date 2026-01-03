package gungun974.stargate.gate.cc;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import gungun974.stargate.core.StargateAddress;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class StargatePeripheral implements IPeripheral {
	private final TileEntityStargate stargate;

	private IComputerAccess attachedComputer;

	public StargatePeripheral(TileEntityStargate stargate) {
		this.stargate = stargate;
	}

	@Override
	public void attach(@NotNull IComputerAccess computer) {
		this.attachedComputer = computer;
	}

	@Override
	public void detach(@NotNull IComputerAccess computer) {
		this.attachedComputer = null;
	}

	private StargateComponent getStargateComponent() throws LuaException {
		StargateComponent stargateComponent = stargate.findMainStargateComponent();

		if (stargateComponent == null) {
			throw new LuaException("Stargate controller not found");
		}

		return stargateComponent;
	}

	@Override
	public @NotNull String getType() {
		return "stargate";
	}

	@Override
	public boolean equals(IPeripheral other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof StargatePeripheral)) {
			return false;
		}

		return stargate.findMainStargateComponent() == ((StargatePeripheral) other).stargate.findMainStargateComponent();
	}

	private @NotNull MethodResult waitCommandQueue(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().waitCommandQueue(() -> {
				if (attachedComputer == null) {
					return;
				}

				attachedComputer.queueEvent("stargate_command_queue", "done");
			});
			return null;
		});

		return MethodResult.pullEvent("stargate_command_queue", new ILuaCallback() {
				@Override
				public @NotNull MethodResult resume(Object[] args) throws LuaException {
					return MethodResult.of(args[1]);
				}
			}
		);
	}

	/**
	 * Return the current symbol to encode
	 */
	@LuaFunction(mainThread = true)
	public final int getCurrentSymbol() throws LuaException {
		return getStargateComponent().getCurrentSymbol();
	}

	/**
	 * Return the current rotation angle
	 */
	@LuaFunction(mainThread = true)
	public final double getRotation() throws LuaException {
		return getStargateComponent().getCurrentAngle();
	}

	/**
	 * Rotate the ring in a clockwise fashion until stop
	 */
	@LuaFunction()
	public final @NotNull MethodResult rotateClockwise(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().rotateClockwise(
				() -> {
					if (attachedComputer == null) {
						return;
					}

					attachedComputer.queueEvent("stargate_command_queue", "done");
				}
			);
			return null;
		});
		return MethodResult.pullEvent("stargate_command_queue", new ILuaCallback() {
			@Override
			public @NotNull MethodResult resume(Object[] args) throws LuaException {
				return MethodResult.of(args[1]);
			}
		});
	}

	/**
	 * Rotate the ring in a counterclockwise fashion until stop
	 */
	@LuaFunction()
	public final @NotNull MethodResult rotateCounterClockwise(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().rotateCounterClockwise(
				() -> {
					if (attachedComputer == null) {
						return;
					}

					attachedComputer.queueEvent("stargate_command_queue", "done");
				}
			);
			return null;
		});
		return MethodResult.pullEvent("stargate_command_queue", new ILuaCallback() {
			@Override
			public @NotNull MethodResult resume(Object[] args) throws LuaException {
				return MethodResult.of(args[1]);
			}
		});
	}

	/**
	 * Stop the ring from rotate
	 */
	@LuaFunction()
	public final @NotNull MethodResult stopRotation(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().stopRotation();
			return null;
		});
		return waitCommandQueue(context);
	}

	/**
	 * Return in tick the time the gate have been open (0 if not open)
	 */
	@LuaFunction(mainThread = true)
	public final int getOpenTime() throws LuaException {
		return getStargateComponent().getOpenTimeDuration();
	}

	/**
	 * Return the gate family name
	 */
	@LuaFunction(mainThread = true)
	public final String getFamily() throws LuaException {
		return getStargateComponent().getFamily().name();
	}

	/**
	 * Check if the gate is connected to somewhere
	 */
	@LuaFunction(mainThread = true)
	public final boolean isStargateConnected() throws LuaException {
		switch (getStargateComponent().getState()) {
			case IDLE:
			case DIALLING:
			case AWAIT:
				return false;
			case OPENING:
			case CONNECTED:
			case CLOSING:
				return true;
		}
		return false;
	}

	/**
	 * Check if the current connection is incoming (false if not open)
	 */
	@LuaFunction(mainThread = true)
	public final boolean isOutgoingConnection() throws LuaException {
		return getStargateComponent().isReceiverGate();
	}

	/**
	 * Return a table with the current dialed address (return an empty table if it's an incoming connection)
	 */
	@LuaFunction(mainThread = true)
	public final List<Integer> getEngagedAddress() throws LuaException {
		if (getStargateComponent().isReceiverGate()) {
			return new ArrayList<>();
		}
		return Arrays.stream(getStargateComponent().getCurrentDialingAddress()).boxed().collect(Collectors.toList());
	}

	/**
	 * Return a table with the current connected address
	 */
	@LuaFunction(mainThread = true)
	public final List<Integer> getConnectedAddress() throws LuaException {
		return Arrays.stream(getStargateComponent().getCurrentDialingAddress()).boxed().collect(Collectors.toList());
	}

	/**
	 * Return a table with the gate address in its family type
	 */
	@LuaFunction(mainThread = true)
	public final List<Integer> getLocalAddress() throws LuaException {
		return Arrays.stream(getStargateComponent().getAddress().encodeAddress()).boxed().collect(Collectors.toList());
	}

	/**
	 * Use the inboard stargate stellar address computer to convert a local address into world coordinate
	 */
	@LuaFunction(mainThread = true)
	public final Object[] addressToLocation(Object table) throws LuaException {

		if (!(table instanceof Map)) {
			throw new LuaException("Expected table");
		}

		@SuppressWarnings("unchecked")
		Map<Object, Object> map = (Map<Object, Object>) table;

		int size = map.size();
		int[] addr = new int[Math.max(size, 9)];

		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (!(key instanceof Number)) {
				throw new LuaException("Table key is not numeric: " + key);
			}
			if (!(value instanceof Number)) {
				throw new LuaException("Table value is not numeric: " + value);
			}

			int index = ((Number) key).intValue();
			if (index < 1 || index > size) {
				throw new LuaException("Invalid index: " + index);
			}

			addr[index - 1] = ((Number) value).intValue();
		}

		if (size == 7) {
			int[] originAddress = getStargateComponent().getAddress().encodeAddress();

			addr[6] = originAddress[6];
			addr[7] = 0;
			addr[8] = 0;
		} else if (size == 8) {
			addr[7] = 0;
			addr[8] = 0;
		}

		StargateAddress stargateAddress =
			StargateAddress.createAddressFromEncoded(
				addr,
				getStargateComponent().getFamily()
			);

		if (stargateAddress == null) {
			throw new LuaException("Can't compute stargate address");
		}

		return new Object[]{
			stargateAddress.getBlockX(),
			stargateAddress.getBlockZ(),
			stargateAddress.getDim()
		};
	}


	/**
	 * Use the inboard stargate stellar address computer to convert world coordinate into local address
	 */
	@LuaFunction(mainThread = true)
	public final List<Integer> locationToAddress(int x, int z, int dim) throws LuaException {
		return Arrays.stream(StargateAddress.createAddressFromBlock(x, z, dim, getStargateComponent().getFamily()).encodeAddress()).boxed().collect(Collectors.toList());
	}

	/**
	 * Return a table map with the gate addresses for each family type
	 */
	@LuaFunction(mainThread = true)
	public final Map<String, List<Integer>> getAddresses() throws LuaException {
		Map<String, List<Integer>> addresses = new HashMap<>();

		for (StargateFamily family : StargateFamily.values()) {
			int[] encoded = getStargateComponent().getAddressWithFamily(family).encodeAddress();
			List<Integer> addressList = Arrays.stream(encoded).boxed().collect(Collectors.toList());
			addresses.put(family.name(), addressList);
		}

		return addresses;
	}

	/**
	 * Pre automate command to move the gate to a specific symbol
	 */
	@LuaFunction()
	public final @NotNull MethodResult moveToSymbol(ILuaContext context, int symbol) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().moveToSymbol(symbol);
			return null;
		});
		return waitCommandQueue(context);
	}

	/**
	 * Encode the current symbol at the top of the gate
	 */
	@LuaFunction()
	public final MethodResult encode(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().encode();
			return null;
		});
		return waitCommandQueue(context);
	}

	/**
	 * Encode a symbol directly into the gate
	 */
	@LuaFunction()
	public final @NotNull MethodResult fastEncode(ILuaContext context, int symbol) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().fastEncode(symbol);
			return null;
		});
		return waitCommandQueue(context);
	}

	/**
	 * Dial the current encoded symbols
	 */
	@LuaFunction()
	public final @NotNull MethodResult dial(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().dial();
			return null;
		});
		return waitCommandQueue(context);
	}

	/**
	 * Disconnect the gate if connected
	 */
	@LuaFunction(mainThread = true)
	public final void disconnect() throws LuaException {
		if (getStargateComponent().isReceiverGate()) {
			throw new LuaException("Can't disconnect incoming connection");
		}
		getStargateComponent().closeGate();
	}
}
