package gungun974.stargate.gate.renders;

import gungun974.stargate.gate.tiles.TileEntityStargateCore;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.Sides;
import net.minecraft.core.world.WorldSource;

public class BlockModelStargateRing<T extends BlockLogic> extends BlockModelStandard<T> {
	public BlockModelStargateRing(Block<T> block) {
		super(block);
	}

	public IconCoordinate getBlockTexture(WorldSource blockAccess, int x, int y, int z, Side side) {
		return getBlockTextureFromSideAndMetadata(side, blockAccess.getBlockMetadata(x, y, z));
	}

	public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int currentMetadata) {
		int index = Sides.orientationLookUpHorizontal[side.getId()];
		if (currentMetadata == 1 && (side != Side.TOP && side != Side.BOTTOM)) {
			IconCoordinate original = this.blockTextures.get(side);

			assert original != null;

			return TextureRegistry.getTexture("minecraft:block/block_redstone");
		} else {
			return this.blockTextures.get(Side.getSideById(index));
		}
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		boolean assembled = TileEntityStargateCore.isPartOfAssembled(renderBlocks.blockAccess, x, y, z);
		if (assembled) {
			return false;
		}
		return super.render(tessellator, x, y, z);
	}
}
