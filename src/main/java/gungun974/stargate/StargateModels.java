package gungun974.stargate;

import gungun974.stargate.gate.renders.*;
import gungun974.stargate.gate.tiles.TileEntityStargateMilkyWay;
import gungun974.stargate.gate.tiles.TileEntityStargatePegasus;
import gungun974.stargate.gate.tiles.TileEntityStargateUniverse;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

public class StargateModels implements ModelEntrypoint {

	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		ModelHelper.setBlockModel(StargateBlocks.STARGATE_CORE_MILKYWAY, () -> new BlockModelStargateCore<>(StargateBlocks.STARGATE_CORE_MILKYWAY)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
			.setTex(0, "minecraft:block/noteblock", Side.NORTH)
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_RING_MILKYWAY, () -> new BlockModelStargateRing<>(StargateBlocks.STARGATE_RING_MILKYWAY)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_CORE_PEGASUS, () -> new BlockModelStargateCore<>(StargateBlocks.STARGATE_CORE_PEGASUS)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
			.setTex(0, "minecraft:block/noteblock", Side.NORTH)
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_RING_PEGASUS, () -> new BlockModelStargateRing<>(StargateBlocks.STARGATE_RING_PEGASUS)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_CORE_UNIVERSE, () -> new BlockModelStargateCore<>(StargateBlocks.STARGATE_CORE_UNIVERSE)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
			.setTex(0, "minecraft:block/noteblock", Side.NORTH)
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_RING_UNIVERSE, () -> new BlockModelStargateRing<>(StargateBlocks.STARGATE_RING_UNIVERSE)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
		);

		StargateMod.LOGGER.info("Block Models initialized.");
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {

	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		ModelHelper.setTileEntityModel(TileEntityStargateMilkyWay.class, TileEntityRenderStargateMilkyWay::new);
		ModelHelper.setTileEntityModel(TileEntityStargatePegasus.class, TileEntityRenderStargatePegasus::new);
		ModelHelper.setTileEntityModel(TileEntityStargateUniverse.class, TileEntityRenderStargateUniverse::new);

		StargateMod.LOGGER.info("Tile Entity Models initialized.");
	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {
	}
}

