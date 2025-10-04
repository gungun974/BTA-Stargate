package gungun974.stargate.gate.blocks.core;

import net.minecraft.client.render.block.model.BlockModelHorizontalRotation;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;

public class BlockModelStargateCore<T extends BlockLogic> extends BlockModelHorizontalRotation<T> {
	public BlockModelStargateCore(Block<T> block) {
		super(block);
	}
}
