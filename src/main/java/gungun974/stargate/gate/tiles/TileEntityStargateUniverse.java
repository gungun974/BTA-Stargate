package gungun974.stargate.gate.tiles;

import gungun974.stargate.StargateBlocks;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.blocks.BlockLogicStargateRing;
import net.minecraft.core.block.Block;

public class TileEntityStargateUniverse extends TileEntityStargateCore {
	@Override
	public StargateFamily getFamily() {
		return StargateFamily.Universe;
	}

	@Override
	public Block<BlockLogicStargateRing> getRingBlock() {
		return StargateBlocks.STARGATE_RING_UNIVERSE;
	}
}
