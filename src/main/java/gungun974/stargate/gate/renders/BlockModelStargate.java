package gungun974.stargate.gate.renders;

import net.minecraft.client.render.block.model.BlockModelHorizontalRotation;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;

public class BlockModelStargate<T extends BlockLogic> extends BlockModelHorizontalRotation<T> {
	public BlockModelStargate(Block<T> block) {
		super(block);
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		return false;
	}
}
