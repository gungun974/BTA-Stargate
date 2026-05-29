package gungun974.stargate.gate.renders;

import gungun974.stargate.core.ProxyWorld;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockModelStargate<T extends BlockLogic> extends BlockModelStandard<T> {
	protected final IconCoordinate particleTexture;

	public BlockModelStargate(Block<T> block, String key) {
		super(block);
		this.particleTexture = TextureRegistry.getTexture(key);
	}

	@Override
	public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
		TileEntity tileEntity = worldSource.getTileEntity(tilePos);
		if (tileEntity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) tileEntity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.getBlock(camouflageComponent.getBlockId());

				BlockModel<?> camoufledBlockModel = BlockModelDispatcher.getInstance().getDispatch(camoufledBlock);

				ProxyWorld proxyWorld = new ProxyWorld(worldSource);

				proxyWorld.setBlock(tilePos.x(), tilePos.y(), tilePos.z(), camouflageComponent.getBlockId(), camouflageComponent.getBlockMeta(), null);

				return camoufledBlockModel.render(tessellator, proxyWorld, tilePos);
			}
		}

		return false;
	}

	@Override
	public @Nullable IconCoordinate getParticleTexture(@NotNull Side side, int meta) {
		return particleTexture;
	}
}
