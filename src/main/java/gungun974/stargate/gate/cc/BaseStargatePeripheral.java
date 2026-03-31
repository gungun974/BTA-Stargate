package gungun974.stargate.gate.cc;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import gungun974.stargate.core.StargateAddress;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.components.StargateComponent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseStargatePeripheral implements IPeripheral {
	protected IComputerAccess attachedComputer;

	@Override
	public void attach(@NotNull IComputerAccess computer) {
		this.attachedComputer = computer;
	}

	@Override
	public void detach(@NotNull IComputerAccess computer) {
		this.attachedComputer = null;
	}

	protected abstract StargateComponent getStargateComponent() throws LuaException;

	protected abstract boolean shouldAllowFastEncode() throws LuaException;

	protected @NotNull MethodResult waitCommandQueue(ILuaContext context) throws LuaException {
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

	@LuaFunction(mainThread = true)
	public final int getCurrentSymbol() throws LuaException {
		return getStargateComponent().getCurrentSymbol();
	}

	@LuaFunction(mainThread = true)
	public final double getRotation() throws LuaException {
		return getStargateComponent().getCurrentAngle();
	}

	@LuaFunction()
	public final @NotNull MethodResult rotateClockwise(ILuaContext context) throws LuaException {
		if (getStargateComponent().getFamily() == StargateFamily.Pegasus) {
			throw new LuaException("Gate can't rotate, use `moveToSymbol` instead");
		}

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

	@LuaFunction()
	public final @NotNull MethodResult rotateCounterClockwise(ILuaContext context) throws LuaException {
		if (getStargateComponent().getFamily() == StargateFamily.Pegasus) {
			throw new LuaException("Gate can't rotate, use `moveToSymbol` instead");
		}

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

	@LuaFunction()
	public final @NotNull MethodResult stopRotation(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().stopRotation();
			return null;
		});
		return waitCommandQueue(context);
	}

	@LuaFunction(mainThread = true)
	public final int getOpenTime() throws LuaException {
		return getStargateComponent().getOpenTimeDuration();
	}

	@LuaFunction(mainThread = true)
	public final String getFamily() throws LuaException {
		return getStargateComponent().getFamily().name();
	}

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

	@LuaFunction(mainThread = true)
	public final boolean isOutgoingConnection() throws LuaException {
		return getStargateComponent().isReceiverGate();
	}

	@LuaFunction(mainThread = true)
	public final List<Integer> getEngagedAddress() throws LuaException {
		if (getStargateComponent().isReceiverGate()) {
			return new ArrayList<>();
		}
		return Arrays.stream(getStargateComponent().getCurrentDialingAddress()).boxed().collect(Collectors.toList());
	}

	@LuaFunction(mainThread = true)
	public final List<Integer> getConnectedAddress() throws LuaException {
		return Arrays.stream(getStargateComponent().getCurrentDialingAddress()).boxed().collect(Collectors.toList());
	}

	@LuaFunction(mainThread = true)
	public final List<Integer> getLocalAddress() throws LuaException {
		return Arrays.stream(getStargateComponent().getAddress().encodeAddress()).boxed().collect(Collectors.toList());
	}

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

	@LuaFunction(mainThread = true)
	public final List<Integer> locationToAddress(int x, int z, int dim) throws LuaException {
		return Arrays.stream(StargateAddress.createAddressFromBlock(x, z, dim, getStargateComponent().getFamily()).encodeAddress()).boxed().collect(Collectors.toList());
	}

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

	@LuaFunction()
	public final @NotNull MethodResult moveToSymbol(ILuaContext context, int symbol) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().moveToSymbol(symbol);
			return null;
		});
		return waitCommandQueue(context);
	}

	@LuaFunction()
	public final MethodResult encode(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().encode();
			return null;
		});
		return waitCommandQueue(context);
	}

	@LuaFunction()
	public final @NotNull MethodResult fastEncode(ILuaContext context, int symbol) throws LuaException {
		if (!shouldAllowFastEncode()) {
			throw new LuaException("Fast encode is not available for this gate");
		}
		context.issueMainThreadTask(() -> {
			getStargateComponent().fastEncode(symbol);
			return null;
		});
		return waitCommandQueue(context);
	}

	@LuaFunction()
	public final @NotNull MethodResult removeSymbol(ILuaContext context, int symbol) throws LuaException {
		if (!shouldAllowFastEncode()) {
			throw new LuaException("Remove symbol is not available for this gate");
		}
		context.issueMainThreadTask(() -> {
			getStargateComponent().removeSymbol(symbol);
			return null;
		});
		return waitCommandQueue(context);
	}

	@LuaFunction()
	public final @NotNull MethodResult dial(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().dial();
			return null;
		});
		return waitCommandQueue(context);
	}

	@LuaFunction()
	public final @NotNull MethodResult cancel(ILuaContext context) throws LuaException {
		context.issueMainThreadTask(() -> {
			getStargateComponent().cancelDial();
			return null;
		});
		return waitCommandQueue(context);
	}

	@LuaFunction(mainThread = true)
	public final void disconnect() throws LuaException {
		if (getStargateComponent().isReceiverGate()) {
			throw new LuaException("Can't disconnect incoming connection");
		}
		getStargateComponent().closeGate();
	}
}
