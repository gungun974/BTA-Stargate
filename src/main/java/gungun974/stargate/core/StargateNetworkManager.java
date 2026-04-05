package gungun974.stargate.core;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.IntTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StargateNetworkManager {
	private static StargateNetworkManager instance;

	private final Map<StargateNetworkGate, TileEntityStargate> publicMilkyWayGates = new HashMap<>();
	private final Map<StargateNetworkGate, TileEntityStargate> publicPegasusGates = new HashMap<>();
	private final Map<StargateNetworkGate, TileEntityStargate> publicUniverseGates = new HashMap<>();

	private StargateNetworkManager() {
	}

	public static synchronized StargateNetworkManager getInstance() {
		if (instance == null) {
			instance = new StargateNetworkManager();
		}

		return instance;
	}

	private @NotNull CompoundTag createNetworkNBTData(Map<StargateNetworkGate, TileEntityStargate> gates) {
		CompoundTag networkTag = new CompoundTag();

		int gateIndex = 0;
		for (Map.Entry<StargateNetworkGate, TileEntityStargate> entry : gates.entrySet()) {
			StargateNetworkGate gate = entry.getKey();
			TileEntityStargate tileEntity = entry.getValue();
			CompoundTag gateTag = new CompoundTag();

			gateTag.putInt("X", gate.x);
			gateTag.putInt("Y", gate.y);
			gateTag.putInt("Z", gate.z);
			gateTag.putInt("Dim", gate.dim);
			gateTag.putString("Family", gate.address.getFamily().name());

			int[] addressArray = gate.address.encodeAddress();
			ListTag addressList = new ListTag();
			for (int symbol : addressArray) {
				addressList.addTag(new IntTag(symbol));
			}
			gateTag.putList("Address", addressList);

			if (tileEntity != null) {
				CompoundTag tileEntityTag = new CompoundTag();
				tileEntity.writeToNBT(tileEntityTag);
				gateTag.putCompound("TileEntity", tileEntityTag);
			}

			networkTag.put(String.valueOf(gateIndex), gateTag);
			gateIndex++;
		}

		return networkTag;
	}

	private void loadNetworkNBTData(CompoundTag networkTag, Map<StargateNetworkGate, TileEntityStargate> gates) {
		gates.clear();

		for (Map.Entry<String, Tag<?>> entry : networkTag.getValue().entrySet()) {
			try {
				final Tag<?> tag = entry.getValue();
				if (!(tag instanceof CompoundTag)) {
					continue;
				}

				final CompoundTag gateTag = (CompoundTag) tag;

				int x = gateTag.getInteger("X");
				int y = gateTag.getInteger("Y");
				int z = gateTag.getInteger("Z");
				int dim = gateTag.getInteger("Dim");
				String familyName = gateTag.getString("Family");
				ListTag addressList = gateTag.getList("Address");

				if (familyName == null || familyName.isEmpty() || addressList == null) {
					continue;
				}

				int[] addressArray = new int[addressList.tagCount()];
				for (int i = 0; i < addressList.tagCount(); i++) {
					Tag<?> symbolTag = addressList.tagAt(i);
					if (symbolTag instanceof IntTag) {
						addressArray[i] = (Integer) symbolTag.getValue();
					}
				}

				StargateFamily family = StargateFamily.valueOf(familyName);
				StargateAddress address = StargateAddress.createAddressFromEncoded(addressArray, family);

				if (address == null) {
					continue;
				}

				TileEntityStargate tileEntity = null;
				CompoundTag tileEntityTag = gateTag.getCompound("TileEntity");
				if (tileEntityTag != null) {
					TileEntity loadedEntity = TileEntityDispatcher.createAndLoadEntity(tileEntityTag);
					if (loadedEntity instanceof TileEntityStargate) {
						tileEntity = (TileEntityStargate) loadedEntity;
					}
				}

				if (tileEntity == null) {
					continue;
				}

				StargateNetworkGate gate = new StargateNetworkGate(x, y, z, dim, address);
				gates.put(gate, tileEntity);

			} catch (Exception ignored) {
			}
		}
	}

	public @NotNull CompoundTag createNBTData() {
		CompoundTag rootTag = new CompoundTag();

		CompoundTag publicTag = new CompoundTag();

		publicTag.putCompound("MilkyWay", createNetworkNBTData(publicMilkyWayGates));
		publicTag.putCompound("Pegasus", createNetworkNBTData(publicPegasusGates));
		publicTag.putCompound("Universe", createNetworkNBTData(publicUniverseGates));

		rootTag.putCompound("Public", publicTag);

		return rootTag;
	}

	public void loadNBTData(CompoundTag rootTag) {
		CompoundTag publicTag = rootTag.getCompound("Public");
		if (publicTag != null) {
			CompoundTag milkyWayTag = publicTag.getCompound("MilkyWay");
			if (milkyWayTag != null) {
				loadNetworkNBTData(milkyWayTag, publicMilkyWayGates);
			}

			CompoundTag pegasusTag = publicTag.getCompound("Pegasus");
			if (pegasusTag != null) {
				loadNetworkNBTData(pegasusTag, publicPegasusGates);
			}

			CompoundTag universeTag = publicTag.getCompound("Universe");
			if (universeTag != null) {
				loadNetworkNBTData(universeTag, publicUniverseGates);
			}
		}
	}

	public void registerPublicStargate(TileEntityStargate tileEntity) {
		StargateComponent gate = tileEntity.getStargateComponent();

		if (gate == null) {
			return;
		}

		publicMilkyWayGates.put(new StargateNetworkGate(tileEntity.x, tileEntity.y, tileEntity.z, tileEntity.dim, gate.getAddressWithFamily(StargateFamily.MilkyWay)), tileEntity);
		publicPegasusGates.put(new StargateNetworkGate(tileEntity.x, tileEntity.y, tileEntity.z, tileEntity.dim, gate.getAddressWithFamily(StargateFamily.Pegasus)), tileEntity);
		publicUniverseGates.put(new StargateNetworkGate(tileEntity.x, tileEntity.y, tileEntity.z, tileEntity.dim, gate.getAddressWithFamily(StargateFamily.Universe)), tileEntity);
	}

	public void unregisterPublicStargate(TileEntityStargate tileEntity) {
		StargateComponent gate = tileEntity.getStargateComponent();

		if (gate == null) {
			return;
		}

		publicMilkyWayGates.remove(new StargateNetworkGate(tileEntity.x, tileEntity.y, tileEntity.z, tileEntity.dim, gate.getAddressWithFamily(StargateFamily.MilkyWay)));
		publicPegasusGates.remove(new StargateNetworkGate(tileEntity.x, tileEntity.y, tileEntity.z, tileEntity.dim, gate.getAddressWithFamily(StargateFamily.Pegasus)));
		publicUniverseGates.remove(new StargateNetworkGate(tileEntity.x, tileEntity.y, tileEntity.z, tileEntity.dim, gate.getAddressWithFamily(StargateFamily.Universe)));
	}

	public List<TileEntityStargate> findStargates(StargateAddress targetAddress, boolean subGrid) {
		Map<StargateNetworkGate, TileEntityStargate> gates;

		if (targetAddress instanceof StargateMilkyWayAddress) {
			gates = publicMilkyWayGates;
		} else if (targetAddress instanceof StargatePegasusAddress) {
			gates = publicPegasusGates;
		} else {
			gates = publicUniverseGates;
		}

		List<TileEntityStargate> tileEntityStargates = new ArrayList<>();

		for (Map.Entry<StargateNetworkGate, TileEntityStargate> gate : gates.entrySet()) {
			StargateAddress stargateAddress = gate.getKey().address;
			if (stargateAddress.isSameRegion(targetAddress) && (!subGrid || stargateAddress.isSameSubRegion(targetAddress))) {
				tileEntityStargates.add(gate.getValue());
			}
		}

		return tileEntityStargates;
	}

	private static class StargateNetworkGate {
		final public int x;
		final public int y;
		final public int z;
		final public int dim;

		final public StargateAddress address;

		private StargateNetworkGate(int x, int y, int z, int dim, StargateAddress address) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dim = dim;
			this.address = address;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			StargateNetworkGate that = (StargateNetworkGate) obj;
			return x == that.x && y == that.y && z == that.z && dim == that.dim;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			result = 31 * result + z;
			result = 31 * result + dim;
			return result;
		}
	}
}
