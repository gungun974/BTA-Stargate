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
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

public class StargateModels implements ModelEntrypoint {

	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		ModelHelper.setBlockModel(StargateBlocks.STARGATE_MILKYWAY, () -> new BlockModelStargate<>(StargateBlocks.STARGATE_MILKYWAY, "minecraft:block/chest/planks/top"));

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_BUILD_PART_MILKYWAY, () -> new BlockModelStargateBuildPart<>(StargateBlocks.STARGATE_BUILD_PART_MILKYWAY)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_PEGASUS, () -> new BlockModelStargate<>(StargateBlocks.STARGATE_PEGASUS, "minecraft:block/chest/planks/top"));

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_BUILD_PART_PEGASUS, () -> new BlockModelStargateBuildPart<>(StargateBlocks.STARGATE_BUILD_PART_PEGASUS)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_UNIVERSE, () -> new BlockModelStargate<>(StargateBlocks.STARGATE_UNIVERSE, "minecraft:block/chest/planks/top"));

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_BUILD_PART_UNIVERSE, () -> new BlockModelStargateBuildPart<>(StargateBlocks.STARGATE_BUILD_PART_UNIVERSE)
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

