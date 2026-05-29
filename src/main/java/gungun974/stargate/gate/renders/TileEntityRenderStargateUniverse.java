package gungun974.stargate.gate.renders;

import gungun974.stargate.core.StargateState;
import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.gate.components.StargateComponent;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.util.helper.LightIndexHelper;

public class TileEntityRenderStargateUniverse extends TileEntityRenderStargate {
	static private final WavefrontLoader UniverseGate = new WavefrontLoader("/assets/stargate/models/Universe/UniverseGate.obj");

	@Override
	protected void renderFrame(TessellatorGeneral tessellator, StargateComponent stargateCore, float partialTicks) {
		if (stargateCore.getState() == StargateState.IDLE && !stargateCore.isRingMove()) {
			UniverseGate.mapMaterial("UniverseOff", "UniverseOff");
		} else {
			UniverseGate.mapMaterial("UniverseOff", "UniverseOn");
		}

		GLRenderer.pushFrame();

		GLRenderer.modelM4f().rotate((float) (stargateCore.interpolatedRingAngle(partialTicks)), 0, 0, 1);

		UniverseGate.render(tessellator);

		GLRenderer.popFrame();
	}

	@Override
	protected void renderSymbolRing(TessellatorGeneral tessellator, StargateComponent stargateCore, float partialTicks) {
		GLRenderer.pushFrame();
		GLRenderer.globalSetLightEnabled(false);

		GLRenderer.setLightmapCoord1i(LightIndexHelper.lightIndex2i(15, 15));

		this.bindTexture("/assets/stargate/models/Universe/universe_stargate_glyphs.png");

		GLRenderer.modelM4f().scale(0.1f, 0.1f, 0.1f);

		float angleStep = 360f / 54f;

		float cellSize = 1f / 6f;

		float uvWidth = 0.08517f;
		float uvHeight = 0.161972f;

		float uvOffsetX = (cellSize - uvWidth) / 2f;
		float uvOffsetY = (cellSize - uvHeight) / 2f;

		float extraRotate = 10 - angleStep - angleStep;

		for (int segment = 0; segment < 36; segment++) {
			if (segment % 4 == 0) {
				extraRotate += angleStep * 2;
			}

			GLRenderer.pushFrame();

			GLRenderer.modelM4f().rotate((float) Math.toRadians(stargateCore.interpolatedRingAngle(partialTicks)), 0, 0, 1);
			GLRenderer.modelM4f().rotate((float) Math.toRadians(-segment * angleStep - extraRotate), 0, 0, 1);

			int i = segment % 6;
			int j = segment / 6;

			if (stargateCore.isReceiverGate()) {
				GLRenderer.setColor4f(0.15f, 0.15f, 0.15f, 1f);
			} else {
				if (stargateCore.interpolatedChevronActive(0, partialTicks) && segment == stargateCore.getChevronActiveSymbol(0)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(1, partialTicks) && segment == stargateCore.getChevronActiveSymbol(1)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(2, partialTicks) && segment == stargateCore.getChevronActiveSymbol(2)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(3, partialTicks) && segment == stargateCore.getChevronActiveSymbol(3)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(4, partialTicks) && segment == stargateCore.getChevronActiveSymbol(4)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(5, partialTicks) && segment == stargateCore.getChevronActiveSymbol(5)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(6, partialTicks) && segment == stargateCore.getChevronActiveSymbol(6)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(7, partialTicks) && segment == stargateCore.getChevronActiveSymbol(7)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else if (stargateCore.interpolatedChevronActive(8, partialTicks) && segment == stargateCore.getChevronActiveSymbol(8)) {
					GLRenderer.setColor4f(1f, 1f, 1f, 1f);
				} else {
					GLRenderer.setColor4f(0.15f, 0.15f, 0.15f, 1f);
				}
			}


			float u0 = i * cellSize + uvOffsetX;
			float u1 = i * cellSize + uvOffsetX + uvWidth;

			float v0 = j * cellSize + uvOffsetY;
			float v1 = j * cellSize + uvOffsetY + uvHeight;

			tessellator.startDrawingQuads();

			tessellator.addVertexWithUV(-1.23574, 32.5467, 0.815, u0, v0);
			tessellator.addVertexWithUV(-1.23574, 27.8465, 0.815, u0, v1);
			tessellator.addVertexWithUV(1.23574, 27.8465, 0.815, u1, v1);
			tessellator.addVertexWithUV(1.23574, 32.5467, 0.815, u1, v0);

			tessellator.draw();

			GLRenderer.popFrame();
		}

		GLRenderer.globalSetLightEnabled(true);
		GLRenderer.setColor4f(1, 1, 1, 1f);
		GLRenderer.popFrame();
	}

	@Override
	void loadEventHorizonTexture() {
		this.bindTexture("/assets/stargate/textures/eventhorizon_universe.png");
	}
}
