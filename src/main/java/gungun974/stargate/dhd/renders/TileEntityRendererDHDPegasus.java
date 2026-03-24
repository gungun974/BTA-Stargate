package gungun974.stargate.dhd.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.dhd.DHDGeometry;
import gungun974.stargate.dhd.blocks.BlockLogicDHDPegasus;
import net.minecraft.client.render.tessellator.Tessellator;

public class TileEntityRendererDHDPegasus extends TileEntityRendererDHD {
	private static final DHDGeometry.KeyPositions[] PRECOMPUTED_KEY_POSITIONS = generateKeyPositions(BlockLogicDHDPegasus.KEY_IDS);

	@Override
	protected DHDGeometry.KeyPositions[] getKeyPositions() {
		return PRECOMPUTED_KEY_POSITIONS;
	}

	@Override
	protected int[] getKeyIds() {
		return BlockLogicDHDPegasus.KEY_IDS;
	}

	@Override
	protected void renderDHD(Tessellator tessellator, WavefrontLoader dhd) {
		dhd.mapMaterial("foot", "foot_pegasus");
		dhd.mapMaterial("plate", "plate_pegasus");

		dhd.render(tessellator);

	}

	@Override
	protected void renderDial(Tessellator tessellator, WavefrontLoader button, boolean active) {
		if (active) {
			button.mapMaterial("button", "button_active_pegasus");
		} else {
			button.mapMaterial("button", "button_pegasus");
		}

		button.render(tessellator);
	}

	@Override
	protected void renderKey(Tessellator tessellator, int i, boolean isActive) {
		if (isActive) {
			this.loadTexture("/assets/stargate/models/DHD/pegasus_key_active.png");
		} else {
			this.loadTexture("/assets/stargate/models/DHD/pegasus_key.png");
		}
	}
}
