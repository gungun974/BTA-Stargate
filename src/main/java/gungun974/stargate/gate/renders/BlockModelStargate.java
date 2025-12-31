package gungun974.stargate.gate.renders;

import gungun974.stargate.core.ProxyWorld;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public class BlockModelStargate<T extends BlockLogic> extends BlockModelStandard<T> {
	protected final IconCoordinate particleTexture;

	public BlockModelStargate(Block<T> block, String key) {
		super(block);
		this.particleTexture = TextureRegistry.getTexture(key);
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		TileEntity tileEntity = renderBlocks.blockAccess.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) tileEntity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				BlockModel<?> camoufledBlockModel = BlockModelDispatcher.getInstance().getDispatch(camoufledBlock);

				WorldSource orginalWorldSource = BlockModel.renderBlocks.blockAccess;

				try {

					ProxyWorld proxyWorld = new ProxyWorld(orginalWorldSource);

					proxyWorld.setBlock(x, y, z, camouflageComponent.getBlockId(), camouflageComponent.getBlockMeta(), null);

					BlockModel.renderBlocks.blockAccess = proxyWorld;


					boolean render = camoufledBlockModel.render(tessellator, x, y, z);

					BlockModel.renderBlocks.blockAccess = orginalWorldSource;

					return render;

				} finally {
					BlockModel.renderBlocks.blockAccess = orginalWorldSource;
				}
			}
		}

		return false;
	}

	@Override
	public IconCoordinate getParticleTexture(Side side, int meta) {
		return particleTexture;
	}
}
