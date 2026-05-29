package gungun974.stargate.dhd.renders;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.dhd.DHDGeometry;
import gungun974.stargate.dhd.blocks.BlockLogicDHD;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.LightIndexHelper;
import org.joml.Vector3d;

public abstract class TileEntityRendererDHD extends TileEntityRenderer<TileEntityDHD> {
	private static final WavefrontLoader DHD = new WavefrontLoader("/assets/stargate/models/DHD/DHD.obj");

	private static final WavefrontLoader Button = new WavefrontLoader("/assets/stargate/models/DHD/button.obj");

	protected static DHDGeometry.KeyPositions[] generateKeyPositions(int[] keyIds) {
		int segments = keyIds.length / 2;
		DHDGeometry.KeyPositions[] positions = new DHDGeometry.KeyPositions[segments * 2];

		for (int i = 0; i < segments * 2; i++) {
			double angle = 360.0 * i / segments;

			if ((segments % 2) == 0) {
				angle += 180.0 / segments;
			}

			positions[i] = DHDGeometry.calculateKeyPositions(i, segments, angle);
		}

		return positions;
	}

	public static int setBlocklightValue(int lightmapCoord, int blocklight) {
		int skyLight = LightIndexHelper.skyLightFromIndex((byte) lightmapCoord);
		return LightIndexHelper.lightIndex2i(skyLight, blocklight);
	}

	protected abstract DHDGeometry.KeyPositions[] getKeyPositions();

	protected abstract int[] getKeyIds();

	@Override
	public void doRender(TessellatorGeneral tessellator, TileEntityDHD tileEntity, double x, double y, double z, float partialTicks) {
		GLRenderer.pushFrame();

		GLRenderer.modelM4f().translate((float) x + 0.5f, (float) y, (float) z + 0.5f);

		StargateComponent stargateComponent = null;
		TileEntityStargate gate = tileEntity.findLinkedGate();
		if (gate != null) {
			stargateComponent = gate.getStargateComponent();
		}

		int lightmap = tileEntity.getLightmap();

		Direction direction = BlockLogicDHD.getDirectionFromMeta(tileEntity.getBlockMeta());

		switch (direction) {
			case EAST:
				GLRenderer.modelM4f().rotate((float) Math.toRadians(90), 0, 1, 0);
				break;
			case NORTH:
				GLRenderer.modelM4f().rotate((float) Math.toRadians(180), 0, 1, 0);
				break;
			case SOUTH:
				break;
			case WEST:
				GLRenderer.modelM4f().rotate((float) Math.toRadians(-90), 0, 1, 0);
				break;
			default:
		}

		renderDHD(tessellator, DHD);

		boolean active = false;

		if (stargateComponent != null) {
			switch (stargateComponent.getState()) {
				case IDLE:
				case DIALLING:
				case AWAIT:
					break;
				case OPENING:
				case CONNECTED:
				case CLOSING:
					active = true;
			}
		}

		if (active) {
			GLRenderer.setLightmapCoord1i(setBlocklightValue(lightmap, Math.max(((lightmap >> 4) & 0xF), 14)));
		}

		renderDial(tessellator, Button, active);

		if (active) {
			GLRenderer.setLightmapCoord1i(lightmap);
		}

		int[] keyIds = getKeyIds();

		int segments = keyIds.length / 2;

		int addressSize = 0;
		StargateFamily family = StargateFamily.MilkyWay;

		if (stargateComponent != null) {
			addressSize = stargateComponent.getCurrentDialingAddressSize();
			family = stargateComponent.getFamily();
		}

		for (int i = 0; i < segments * 2; i++) {
			int keyId = keyIds[i];

			boolean isActiveKey = isKeyActive(stargateComponent, keyId);

			if (isActiveKey) {
				GLRenderer.setLightmapCoord1i(setBlocklightValue(lightmap, Math.max(((lightmap >> 4) & 0xF), 14)));
			}

			renderKey(tessellator, i, isActiveKey, addressSize, family, active || gate == null);

			if (isActiveKey) {
				GLRenderer.setLightmapCoord1i(setBlocklightValue(lightmap, Math.max(((lightmap >> 4) & 0xF), 10)));
			}

			DHDGeometry.KeyPositions positions = getKeyPositions()[i];

			tessellator.startDrawingQuads();

			Vector3d v1 = new Vector3d(positions.a3().x() - positions.a4().x(), positions.a3().y() - positions.a4().y(), positions.a3().z() - positions.a4().z());
			Vector3d v2 = new Vector3d(positions.a1().x() - positions.a4().x(), positions.a1().y() - positions.a4().y(), positions.a1().z() - positions.a4().z());
			Vector3d normal = v1.cross(v2, new Vector3d()).normalize();
			tessellator.setNormal((float) normal.x(), (float) normal.y(), (float) normal.z());
			tessellator.addVertexWithUV(positions.a4().x(), positions.a4().y(), positions.a4().z(), 0.1, 0);
			tessellator.addVertexWithUV(positions.a3().x(), positions.a3().y(), positions.a3().z(), 0.1, 0.1);
			tessellator.addVertexWithUV(positions.a2().x(), positions.a2().y(), positions.a2().z(), 0, 0.1);
			tessellator.addVertexWithUV(positions.a1().x(), positions.a1().y(), positions.a1().z(), 0, 0);


			v1 = new Vector3d(positions.b4().x() - positions.b1().x(), positions.b4().y() - positions.b1().y(), positions.b4().z() - positions.b1().z());
			v2 = new Vector3d(positions.a1().x() - positions.b1().x(), positions.a1().y() - positions.b1().y(), positions.a1().z() - positions.b1().z());
			normal = v1.cross(v2, new Vector3d()).normalize();
			tessellator.setNormal((float) normal.x(), (float) normal.y(), (float) normal.z());
			tessellator.addVertexWithUV(positions.b1().x(), positions.b1().y(), positions.b1().z(), 0, 0.1);
			tessellator.addVertexWithUV(positions.b4().x(), positions.b4().y(), positions.b4().z(), 0.1, 0.1);
			tessellator.addVertexWithUV(positions.a4().x(), positions.a4().y(), positions.a4().z(), 0.1, 0);
			tessellator.addVertexWithUV(positions.a1().x(), positions.a1().y(), positions.a1().z(), 0, 0);

			v1 = new Vector3d(positions.b3().x() - positions.a3().x(), positions.b3().y() - positions.a3().y(), positions.b3().z() - positions.a3().z());
			v2 = new Vector3d(positions.a2().x() - positions.a3().x(), positions.a2().y() - positions.a3().y(), positions.a2().z() - positions.a3().z());
			normal = v1.cross(v2, new Vector3d()).normalize();
			tessellator.setNormal((float) normal.x(), (float) normal.y(), (float) normal.z());
			tessellator.addVertexWithUV(positions.a3().x(), positions.a3().y(), positions.a3().z(), 0.1, 0);
			tessellator.addVertexWithUV(positions.b3().x(), positions.b3().y(), positions.b3().z(), 0.1, 0.1);
			tessellator.addVertexWithUV(positions.b2().x(), positions.b2().y(), positions.b2().z(), 0, 0.1);
			tessellator.addVertexWithUV(positions.a2().x(), positions.a2().y(), positions.a2().z(), 0, 0);

			v1 = new Vector3d(positions.b2().x() - positions.a2().x(), positions.b2().y() - positions.a2().y(), positions.b2().z() - positions.a2().z());
			v2 = new Vector3d(positions.a1().x() - positions.a2().x(), positions.a1().y() - positions.a2().y(), positions.a1().z() - positions.a2().z());
			normal = v1.cross(v2, new Vector3d()).normalize();
			tessellator.setNormal((float) normal.x(), (float) normal.y(), (float) normal.z());
			tessellator.addVertexWithUV(positions.a2().x(), positions.a2().y(), positions.a2().z(), 0.1, 0);
			tessellator.addVertexWithUV(positions.b2().x(), positions.b2().y(), positions.b2().z(), 0.1, 0.1);
			tessellator.addVertexWithUV(positions.b1().x(), positions.b1().y(), positions.b1().z(), 0, 0.1);
			tessellator.addVertexWithUV(positions.a1().x(), positions.a1().y(), positions.a1().z(), 0, 0);

			v1 = new Vector3d(positions.b3().x() - positions.b4().x(), positions.b3().y() - positions.b4().y(), positions.b3().z() - positions.b4().z());
			v2 = new Vector3d(positions.a4().x() - positions.b4().x(), positions.a4().y() - positions.b4().y(), positions.a4().z() - positions.b4().z());
			normal = v1.cross(v2, new Vector3d()).normalize();
			tessellator.setNormal((float) normal.x(), (float) normal.y(), (float) normal.z());
			tessellator.addVertexWithUV(positions.b4().x(), positions.b4().y(), positions.b4().z(), 0, 0.1);
			tessellator.addVertexWithUV(positions.b3().x(), positions.b3().y(), positions.b3().z(), 0.1, 0.1);
			tessellator.addVertexWithUV(positions.a3().x(), positions.a3().y(), positions.a3().z(), 0.1, 0);
			tessellator.addVertexWithUV(positions.a4().x(), positions.a4().y(), positions.a4().z(), 0, 0);

			tessellator.draw();

			GLRenderer.setLightmapCoord1i(lightmap);
		}

		GLRenderer.popFrame();
	}

	protected abstract void renderDHD(TessellatorGeneral tessellator, WavefrontLoader dhd);

	protected abstract void renderDial(TessellatorGeneral tessellator, WavefrontLoader button, boolean active);

	protected abstract void renderKey(TessellatorGeneral tessellator, int i, boolean isActive, int addressSize, StargateFamily family, boolean isGateActive);

	protected boolean isKeyActive(StargateComponent stargateComponent, int keyId) {
		if (stargateComponent == null) {
			return false;
		}

		if (stargateComponent.isReceiverGate()) {
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
