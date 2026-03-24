package gungun974.stargate.dhd.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.dhd.DHDGeometry;
import gungun974.stargate.dhd.blocks.BlockLogicDHDPegasus;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.util.phys.Vec3;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererDHDPegasus extends TileEntityRendererDHD {
	private static final WavefrontLoader ButtonFrame = new WavefrontLoader("/assets/stargate/models/DHD/PegasusButtonFrame.obj");

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

		ButtonFrame.mapMaterial("foot", "foot_pegasus");
		ButtonFrame.mapMaterial("plate", "plate_pegasus");

		ButtonFrame.render(tessellator);

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
		this.loadTexture("/assets/stargate/models/Pegasus/pegasus_stargate_glyphs.png");

		int keyId = BlockLogicDHDPegasus.KEY_IDS[i];

		float cellSize = 1f / 6f;

		float uvWidth = 0.166667f;
		float uvHeight = 0.139506f;

		float uvOffsetX = (cellSize - uvWidth) / 2f;
		float uvOffsetY = (cellSize - uvHeight) / 2f;

		int j = keyId % 6;
		int k = keyId / 6;

		float u0 = j * cellSize + uvOffsetX;
		float u1 = j * cellSize + uvOffsetX + uvWidth;

		float v0 = k * cellSize + uvOffsetY;
		float v1 = k * cellSize + uvOffsetY + uvHeight;

		DHDGeometry.KeyPositions positions = getKeyPositions()[i];

		if (!isActive) {
			GL11.glColor4f(0.55f, 0.55f, 0.55f, 1);
		}

		tessellator.startDrawingQuads();

		Vec3 vm1 = Vec3.getTempVec3(positions.c3.x - positions.c4.x, positions.c3.y - positions.c4.y, positions.c3.z - positions.c4.z);
		Vec3 vm2 = Vec3.getTempVec3(positions.c1.x - positions.c4.x, positions.c1.y - positions.c4.y, positions.c1.z - positions.c4.z);
		Vec3 normal = vm1.crossProduct(vm2).normalize();
		tessellator.setNormal((float) normal.x, (float) normal.y, (float) normal.z);

		if (i >= BlockLogicDHDPegasus.KEY_IDS.length / 2) {
			tessellator.addVertexWithUV(positions.c4.x, positions.c4.y + 0.0001, positions.c4.z, u1, v1);
			tessellator.addVertexWithUV(positions.c3.x, positions.c3.y + 0.0001, positions.c3.z, u1, v0);
			tessellator.addVertexWithUV(positions.c2.x, positions.c2.y + 0.0001, positions.c2.z, u0, v0);
			tessellator.addVertexWithUV(positions.c1.x, positions.c1.y + 0.0001, positions.c1.z, u0, v1);
		} else {
			tessellator.addVertexWithUV(positions.c4.x, positions.c4.y + 0.0001, positions.c4.z, u0, v1);
			tessellator.addVertexWithUV(positions.c3.x, positions.c3.y + 0.0001, positions.c3.z, u1, v1);
			tessellator.addVertexWithUV(positions.c2.x, positions.c2.y + 0.0001, positions.c2.z, u1, v0);
			tessellator.addVertexWithUV(positions.c1.x, positions.c1.y + 0.0001, positions.c1.z, u0, v0);
		}

		tessellator.draw();

		if (!isActive) {
			GL11.glColor4f(1, 1, 1, 1);
		}

		if (isActive) {
			this.loadTexture("/assets/stargate/models/DHD/pegasus_key_active.png");
		} else {
			this.loadTexture("/assets/stargate/models/DHD/pegasus_key.png");
		}
	}
}
