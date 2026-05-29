package gungun974.stargate;

import gungun974.stargate.dhd.blocks.BlockLogicDHD;
import gungun974.stargate.dhd.blocks.BlockLogicDHDMilkyWay;
import gungun974.stargate.dhd.blocks.BlockLogicDHDPegasus;
import gungun974.stargate.dhd.tiles.TileEntityDHDMilkyWay;
import gungun974.stargate.dhd.tiles.TileEntityDHDPegasus;
import gungun974.stargate.gate.blocks.BlockLogicStargate;
import gungun974.stargate.gate.blocks.BlockLogicStargateBuildPart;
import gungun974.stargate.gate.tiles.TileEntityStargateMilkyWay;
import gungun974.stargate.gate.tiles.TileEntityStargatePegasus;
import gungun974.stargate.gate.tiles.TileEntityStargateUniverse;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryCategory;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

import java.util.LinkedList;
import java.util.List;

import static gungun974.stargate.StargateMod.MOD_ID;

public class StargateBlocks {
	public static Block<BlockLogicStargate> STARGATE_MILKYWAY;
	public static Block<BlockLogicStargateBuildPart> STARGATE_BUILD_PART_MILKYWAY;

	public static Block<BlockLogicStargate> STARGATE_PEGASUS;
	public static Block<BlockLogicStargateBuildPart> STARGATE_BUILD_PART_PEGASUS;

	public static Block<BlockLogicStargate> STARGATE_UNIVERSE;
	public static Block<BlockLogicStargateBuildPart> STARGATE_BUILD_PART_UNIVERSE;

	public static Block<BlockLogicDHD> DHD_MILKYWAY;
	public static Block<BlockLogicDHD> DHD_PEGASUS;

	private static int currentGeneratedId;

	private static int generateNexId() {
		return currentGeneratedId++;
	}

	public static void RegisterBlocks() {
		currentGeneratedId = StargateMod.startBlockID;

		TileEntityDispatcher.addMapping(TileEntityStargateMilkyWay.class, new NamespaceID(MOD_ID, "stargate_milkyway"));
		TileEntityDispatcher.addMapping(TileEntityStargatePegasus.class, new NamespaceID(MOD_ID, "stargate_pegasus"));
		TileEntityDispatcher.addMapping(TileEntityStargateUniverse.class, new NamespaceID(MOD_ID, "stargate_universe"));

		BlockBuilder stargateBuilder = new BlockBuilder(MOD_ID)
			.setHardness(20f)
			.setResistance(8000.0f)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.setBlockSound(BlockSounds.METAL)
			.setLuminance(15);

		BlockBuilder stargateBuildPartBuilder = new BlockBuilder(MOD_ID)
			.setHardness(5f)
			.setResistance(80f)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_PICKAXE)
			.setBlockSound(BlockSounds.METAL);

		STARGATE_MILKYWAY = stargateBuilder
			.setTileEntity(TileEntityStargateMilkyWay::new)
			.build("stargate_milkyway", generateNexId(), b -> new BlockLogicStargate(b, Materials.WOOD));
		STARGATE_BUILD_PART_MILKYWAY = stargateBuildPartBuilder
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS).setCustomSupplier(() -> {
				List<ItemStack> creativeItems = new LinkedList<>();

				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_MILKYWAY, 1, 0));
				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_MILKYWAY, 1, 1));
				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_MILKYWAY, 1, 2));

				return creativeItems;
			}))
			.build("stargate_build_part_milkyway", generateNexId(), b -> new BlockLogicStargateBuildPart(b, Materials.WOOD));

		STARGATE_PEGASUS = stargateBuilder
			.setTileEntity(TileEntityStargatePegasus::new)
			.build("stargate_pegasus", generateNexId(), b -> new BlockLogicStargate(b, Materials.WOOD));
		STARGATE_BUILD_PART_PEGASUS = stargateBuildPartBuilder
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS).setCustomSupplier(() -> {
				List<ItemStack> creativeItems = new LinkedList<>();

				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_PEGASUS, 1, 0));
				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_PEGASUS, 1, 1));
				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_PEGASUS, 1, 2));

				return creativeItems;
			}))
			.build("stargate_build_part_pegasus", generateNexId(), b -> new BlockLogicStargateBuildPart(b, Materials.WOOD));

		STARGATE_UNIVERSE = stargateBuilder
			.setTileEntity(TileEntityStargateUniverse::new)
			.build("stargate_universe", generateNexId(), b -> new BlockLogicStargate(b, Materials.WOOD));
		STARGATE_BUILD_PART_UNIVERSE = stargateBuildPartBuilder
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS).setCustomSupplier(() -> {
				List<ItemStack> creativeItems = new LinkedList<>();

				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_UNIVERSE, 1, 0));
				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_UNIVERSE, 1, 1));
				creativeItems.add(new ItemStack(StargateBlocks.STARGATE_BUILD_PART_UNIVERSE, 1, 2));

				return creativeItems;
			}))
			.build("stargate_build_part_universe", generateNexId(), b -> new BlockLogicStargateBuildPart(b, Materials.WOOD));

		TileEntityDispatcher.addMapping(TileEntityDHDMilkyWay.class, new NamespaceID(MOD_ID, "dhd_milkyway"));
		TileEntityDispatcher.addMapping(TileEntityDHDPegasus.class, new NamespaceID(MOD_ID, "dhd_pegasus"));

		BlockBuilder dhdBuilder = new BlockBuilder(MOD_ID)
			.setHardness(5f)
			.setResistance(10f)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.setBlockSound(BlockSounds.METAL);

		DHD_MILKYWAY = dhdBuilder
			.setTileEntity(TileEntityDHDMilkyWay::new)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build("dhd_milkyway", generateNexId(), b -> new BlockLogicDHDMilkyWay(b, Materials.WOOD));

		DHD_PEGASUS = dhdBuilder
			.setTileEntity(TileEntityDHDPegasus::new)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build("dhd_pegasus", generateNexId(), b -> new BlockLogicDHDPegasus(b, Materials.WOOD));
	}

}
