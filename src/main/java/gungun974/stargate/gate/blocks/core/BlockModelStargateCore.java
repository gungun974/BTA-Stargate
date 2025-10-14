package gungun974.stargate.gate.blocks.core;

import net.minecraft.client.render.block.model.BlockModelHorizontalRotation;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;

public class BlockModelStargateCore<T extends BlockLogic> extends BlockModelHorizontalRotation<T> {
	public BlockModelStargateCore(Block<T> block) {
		super(block);
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
