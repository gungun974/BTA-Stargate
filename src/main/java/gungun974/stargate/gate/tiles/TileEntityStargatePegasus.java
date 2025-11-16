package gungun974.stargate.gate.tiles;

import gungun974.stargate.StargateBlocks;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.blocks.BlockLogicStargateRing;
import net.minecraft.core.block.Block;

public class TileEntityStargatePegasus extends TileEntityStargateCore {
	@Override
	public StargateFamily getFamily() {
		return StargateFamily.Pegasus;
	}

	@Override
	public Block<BlockLogicStargateRing> getRingBlock() {
		return StargateBlocks.STARGATE_RING_PEGASUS;
	}
}
