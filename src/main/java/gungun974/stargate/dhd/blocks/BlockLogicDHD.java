package gungun974.stargate.dhd.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.material.Material;

public class BlockLogicDHD extends BlockLogicRotatable {
	public BlockLogicDHD(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}
}
