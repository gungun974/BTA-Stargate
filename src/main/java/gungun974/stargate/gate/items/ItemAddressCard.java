package gungun974.stargate.gate.items;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.StargateItems;
import gungun974.stargate.core.StargateAddress;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import turniplabs.halplibe.helper.EnvironmentHelper;

import javax.annotation.Nonnull;

public class ItemAddressCard extends Item {
	private static final String NBT_ADDRESS_MILKYWAY = "AddressMilkyWay";
	private static final String NBT_ADDRESS_PEGASUS = "AddressPegasus";
	private static final String NBT_ADDRESS_UNIVERSE = "AddressUniverse";

	public ItemAddressCard(NamespaceID namespaceId, int id) {
		super(namespaceId, id);
	}

	@Nonnull
	public static ItemStack createFromGate(StargateComponent gate) {
		ItemStack stack = new ItemStack(StargateItems.ADDRESS_CARD);
		updateAddresses(stack, gate);

		switch (gate.getFamily()) {
			case MilkyWay:
				StargateAddress milkyway = gate.getAddressWithFamily(StargateFamily.MilkyWay);
				if (milkyway != null) {
					stack.setCustomName(I18n.getInstance().translateKeyAndFormat(stack.getItemKey() + ".milkyway.name", milkyway.getBlockX(), milkyway.getBlockZ()));
				} else {
					stack.setCustomName("???");
				}
				break;
			case Pegasus:
				StargateAddress pegasus = gate.getAddressWithFamily(StargateFamily.Pegasus);
				if (pegasus != null) {
					stack.setCustomName(I18n.getInstance().translateKeyAndFormat(stack.getItemKey() + ".pegasus.name", pegasus.getBlockX(), pegasus.getBlockZ()));
				} else {
					stack.setCustomName("???");
				}
				break;
			case Universe:
				StargateAddress universe = gate.getAddressWithFamily(StargateFamily.Universe);
				if (universe != null) {
					stack.setCustomName(I18n.getInstance().translateKeyAndFormat(stack.getItemKey() + ".universe.name", universe.getBlockX(), universe.getBlockZ()));
				} else {
					stack.setCustomName("???");
				}
				break;
		}

		return stack;
	}

	private static void updateAddresses(ItemStack stack, StargateComponent gate) {
		StargateAddress milkyway = gate.getAddressWithFamily(StargateFamily.MilkyWay);
		StargateAddress pegasus = gate.getAddressWithFamily(StargateFamily.Pegasus);
		StargateAddress universe = gate.getAddressWithFamily(StargateFamily.Universe);

		if (milkyway != null) {
			int[] milkywayAddress = milkyway.encodeAddress();

			CompoundTag milkywayTag = new CompoundTag();

			milkywayTag.putInt("Address0", milkywayAddress[0]);
			milkywayTag.putInt("Address1", milkywayAddress[1]);
			milkywayTag.putInt("Address2", milkywayAddress[2]);
			milkywayTag.putInt("Address3", milkywayAddress[3]);
			milkywayTag.putInt("Address4", milkywayAddress[4]);
			milkywayTag.putInt("Address5", milkywayAddress[5]);
			milkywayTag.putInt("Address6", milkywayAddress[6]);
			milkywayTag.putInt("Address7", milkywayAddress[7]);

			stack.getData().putCompound(NBT_ADDRESS_MILKYWAY, milkywayTag);
		}

		if (pegasus != null) {
			int[] pegasusAddress = pegasus.encodeAddress();

			CompoundTag pegasusTag = new CompoundTag();

			pegasusTag.putInt("Address0", pegasusAddress[0]);
			pegasusTag.putInt("Address1", pegasusAddress[1]);
			pegasusTag.putInt("Address2", pegasusAddress[2]);
			pegasusTag.putInt("Address3", pegasusAddress[3]);
			pegasusTag.putInt("Address4", pegasusAddress[4]);
			pegasusTag.putInt("Address5", pegasusAddress[5]);
			pegasusTag.putInt("Address6", pegasusAddress[6]);
			pegasusTag.putInt("Address7", pegasusAddress[7]);

			stack.getData().putCompound(NBT_ADDRESS_PEGASUS, pegasusTag);
		}

		if (universe != null) {
			int[] universeAddress = universe.encodeAddress();

			CompoundTag universeTag = new CompoundTag();

			universeTag.putInt("Address0", universeAddress[0]);
			universeTag.putInt("Address1", universeAddress[1]);
			universeTag.putInt("Address2", universeAddress[2]);
			universeTag.putInt("Address3", universeAddress[3]);
			universeTag.putInt("Address4", universeAddress[4]);
			universeTag.putInt("Address5", universeAddress[5]);
			universeTag.putInt("Address6", universeAddress[6]);
			universeTag.putInt("Address7", universeAddress[7]);

			stack.getData().putCompound(NBT_ADDRESS_UNIVERSE, universeTag);
		}
	}

	public static int getSymbol(@Nonnull ItemStack stack, StargateFamily family, int symbolIndex) {
		CompoundTag nbt = stack.getData();
		String nbtKey;

		switch (family) {
			case MilkyWay:
				nbtKey = NBT_ADDRESS_MILKYWAY;
				break;
			case Pegasus:
				nbtKey = NBT_ADDRESS_PEGASUS;
				break;
			case Universe:
				nbtKey = NBT_ADDRESS_UNIVERSE;
				break;
			default:
				return 0;
		}

		if (!nbt.containsKey(nbtKey)) {
			return 0;
		}

		CompoundTag addressTag = nbt.getCompound(nbtKey);

		if (symbolIndex < 0 || symbolIndex > 7) {
			return 0;
		}

		return addressTag.getInteger("Address" + symbolIndex);
	}

	public static boolean hasAddress(@Nonnull ItemStack stack, StargateFamily family) {
		CompoundTag nbt = stack.getData();
		String nbtKey;

		switch (family) {
			case MilkyWay:
				nbtKey = NBT_ADDRESS_MILKYWAY;
				break;
			case Pegasus:
				nbtKey = NBT_ADDRESS_PEGASUS;
				break;
			case Universe:
				nbtKey = NBT_ADDRESS_UNIVERSE;
				break;
			default:
				return false;
		}

		return nbt.containsKey(nbtKey);
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
		if (EnvironmentHelper.isClientWorld()) {
			return false;
		}

		if (!player.isSneaking()) {
			return false;
		}

		if (player.getGamemode() != Gamemode.creative) {
			return false;
		}

		TileEntity tileEntity = world.getTileEntity(blockX, blockY, blockZ);

		if (!(tileEntity instanceof TileEntityDHD)) {
			return false;
		}

		TileEntityDHD dhd = ((TileEntityDHD) tileEntity);

		TileEntityStargate tile = dhd.findLinkedGate();

		if (tile == null) {
			return false;
		}

		StargateComponent gate = tile.getStargateComponent();

		if (gate == null) {
			return false;
		}

		gate.clearAddress();

		switch (gate.getFamily()) {
			case MilkyWay:
				for (int i = 0; i < 9; i++) {
					dhd.encode(getSymbol(itemstack, StargateFamily.MilkyWay, i));
				}
				break;
			case Pegasus:
				for (int i = 0; i < 9; i++) {
					dhd.encode(getSymbol(itemstack, StargateFamily.Pegasus, i));
				}
				break;
			case Universe:
				for (int i = 0; i < 9; i++) {
					dhd.encode(getSymbol(itemstack, StargateFamily.Universe, i));
				}
				break;
		}

		dhd.dial();

		return true;
	}
}
