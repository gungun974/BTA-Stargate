package gungun974.stargate;

import gungun974.stargate.gate.items.ItemAddressCard;
import gungun974.stargate.gate.items.ItemStargateWand;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.ItemBuilder;

import static gungun974.stargate.StargateMod.MOD_ID;

public class StargateItems {
	public static ItemStargateWand STARGATE_WAND;
	public static ItemAddressCard ADDRESS_CARD;

	private static int currentGeneratedId;

	private static int generateNexId() {
		return currentGeneratedId++;
	}

	public static void RegisterItems() {
		currentGeneratedId = StargateMod.startItemID;

		STARGATE_WAND = new ItemBuilder(MOD_ID)
			.setKey("item.stargate_wand")
			.build((new ItemStargateWand(NamespaceID.getPermanent(MOD_ID, "stargate_wand"), generateNexId())));

		ADDRESS_CARD = new ItemBuilder(MOD_ID)
			.setKey("item.address_card")
			.build((new ItemAddressCard(NamespaceID.getPermanent(MOD_ID, "address_card"), generateNexId())));
	}

}
