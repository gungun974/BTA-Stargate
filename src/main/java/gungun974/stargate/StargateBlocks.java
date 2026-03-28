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
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;

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

		EntityHelper.createTileEntity(TileEntityStargateMilkyWay.class, NamespaceID.getPermanent(MOD_ID, "stargate_milkyway"));
		EntityHelper.createTileEntity(TileEntityStargatePegasus.class, NamespaceID.getPermanent(MOD_ID, "stargate_pegasus"));
		EntityHelper.createTileEntity(TileEntityStargateUniverse.class, NamespaceID.getPermanent(MOD_ID, "stargate_universe"));

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
			.build("stargate_milkyway", generateNexId(), b -> new BlockLogicStargate(b, Material.wood));
		STARGATE_BUILD_PART_MILKYWAY = stargateBuildPartBuilder
			.build("stargate_build_part_milkyway", generateNexId(), b -> new BlockLogicStargateBuildPart(b, Material.wood));

		STARGATE_PEGASUS = stargateBuilder
			.setTileEntity(TileEntityStargatePegasus::new)
			.build("stargate_pegasus", generateNexId(), b -> new BlockLogicStargate(b, Material.wood));
		STARGATE_BUILD_PART_PEGASUS = stargateBuildPartBuilder
			.build("stargate_build_part_pegasus", generateNexId(), b -> new BlockLogicStargateBuildPart(b, Material.wood));

		STARGATE_UNIVERSE = stargateBuilder
			.setTileEntity(TileEntityStargateUniverse::new)
			.build("stargate_universe", generateNexId(), b -> new BlockLogicStargate(b, Material.wood));
		STARGATE_BUILD_PART_UNIVERSE = stargateBuildPartBuilder
			.build("stargate_build_part_universe", generateNexId(), b -> new BlockLogicStargateBuildPart(b, Material.wood));

		EntityHelper.createTileEntity(TileEntityDHDMilkyWay.class, NamespaceID.getPermanent(MOD_ID, "dhd_milkyway"));
		EntityHelper.createTileEntity(TileEntityDHDPegasus.class, NamespaceID.getPermanent(MOD_ID, "dhd_pegasus"));

		BlockBuilder dhdBuilder = new BlockBuilder(MOD_ID)
			.setHardness(5f)
			.setResistance(10f)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.setBlockSound(BlockSounds.METAL);

		DHD_MILKYWAY = dhdBuilder
			.setTileEntity(TileEntityDHDMilkyWay::new)
			.build("dhd_milkyway", generateNexId(), b -> new BlockLogicDHDMilkyWay(b, Material.wood));

		DHD_PEGASUS = dhdBuilder
			.setTileEntity(TileEntityDHDPegasus::new)
			.build("dhd_pegasus", generateNexId(), b -> new BlockLogicDHDPegasus(b, Material.wood));
	}

}
