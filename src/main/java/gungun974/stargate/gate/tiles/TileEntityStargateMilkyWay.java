package gungun974.stargate.gate.tiles;

import gungun974.stargate.StargateBlocks;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.blocks.BlockLogicStargateRing;
import net.minecraft.core.block.Block;

public class TileEntityStargateMilkyWay extends TileEntityStargateCore {
	@Override
	public StargateFamily getFamily() {
		return StargateFamily.MilkyWay;
	}

	@Override
	public Block<BlockLogicStargateRing> getRingBlock() {
		return StargateBlocks.STARGATE_RING_MILKYWAY;
	}
}
