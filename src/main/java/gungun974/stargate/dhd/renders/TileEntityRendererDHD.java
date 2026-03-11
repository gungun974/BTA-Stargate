package gungun974.stargate.dhd.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.dhd.blocks.BlockLogicDHD;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.util.helper.Direction;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererDHD extends TileEntityRenderer<TileEntityDHD> {

	private static final WavefrontLoader DHD = new WavefrontLoader("/assets/stargate/models/DHD/DHD.obj");

	private static final int[] KEY_IDS = {
		1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
		14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
		26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39
	};

	private static final WavefrontLoader[] KEYS = {
		new WavefrontLoader("/assets/stargate/models/DHD/001.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/002.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/003.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/004.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/005.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/006.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/007.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/008.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/009.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/010.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/011.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/012.obj"),

		new WavefrontLoader("/assets/stargate/models/DHD/014.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/015.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/016.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/017.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/018.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/019.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/020.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/021.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/022.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/023.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/024.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/025.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/026.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/027.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/028.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/029.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/030.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/031.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/032.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/033.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/034.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/035.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/036.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/037.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/038.obj"),
		new WavefrontLoader("/assets/stargate/models/DHD/039.obj")
	};

	@Override
	public void doRender(Tessellator tessellator, TileEntityDHD tileEntity, double x, double y, double z, float partialTicks) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);

		StargateComponent stargateComponent = null;
		TileEntityStargate gate = tileEntity.findLinkedGate();
		if (gate != null) {
			stargateComponent = gate.getStargateComponent();
		}

		Direction direction = BlockLogicDHD.getDirectionFromMeta(tileEntity.getBlockMeta());

		switch (direction) {
			case EAST:
				GL11.glRotatef(90, 0, 1, 0);
				break;
			case NORTH:
				GL11.glRotatef(180, 0, 1, 0);
				break;
			case SOUTH:
				break;
			case WEST:
				GL11.glRotatef(-90, 0, 1, 0);
				break;
			default:
		}

		DHD.render(tessellator);

		for (int i = 0; i < KEYS.length; i++) {
			int keyId = KEY_IDS[i];
			WavefrontLoader key = KEYS[i];

			if (isKeyActive(stargateComponent, keyId)) {
				key.mapMaterial("glyph", "glyph_active");
			} else {
				key.mapMaterial("glyph", "glyph");
			}

			key.render(tessellator);
		}

		GL11.glPopMatrix();
	}

	private boolean isKeyActive(StargateComponent stargateComponent, int keyId) {
		if (stargateComponent == null) {
			return false;
		}

		int[] address = stargateComponent.getCurrentDialingAddress();
		if (address == null) {
			return false;
		}

		for (int value : address) {
			if (value == keyId) {
				return true;
			}
		}

		return false;
	}
}
