package gungun974.stargate.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import gungun974.stargate.StargateItems;
import gungun974.stargate.dhd.cc.DHDPeripheral;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import gungun974.stargate.gate.cc.AddressCardMedia;
import gungun974.stargate.gate.cc.StargatePeripheral;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.entity.TileEntity;

public class CCPlugin {
	public static void register() {
		ComputerCraftAPI.registerPeripheralProvider((world, pos, side) -> {
			TileEntity tile = world.getTileEntity(pos.x, pos.y, pos.z);
			if (tile instanceof TileEntityStargate) {
				return new StargatePeripheral((TileEntityStargate) tile);
			}
			if (tile instanceof TileEntityDHD) {
				return new DHDPeripheral((TileEntityDHD) tile);
			}
			return null;
		});

		ComputerCraftAPI.registerMediaProvider((stack) -> {
			if (stack.itemID == StargateItems.ADDRESS_CARD.id) {
				return new AddressCardMedia();
			}
			return null;
		});
	}
}
