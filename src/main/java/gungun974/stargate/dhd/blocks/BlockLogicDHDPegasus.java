package gungun974.stargate.dhd.blocks;

import gungun974.stargate.core.RaycastHelper;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;

public class BlockLogicDHDPegasus extends BlockLogicDHD {
	public static final int[] KEY_IDS = {22, 5, 27, 32, 11, 35, 10, 19, 2, 3, 18, 8, 4, 30, 0, 17, 20, 6, 26, 9, 31, 24, 21, 16, 12, 15, 1, 23, 34, 7, 25, 29, 13, 33, 28, 14};

	private static final RaycastHelper.KeyTriangles[] KEY_TRIANGLES = BlockLogicDHD.generateKeyTriangles(KEY_IDS);

	public BlockLogicDHDPegasus(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	protected RaycastHelper.KeyTriangles[] getKeyTriangles() {
		return KEY_TRIANGLES;
	}
}
