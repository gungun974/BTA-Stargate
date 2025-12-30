package gungun974.stargate.gate.renders;

import gungun974.stargate.gate.blocks.BlockLogicStargateBuildPart;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.Sides;
import net.minecraft.core.world.WorldSource;

public class BlockModelStargateBuildPart<T extends BlockLogic> extends BlockModelStandard<T> {
	public BlockModelStargateBuildPart(Block<T> block) {
		super(block);
	}

	public IconCoordinate getBlockTexture(WorldSource blockAccess, int x, int y, int z, Side side) {
		return getBlockTextureFromSideAndMetadata(side.getOpposite(), blockAccess.getBlockMetadata(x, y, z));
	}

	public IconCoordinate getBlockTextureFromSideAndMetadata(Side rawSide, int rawMetadata) {
		Side side = rawSide;

		int currentMetadata = BlockLogicStargateBuildPart.getRawMeta(rawMetadata);

		if (currentMetadata == BlockLogicStargateBuildPart.CORE_META) {
			int index = Sides.orientationLookUpHorizontal[6 * Math.min(BlockLogicStargateBuildPart.getDirectionFromMeta(rawMetadata).getId(), 5) + rawSide.getId()];

			if (index >= Sides.orientationLookUpHorizontal.length) return this.blockTextures.get(Side.BOTTOM);

			side = Side.getSideById(index);
		}

		int index = Sides.orientationLookUpHorizontal[side.getId()];

		if (currentMetadata == BlockLogicStargateBuildPart.CHEVRON_META && (side != Side.TOP && side != Side.BOTTOM)) {
			IconCoordinate original = this.blockTextures.get(side);

			assert original != null;

			return TextureRegistry.getTexture("minecraft:block/block_redstone");
		} else if (currentMetadata == BlockLogicStargateBuildPart.CORE_META && (side == Side.SOUTH)) {
			IconCoordinate original = this.blockTextures.get(side);

			assert original != null;

			return TextureRegistry.getTexture("minecraft:block/block_lapis");
		} else {
			return this.blockTextures.get(Side.getSideById(index));
		}
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		return super.render(tessellator, x, y, z);
	}
}
