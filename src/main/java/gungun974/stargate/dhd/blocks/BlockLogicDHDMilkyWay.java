package gungun974.stargate.dhd.blocks;

import gungun974.stargate.core.RaycastHelper;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;

public class BlockLogicDHDMilkyWay extends BlockLogicDHD {
	public static final int[] KEY_IDS = {23, 5, 28, 37, 33, 11, 36, 10, 20, 2, 3, 19, 8, 4, 31, 0, 18, 21, 6, 27, 9, 32, 38, 25, 22, 17, 13, 16, 1, 24, 35, 7, 26, 30, 14, 34, 29, 15};

	private static final RaycastHelper.KeyTriangles[] KEY_TRIANGLES = BlockLogicDHD.generateKeyTriangles(KEY_IDS);

	public BlockLogicDHDMilkyWay(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	protected RaycastHelper.KeyTriangles[] getKeyTriangles() {
		return KEY_TRIANGLES;
	}
}
