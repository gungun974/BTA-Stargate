package gungun974.stargate.gate.renders;

import gungun974.stargate.core.StargateState;
import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.gate.tiles.TileEntityStargateCore;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.tessellator.Tessellator;
import org.lwjgl.opengl.GL11;

public class TileEntityRenderStargateUniverse extends TileEntityRenderStargateCore {
	static private final WavefrontLoader UniverseGate = new WavefrontLoader("/assets/stargate/models/Universe/UniverseGate.obj");

	@Override
	protected void renderFrame(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
		if (stargateCore.getState() == StargateState.IDLE && !stargateCore.isRingMove()) {
			UniverseGate.mapMaterial("UniverseOff", "UniverseOff");
		} else {
			UniverseGate.mapMaterial("UniverseOff", "UniverseOn");
		}

		GL11.glPushMatrix();

		GL11.glRotatef((float) (stargateCore.interpolatedRingAngle(partialTicks)), 0, 0, 1);

		UniverseGate.render(tessellator);

		GL11.glPopMatrix();
	}

	@Override
	protected void renderSymbolRing(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);

		if (LightmapHelper.isLightmapEnabled()) {
			int lightmap = stargateCore.getLightmap();
			LightmapHelper.setLightmapCoord(LightmapHelper.setBlocklightValue(lightmap, Math.min(((lightmap >> 4) & 0xF) + 1, 15)));

			LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
		}


		this.loadTexture("/assets/stargate/models/Universe/universe_stargate_glyphs.png");

		GL11.glScaled(0.1, 0.1, 0.1);

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

			GL11.glPushMatrix();

			GL11.glRotatef((float) (stargateCore.interpolatedRingAngle(partialTicks)), 0, 0, 1);
			GL11.glRotatef(-segment * angleStep - extraRotate, 0, 0, 1);

			int i = segment % 6;
			int j = segment / 6;

			if (stargateCore.interpolatedChevronActive(0, partialTicks) && segment == stargateCore.getChevronActiveSymbol(0)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(1, partialTicks) && segment == stargateCore.getChevronActiveSymbol(1)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(2, partialTicks) && segment == stargateCore.getChevronActiveSymbol(2)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(3, partialTicks) && segment == stargateCore.getChevronActiveSymbol(3)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(4, partialTicks) && segment == stargateCore.getChevronActiveSymbol(4)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(5, partialTicks) && segment == stargateCore.getChevronActiveSymbol(5)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(6, partialTicks) && segment == stargateCore.getChevronActiveSymbol(6)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(7, partialTicks) && segment == stargateCore.getChevronActiveSymbol(7)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else if (stargateCore.interpolatedChevronActive(8, partialTicks) && segment == stargateCore.getChevronActiveSymbol(8)) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else {
				GL11.glColor4f(0.15f, 0.15f, 0.15f, 1f);
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

			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1f);
		GL11.glPopMatrix();
	}

	@Override
	void loadEventHorizonTexture() {
		this.loadTexture("/assets/stargate/textures/eventhorizon_universe.png");
	}
}
