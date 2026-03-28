package gungun974.stargate;

import gungun974.stargate.dhd.renders.ItemModelDHD;
import gungun974.stargate.dhd.renders.TileEntityRendererDHDMilkyWay;
import gungun974.stargate.dhd.renders.TileEntityRendererDHDPegasus;
import gungun974.stargate.dhd.tiles.TileEntityDHDMilkyWay;
import gungun974.stargate.dhd.tiles.TileEntityDHDPegasus;
import gungun974.stargate.gate.renders.*;
import gungun974.stargate.gate.tiles.TileEntityStargateMilkyWay;
import gungun974.stargate.gate.tiles.TileEntityStargatePegasus;
import gungun974.stargate.gate.tiles.TileEntityStargateUniverse;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelEmpty;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import static gungun974.stargate.StargateMod.MOD_ID;

public class StargateModels implements ModelEntrypoint {

	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		final IconCoordinate a = TextureRegistry.getTexture("stargate:block/gate/milkyway_ring");

		try {
			TextureRegistry.initializeAllFiles("stargate", a.parentAtlas, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_MILKYWAY, () -> new BlockModelStargate<>(StargateBlocks.STARGATE_MILKYWAY, "stargate:block/gate/milkyway_ring"));

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_BUILD_PART_MILKYWAY, () -> new BlockModelStargateBuildPart<>(StargateBlocks.STARGATE_BUILD_PART_MILKYWAY)
			.setAllTextures(0, "stargate:block/gate/milkyway_ring")
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_PEGASUS, () -> new BlockModelStargate<>(StargateBlocks.STARGATE_PEGASUS, "stargate:block/gate/pegasus_ring"));

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_BUILD_PART_PEGASUS, () -> new BlockModelStargateBuildPart<>(StargateBlocks.STARGATE_BUILD_PART_PEGASUS)
			.setAllTextures(0, "stargate:block/gate/pegasus_ring")
		);

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_UNIVERSE, () -> new BlockModelStargate<>(StargateBlocks.STARGATE_UNIVERSE, "stargate:block/gate/universe_ring"));

		ModelHelper.setBlockModel(StargateBlocks.STARGATE_BUILD_PART_UNIVERSE, () -> new BlockModelStargateBuildPart<>(StargateBlocks.STARGATE_BUILD_PART_UNIVERSE)
			.setAllTextures(0, "stargate:block/gate/universe_ring")
		);

		ModelHelper.setBlockModel(StargateBlocks.DHD_MILKYWAY, () -> new BlockModelEmpty<>(StargateBlocks.DHD_MILKYWAY));

		StargateMod.LOGGER.info("Block Models initialized.");
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {
		ModelHelper.setItemModel(StargateItems.STARGATE_WAND, () -> {
			ItemModelStandard itemModelStandard = new ItemModelStandard(StargateItems.STARGATE_WAND, MOD_ID);
			itemModelStandard.icon = TextureRegistry.getTexture(NamespaceID.getPermanent("minecraft", "item/stick"));
			return itemModelStandard;
		});

		ModelHelper.setItemModel(StargateItems.ADDRESS_CARD, () -> {
			ItemModelStandard itemModelStandard = new ItemModelAddressCard(StargateItems.ADDRESS_CARD, MOD_ID);
			itemModelStandard.icon = TextureRegistry.getTexture(NamespaceID.getPermanent(MOD_ID, "item/address_card"));
			return itemModelStandard;
		});

		ModelHelper.setItemModel(StargateBlocks.DHD_MILKYWAY.asItem(), () -> {
			ItemModelStandard itemModelStandard = new ItemModelDHD(StargateBlocks.DHD_MILKYWAY.asItem(), MOD_ID);
			itemModelStandard.icon = TextureRegistry.getTexture(NamespaceID.getPermanent(MOD_ID, "item/milkyway_dhd"));
			return itemModelStandard;
		});

		ModelHelper.setItemModel(StargateBlocks.DHD_PEGASUS.asItem(), () -> {
			ItemModelStandard itemModelStandard = new ItemModelDHD(StargateBlocks.DHD_PEGASUS.asItem(), MOD_ID);
			itemModelStandard.icon = TextureRegistry.getTexture(NamespaceID.getPermanent(MOD_ID, "item/pegasus_dhd"));
			return itemModelStandard;
		});
	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		ModelHelper.setTileEntityModel(TileEntityStargateMilkyWay.class, TileEntityRenderStargateMilkyWay::new);
		ModelHelper.setTileEntityModel(TileEntityStargatePegasus.class, TileEntityRenderStargatePegasus::new);
		ModelHelper.setTileEntityModel(TileEntityStargateUniverse.class, TileEntityRenderStargateUniverse::new);

		ModelHelper.setTileEntityModel(TileEntityDHDMilkyWay.class, TileEntityRendererDHDMilkyWay::new);
		ModelHelper.setTileEntityModel(TileEntityDHDPegasus.class, TileEntityRendererDHDPegasus::new);

		StargateMod.LOGGER.info("Tile Entity Models initialized.");
	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {
	}
}

