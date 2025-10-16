package gungun974.stargate.core;

import gungun974.stargate.gate.blocks.core.TileEntityStargateCore;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StargateSessionManager {
	private static StargateSessionManager instance;
	private final List<StargateSession> sessions = new ArrayList<>();

	private StargateSessionManager() {
	}

	public static synchronized StargateSessionManager getInstance() {
		if (instance == null) {
			instance = new StargateSessionManager();
		}

		return instance;
	}

	public void createSession(TileEntityStargateCore origin, TileEntityStargateCore destination, short dialingAddressSize) {
		if (origin == null) {
			return;
		}

		if (destination == null) {
			return;
		}

		if (getSession(origin) != null) {
			return;
		}

		if (getSession(destination) != null) {
			return;
		}

		sessions.add(new StargateSession(
			origin.x,
			origin.y,
			origin.z,
			origin.worldObj.dimension.id,
			origin.getAddress(),
			destination.x,
			destination.y,
			destination.z,
			destination.worldObj.dimension.id,
			destination.getAddress(),
			dialingAddressSize
		));
	}

	@Nullable
	public StargateSession getSession(TileEntityStargateCore gate) {
		if (gate == null) {
			return null;
		}

		int dim = gate.worldObj.dimension.id;

		for (StargateSession session : sessions) {
			if (gate.x == session.originX && gate.y == session.originY && gate.z == session.originZ && dim == session.originDim) {
				return session;
			}
			if (gate.x == session.destinationX && gate.y == session.destinationY && gate.z == session.destinationZ && dim == session.destinationDim) {
				return session;
			}
		}

		return null;
	}
}
