package gungun974.stargate;

import gungun974.stargate.core.DangerousItemFood;
import gungun974.stargate.gate.items.ItemAddressCard;
import gungun974.stargate.gate.items.ItemStargateWand;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryCategory;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

import static gungun974.stargate.StargateMod.MOD_ID;

public class StargateItems {
	public static ItemStargateWand STARGATE_WAND;
	public static ItemAddressCard ADDRESS_CARD;

	public static Item NAQUADAH;
	public static Item NAQUADAH_DUST;
	public static Item NAQUADAH_CRUDE_ALLOY;
	public static Item NAQUADAH_ALLOY;

	public static Item CONTROL_CRYSTAL;

	private static int currentGeneratedId;

	private static int generateNexId() {
		return currentGeneratedId++;
	}

	public static void RegisterItems() {
		currentGeneratedId = StargateMod.startItemID;

		STARGATE_WAND = new ItemBuilder(MOD_ID)
			.setKey("item.stargate_wand")
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build((new ItemStargateWand(new NamespaceID(MOD_ID, "stargate_wand"), "stargate.stargate_wand", generateNexId())));

		ADDRESS_CARD = new ItemBuilder(MOD_ID)
			.setKey("item.address_card")
			.setTags(ItemTags.NOT_IN_CREATIVE_MENU)
			.build((new ItemAddressCard(new NamespaceID(MOD_ID, "address_card"), "stargate.address_card", generateNexId())));

		NAQUADAH = new ItemBuilder(MOD_ID)
			.setKey("item.naquadah")
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build((new Item(new NamespaceID(MOD_ID, "naquadah"), "stargate.naquadah", generateNexId())));
		NAQUADAH_DUST = new ItemBuilder(MOD_ID)
			.setKey("item.naquadah_dust")
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build((new DangerousItemFood("item.naquadah_dust", MOD_ID + ":naquadah_dust", generateNexId(), -16, 16, false, 64)));
		NAQUADAH_CRUDE_ALLOY = new ItemBuilder(MOD_ID)
			.setKey("item.naquadah_crude_alloy")
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build((new Item(new NamespaceID(MOD_ID, "naquadah_crude_alloy"), "stargate.naquadah_crude_alloy", generateNexId())));
		NAQUADAH_ALLOY = new ItemBuilder(MOD_ID)
			.setKey("item.naquadah_alloy")
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build((new Item(new NamespaceID(MOD_ID, "naquadah_alloy"), "stargate.naquadah_alloy", generateNexId())));

		CONTROL_CRYSTAL = new ItemBuilder(MOD_ID)
			.setKey("item.control_crystal")
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build((new Item(new NamespaceID(MOD_ID, "control_crystal"), "stargate.control_crystal", generateNexId())));
	}

}
