package gungun974.stargate;

import gungun974.stargate.gate.blocks.core.BlockModelStargateCore;
import gungun974.stargate.gate.blocks.core.TileEntityRenderStargateMilkyWay;
import gungun974.stargate.gate.blocks.core.TileEntityStargateMilkyWay;
import gungun974.stargate.gate.blocks.ring.BlockModelStargateRing;
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
		ModelHelper.setBlockModel(StargateBlocks.STARGATE_CORE, () -> new BlockModelStargateCore<>(StargateBlocks.STARGATE_CORE)
			.setAllTextures(0, "minecraft:block/chest/planks/top")
			.setTex(0, "minecraft:block/noteblock", Side.NORTH)
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_RING, () -> new BlockModelStargateRing<>(StargateBlocks.STARGATE_RING)
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

		StargateMod.LOGGER.info("Tile Entity Models initialized.");
	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {
	}
}

