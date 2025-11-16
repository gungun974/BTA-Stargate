package gungun974.stargate;

import gungun974.stargate.gate.blocks.BlockLogicStargateCore;
import gungun974.stargate.gate.blocks.BlockLogicStargateRing;
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
	public static Block<BlockLogicStargateCore> STARGATE_CORE_MILKYWAY;
	public static Block<BlockLogicStargateRing> STARGATE_RING_MILKYWAY;

	public static Block<BlockLogicStargateCore> STARGATE_CORE_PEGASUS;
	public static Block<BlockLogicStargateRing> STARGATE_RING_PEGASUS;

	public static Block<BlockLogicStargateCore> STARGATE_CORE_UNIVERSE;
	public static Block<BlockLogicStargateRing> STARGATE_RING_UNIVERSE;

	private static int currentGeneratedId;

	private static int generateNexId() {
		return currentGeneratedId++;
	}

	public static void RegisterBlocks() {
		currentGeneratedId = StargateMod.startBlockID;

		EntityHelper.createTileEntity(TileEntityStargateMilkyWay.class, NamespaceID.getPermanent(MOD_ID, "stargate_milkyway"));
		EntityHelper.createTileEntity(TileEntityStargatePegasus.class, NamespaceID.getPermanent(MOD_ID, "stargate_pegasus"));
		EntityHelper.createTileEntity(TileEntityStargateUniverse.class, NamespaceID.getPermanent(MOD_ID, "stargate_universe"));

		BlockBuilder stargateCoreBuilder = new BlockBuilder(MOD_ID)
			.setHardness(2.5f)
			.setResistance(5.0f)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE)
			.setBlockSound(BlockSounds.WOOD);

		BlockBuilder stargateRingBuilder = new BlockBuilder(MOD_ID)
			.setHardness(2.5f)
			.setResistance(5.0f)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE)
			.setBlockSound(BlockSounds.WOOD);

		STARGATE_CORE_MILKYWAY = stargateCoreBuilder
			.setTileEntity(TileEntityStargateMilkyWay::new)
			.build("stargate_core_milkyway", generateNexId(), b -> new BlockLogicStargateCore(b, Material.wood));
		STARGATE_RING_MILKYWAY = stargateRingBuilder
			.build("stargate_ring_milkyway", generateNexId(), b -> new BlockLogicStargateRing(b, Material.wood));

		STARGATE_CORE_PEGASUS = stargateCoreBuilder
			.setTileEntity(TileEntityStargatePegasus::new)
			.build("stargate_core_pegasus", generateNexId(), b -> new BlockLogicStargateCore(b, Material.wood));
		STARGATE_RING_PEGASUS = stargateRingBuilder
			.build("stargate_ring_pegasus", generateNexId(), b -> new BlockLogicStargateRing(b, Material.wood));

		STARGATE_CORE_UNIVERSE = stargateCoreBuilder
			.setTileEntity(TileEntityStargateUniverse::new)
			.build("stargate_core_universe", generateNexId(), b -> new BlockLogicStargateCore(b, Material.wood));
		STARGATE_RING_UNIVERSE = stargateRingBuilder
			.build("stargate_ring_universe", generateNexId(), b -> new BlockLogicStargateRing(b, Material.wood));
	}

}
