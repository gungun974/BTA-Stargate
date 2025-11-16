package gungun974.stargate;

import gungun974.stargate.gate.blocks.core.BlockLogicStargateCore;
import gungun974.stargate.gate.blocks.core.TileEntityStargateMilkyWay;
import gungun974.stargate.gate.blocks.ring.BlockLogicStargateRing;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;

import static gungun974.stargate.StargateMod.MOD_ID;

public class StargateBlocks {
	public static Block<BlockLogicStargateCore> STARGATE_CORE;
	public static Block<BlockLogicStargateRing> STARGATE_RING;

	private static int currentGeneratedId;

	private static int generateNexId() {
		return currentGeneratedId++;
	}

	public static void RegisterBlocks() {
		currentGeneratedId = StargateMod.startBlockID;

		EntityHelper.createTileEntity(TileEntityStargateMilkyWay.class, NamespaceID.getPermanent(MOD_ID, "stargate_milkyway"));

		STARGATE_CORE = new BlockBuilder(MOD_ID)
			.setHardness(2.5f)
			.setResistance(5.0f)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE)
			.setTileEntity(TileEntityStargateMilkyWay::new)
			.setBlockSound(BlockSounds.WOOD)
			.build("stargate_core_milkyway", generateNexId(), b -> new BlockLogicStargateCore(b, Material.wood));

		STARGATE_RING = new BlockBuilder(MOD_ID)
			.setHardness(2.5f)
			.setResistance(5.0f)
			.setTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE)
			.setBlockSound(BlockSounds.WOOD)
			.build("stargate_ring", generateNexId(), b -> new BlockLogicStargateRing(b, Material.wood));
	}

}
