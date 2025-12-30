package gungun974.stargate.gate.renders;

import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;

public class BlockModelStargate<T extends BlockLogic> extends BlockModelStandard<T> {
	protected final IconCoordinate particleTexture;

	public BlockModelStargate(Block<T> block, String key) {
		super(block);
		this.particleTexture = TextureRegistry.getTexture(key);
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		return false;
	}

	@Override
	public IconCoordinate getParticleTexture(Side side, int meta) {
		return particleTexture;
	}
}
