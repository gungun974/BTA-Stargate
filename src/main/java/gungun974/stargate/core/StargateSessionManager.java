package gungun974.stargate.core;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.util.helper.Direction;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StargateSessionManager {
	private static final int MAX_OPENING_TIME = 20 * 60 * 38;
	private static final int FAST_CLOSING_TIME = 20 * 5;

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

	public @NotNull CompoundTag createNBTData() {
		CompoundTag sessionsTag = new CompoundTag();

		for (int i = 0; i < sessions.size(); i++) {
			StargateSession session = sessions.get(i);

			CompoundTag sessionTag = new CompoundTag();

			sessionTag.putInt("OriginX", session.originX);
			sessionTag.putInt("OriginY", session.originY);
			sessionTag.putInt("OriginZ", session.originZ);
			sessionTag.putInt("OriginDirection", session.originDirection.ordinal());
			sessionTag.putInt("OriginOrientation", session.originOrientation.ordinal());
			sessionTag.putInt("OriginDim", session.originDim);

			int[] originAddress = session.originAddress.encodeAddress();

			sessionTag.putInt("OriginAddress0", originAddress[0]);
			sessionTag.putInt("OriginAddress1", originAddress[1]);
			sessionTag.putInt("OriginAddress2", originAddress[2]);
			sessionTag.putInt("OriginAddress3", originAddress[3]);
			sessionTag.putInt("OriginAddress4", originAddress[4]);
			sessionTag.putInt("OriginAddress5", originAddress[5]);
			sessionTag.putInt("OriginAddress6", originAddress[6]);
			sessionTag.putInt("OriginAddress7", originAddress[7]);
			sessionTag.putInt("OriginAddressFamily", session.originAddress.getFamily().ordinal());

			sessionTag.putInt("DestinationX", session.destinationX);
			sessionTag.putInt("DestinationY", session.destinationY);
			sessionTag.putInt("DestinationZ", session.destinationZ);
			sessionTag.putInt("DestinationDirection", session.destinationDirection.ordinal());
			sessionTag.putInt("DestinationOrientation", session.destinationOrientation.ordinal());
			sessionTag.putInt("DestinationDim", session.destinationDim);

			int[] destinationAddress = session.destinationAddress.encodeAddress();

			sessionTag.putInt("DestinationAddress0", destinationAddress[0]);
			sessionTag.putInt("DestinationAddress1", destinationAddress[1]);
			sessionTag.putInt("DestinationAddress2", destinationAddress[2]);
			sessionTag.putInt("DestinationAddress3", destinationAddress[3]);
			sessionTag.putInt("DestinationAddress4", destinationAddress[4]);
			sessionTag.putInt("DestinationAddress5", destinationAddress[5]);
			sessionTag.putInt("DestinationAddress6", destinationAddress[6]);
			sessionTag.putInt("DestinationAddress7", destinationAddress[7]);
			sessionTag.putInt("DestinationFamily", session.destinationAddress.getFamily().ordinal());

			sessionTag.putShort("DialingAddressSize", session.dialingAddressSize);

			sessionTag.putInt("RemainingTick", session.remainingTick);
			sessionTag.putInt("OpenTick", session.openTick);
			sessionTag.putInt("FastCloseTick", session.fastCloseTick);
			sessionTag.putBoolean("FastClosing", session.fastClosing);

			sessionsTag.put(String.valueOf(i), sessionTag);
		}

		return sessionsTag;
	}


	public void loadNBTData(CompoundTag sessionsTag) {
		sessions.clear();

		for (Map.Entry<String, Tag<?>> entry : sessionsTag.getValue().entrySet()) {
			try {
				final Tag<?> tag = entry.getValue();
				if (!(tag instanceof CompoundTag)) {
					continue;
				}

				final CompoundTag sessionTag = (CompoundTag) tag;

				sessions.add(new StargateSession(
					sessionTag.getInteger("OriginX"),
					sessionTag.getInteger("OriginY"),
					sessionTag.getInteger("OriginZ"),
					sessionTag.getInteger("OriginDim"),
					Direction.values()[sessionTag.getInteger("OriginDirection")],
					Direction.values()[sessionTag.getInteger("OriginOrientation")],
					StargateAddress.createAddressFromEncoded(new int[]{
							sessionTag.getInteger("OriginAddress0"),
							sessionTag.getInteger("OriginAddress1"),
							sessionTag.getInteger("OriginAddress2"),
							sessionTag.getInteger("OriginAddress3"),
							sessionTag.getInteger("OriginAddress4"),
							sessionTag.getInteger("OriginAddress5"),
							sessionTag.getInteger("OriginAddress6"),
							sessionTag.getInteger("OriginAddress7")
						},
						StargateFamily.values()[sessionTag.getInteger("OriginAddressFamily")]
					),
					sessionTag.getInteger("DestinationX"),
					sessionTag.getInteger("DestinationY"),
					sessionTag.getInteger("DestinationZ"),
					sessionTag.getInteger("DestinationDim"),
					Direction.values()[sessionTag.getInteger("DestinationDirection")],
					Direction.values()[sessionTag.getInteger("DestinationOrientation")],
					StargateAddress.createAddressFromEncoded(new int[]{
							sessionTag.getInteger("DestinationAddress0"),
							sessionTag.getInteger("DestinationAddress1"),
							sessionTag.getInteger("DestinationAddress2"),
							sessionTag.getInteger("DestinationAddress3"),
							sessionTag.getInteger("DestinationAddress4"),
							sessionTag.getInteger("DestinationAddress5"),
							sessionTag.getInteger("DestinationAddress6"),
							sessionTag.getInteger("DestinationAddress7"),
						},
						StargateFamily.values()[sessionTag.getInteger("DestinationAddressFamily")]
					),
					sessionTag.getShort("DialingAddressSize"),
					sessionTag.getInteger("RemainingTick"),
					sessionTag.getInteger("OpenTick"),
					sessionTag.getInteger("FastCloseTick"),
					sessionTag.getBoolean("FastClosing")
				));
			} catch (Exception ignored) {
			}
		}
	}


	public void createSession(StargateComponent origin, StargateComponent destination, short dialingAddressSize) {
		if (origin.stargateTile == null) {
			return;
		}

		if (destination.stargateTile == null) {
			return;
		}

		if (getSession(origin) != null) {
			return;
		}

		if (getSession(destination) != null) {
			return;
		}

		sessions.add(new StargateSession(
			origin.stargateTile.x,
			origin.stargateTile.y,
			origin.stargateTile.z,
			origin.stargateTile.dim,
			origin.getDirection(),
			origin.getOrientation(),
			origin.getAddress(),
			destination.stargateTile.x,
			destination.stargateTile.y,
			destination.stargateTile.z,
			destination.stargateTile.dim,
			destination.getDirection(),
			destination.getOrientation(),
			destination.getAddressWithFamily(origin.getFamily()),
			dialingAddressSize,
			MAX_OPENING_TIME,
			0,
			0,
			false
		));
	}

	@Nullable
	public StargateSession getSession(StargateComponent gate) {
		TileEntityStargate tile = gate.stargateTile;

		if (tile == null) {
			return null;
		}

		int dim = tile.dim;

		for (StargateSession session : sessions) {
			if (tile.x == session.originX && tile.y == session.originY && tile.z == session.originZ && dim == session.originDim) {
				return session;
			}
			if (tile.x == session.destinationX && tile.y == session.destinationY && tile.z == session.destinationZ && dim == session.destinationDim) {
				return session;
			}
		}

		return null;
	}

	public void removeSession(StargateComponent gate) {
		TileEntityStargate tile = gate.stargateTile;

		if (tile == null) {
			return;
		}

		int dim = tile.dim;

		Iterator<StargateSession> iterator = sessions.iterator();

		while (iterator.hasNext()) {
			StargateSession session = iterator.next();

			if (tile.x == session.originX && tile.y == session.originY && tile.z == session.originZ && dim == session.originDim) {
				iterator.remove();
				return;
			}

			if (tile.x == session.destinationX && tile.y == session.destinationY && tile.z == session.destinationZ && dim == session.destinationDim) {
				iterator.remove();
				return;
			}
		}

	}

	public void endSession(StargateComponent gate) {
		TileEntityStargate tile = gate.stargateTile;

		if (tile == null) {
			return;
		}

		int dim = tile.dim;

		for (StargateSession session : sessions) {
			if (tile.x == session.originX && tile.y == session.originY && tile.z == session.originZ && dim == session.originDim) {
				session.fastClosing = true;
				return;
			}

			if (tile.x == session.destinationX && tile.y == session.destinationY && tile.z == session.destinationZ && dim == session.destinationDim) {
				session.fastClosing = true;
				return;
			}
		}
	}

	public void resetEndSession(StargateComponent gate) {
		TileEntityStargate tile = gate.stargateTile;

		if (tile == null) {
			return;
		}

		int dim = tile.dim;

		for (StargateSession session : sessions) {
			if (tile.x == session.originX && tile.y == session.originY && tile.z == session.originZ && dim == session.originDim) {
				session.fastCloseTick = 0;
				return;
			}

			if (tile.x == session.destinationX && tile.y == session.destinationY && tile.z == session.destinationZ && dim == session.destinationDim) {
				session.fastCloseTick = 0;
				return;
			}
		}
	}

	public void tick() {
		if (EnvironmentHelper.isClientWorld()) {
			return;
		}

		Iterator<StargateSession> iterator = sessions.iterator();

		while (iterator.hasNext()) {
			StargateSession session = iterator.next();

			session.remainingTick -= 1;
			session.openTick += 1;
			if (session.fastClosing) {
				session.fastCloseTick += 1;
			}

			if (session.remainingTick <= 0) {
				iterator.remove();
			} else if (session.fastCloseTick >= FAST_CLOSING_TIME) {
				iterator.remove();
			}
		}
	}
}
