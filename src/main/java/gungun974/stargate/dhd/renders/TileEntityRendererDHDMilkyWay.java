package gungun974.stargate.dhd.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.dhd.DHDGeometry;
import gungun974.stargate.dhd.blocks.BlockLogicDHDMilkyWay;
import net.minecraft.client.render.tessellator.Tessellator;

public class TileEntityRendererDHDMilkyWay extends TileEntityRendererDHD {
	private static final WavefrontLoader[] KEYS = {
		new WavefrontLoader("/assets/stargate/models/DHD/024.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/006.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/029.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/038.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/034.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/012.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/037.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/011.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/021.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/003.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/004.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/020.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/009.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/005.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/032.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/001.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/019.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/022.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/007.obj"),

		new WavefrontLoader("/assets/stargate/models/DHD/028.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/010.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/033.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/039.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/026.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/023.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/018.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/014.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/017.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/002.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/025.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/036.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/008.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/027.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/031.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/015.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/035.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/030.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/016.obj")
	};

	private static final DHDGeometry.KeyPositions[] PRECOMPUTED_KEY_POSITIONS = generateKeyPositions(BlockLogicDHDMilkyWay.KEY_IDS);

	@Override
	protected DHDGeometry.KeyPositions[] getKeyPositions() {
		return PRECOMPUTED_KEY_POSITIONS;
	}

	@Override
	protected int[] getKeyIds() {
		return BlockLogicDHDMilkyWay.KEY_IDS;
	}

	@Override
	protected void renderDHD(Tessellator tessellator, WavefrontLoader dhd) {
		dhd.mapMaterial("foot", "foot");
		dhd.mapMaterial("plate", "plate");

		dhd.render(tessellator);

	}

	@Override
	protected void renderDial(Tessellator tessellator, WavefrontLoader button, boolean active) {
		if (active) {
			button.mapMaterial("button", "button_active");
		} else {
			button.mapMaterial("button", "button");
		}

		button.render(tessellator);
	}

	@Override
	protected void renderKey(Tessellator tessellator, int i, boolean isActive) {
		WavefrontLoader key = KEYS[i];

		if (isActive) {
			key.mapMaterial("glyph", "glyph_active");
		} else {
			key.mapMaterial("glyph", "glyph");
		}

		key.render(tessellator);
	}
}
