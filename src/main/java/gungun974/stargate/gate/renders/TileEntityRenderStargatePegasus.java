package gungun974.stargate.gate.renders;

import gungun974.stargate.core.StargateState;
import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.gate.components.StargateComponent;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.tessellator.Tessellator;
import org.lwjgl.opengl.GL11;

public class TileEntityRenderStargatePegasus extends TileEntityRenderStargate {
	static private final WavefrontLoader PegasusRing = new WavefrontLoader("/assets/stargate/models/Pegasus/PegasusRing.obj");
	static private final WavefrontLoader ChevronStatic = new WavefrontLoader("/assets/stargate/models/Pegasus/PegasusChevronStatic.obj");
	static private final WavefrontLoader ChevronUpperFront = new WavefrontLoader("/assets/stargate/models/Pegasus/PegasusChevronUpperFront.obj");
	static private final WavefrontLoader ChevronUpperBack = new WavefrontLoader("/assets/stargate/models/Pegasus/PegasusChevronUpperBack.obj");
	static private final WavefrontLoader ChevronLower = new WavefrontLoader("/assets/stargate/models/Pegasus/PegasusChevronLower.obj");
	static private final WavefrontLoader ChevronLowerLightFront = new WavefrontLoader("/assets/stargate/models/Pegasus/PegasusChevronLowerLightFront.obj");
	static private final WavefrontLoader ChevronLowerLightBack = new WavefrontLoader("/assets/stargate/models/Pegasus/PegasusChevronLowerLightBack.obj");

	static private final boolean RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES = true;

	@Override
	protected void renderFrame(Tessellator tessellator, StargateComponent stargateCore, float partialTicks) {
		PegasusRing.render(tessellator);
	}

	@Override
	protected void renderSymbolRing(Tessellator tessellator, StargateComponent stargateCore, float partialTicks) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);

		if (LightmapHelper.isLightmapEnabled()) {
			int lightmap = stargateCore.getLightmap();
			LightmapHelper.setLightmapCoord(LightmapHelper.setBlocklightValue(lightmap, Math.min(((lightmap >> 4) & 0xF) + 1, 15)));

			LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
		}

		if (stargateCore.getState() == StargateState.IDLE) {
			GL11.glColor4f(0.4f, 0.67f, 1.0f, 0.5f);
		} else {
			GL11.glColor4f(0.4f, 0.67f, 1.0f, 1f);
		}

		this.loadTexture("/assets/stargate/models/Pegasus/pegasus_stargate_glyphs.png");

		GL11.glScaled(0.1, 0.1, 0.1);

		float angleStep = 360f / 36f;

		float cellSize = 1f / 6f;

		float uvWidth = 0.166667f;
		float uvHeight = 0.139506f;

		float uvOffsetX = (cellSize - uvWidth) / 2f;
		float uvOffsetY = (cellSize - uvHeight) / 2f;

		for (int segment = 0; segment < 36; segment++) {
			int glyph = -1;
			if (stargateCore.getState() == StargateState.IDLE) {
				glyph = segment;
			}

			if (glyph == -1) {
				if ((segment % 4 == 0)) {
					int currentChevron;

					switch (segment) {
						case 8:
							currentChevron = 1;
							break;
						case 12:
							currentChevron = 2;
							break;
						case 24:
							currentChevron = 3;
							break;
						case 28:
							currentChevron = 4;
							break;
						case 32:
							currentChevron = 5;
							break;
						case 0:
							currentChevron = 6;
							break;
						case 16:
							currentChevron = 7;
							break;
						case 20:
							currentChevron = 8;
							break;
						default:
							currentChevron = 0;
							break;
					}

					boolean chevronActive = stargateCore.interpolatedChevronActive(currentChevron, partialTicks);

					if (chevronActive) {
						glyph = stargateCore.getChevronActiveSymbol(currentChevron);
					}
				}
			}

			if (glyph == -1) {
				int currentSegment = ((int) Math.round((((stargateCore.interpolatedRingAngle(partialTicks) % 360) + 360) % 360) / angleStep)) % 36;

				if (segment == currentSegment) {
					glyph = stargateCore.getCurrentSymbol();
				}
			}

			if (glyph == -1) {
				continue;
			}

			GL11.glPushMatrix();

			GL11.glRotatef(-segment * angleStep, 0, 0, 1);

			int i = glyph % 6;
			int j = glyph / 6;

			float u0 = i * cellSize + uvOffsetX;
			float u1 = i * cellSize + uvOffsetX + uvWidth;

			float v0 = j * cellSize + uvOffsetY;
			float v1 = j * cellSize + uvOffsetY + uvHeight;

			tessellator.startDrawingQuads();

			tessellator.addVertexWithUV(-1.89212, 30.7384, 0.362129, u0, v0);
			tessellator.addVertexWithUV(-1.89212, 27.571, 0.328327, u0, v1);
			tessellator.addVertexWithUV(1.89212, 27.571, 0.328327, u1, v1);
			tessellator.addVertexWithUV(1.89212, 30.7384, 0.362129, u1, v0);

			tessellator.draw();

			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1f);
		GL11.glPopMatrix();
	}

	protected void chevron(Tessellator tessellator, StargateComponent stargateCore, int chevron, float partialTicks) {
		boolean chevronActive = stargateCore.interpolatedChevronActive(chevron, partialTicks);

		if (LightmapHelper.isLightmapEnabled()) {
			int lightmap = stargateCore.getLightmap();
			if (chevronActive) {
				LightmapHelper.setLightmapCoord(LightmapHelper.setBlocklightValue(lightmap, Math.min(((lightmap >> 4) & 0xF) + 1, 15)));
			} else {
				LightmapHelper.setLightmapCoord(lightmap);
			}
		}

		ChevronStatic.render(tessellator);

		ChevronLower.render(tessellator);

		if (!RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
			ChevronLowerLightBack.mapMaterial("PegasusChevronOff", "PegasusChevronStatic");
			ChevronLowerLightBack.render(tessellator);

			ChevronUpperBack.mapMaterial("PegasusChevronOff", "PegasusChevronStatic");
			ChevronUpperBack.render(tessellator);
		}

		GL11.glPushMatrix();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);

		if (chevronActive && LightmapHelper.isLightmapEnabled()) {
			LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
		}

		if (chevronActive) {
			ChevronLowerLightFront.mapMaterial("PegasusChevronOn", "PegasusChevronOn");
			ChevronUpperFront.mapMaterial("PegasusChevronOn", "PegasusChevronOn");
			if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
				ChevronLowerLightBack.mapMaterial("PegasusChevronOff", "PegasusChevronOff");
				ChevronUpperBack.mapMaterial("PegasusChevronOff", "PegasusChevronOn");
			}
		} else {
			ChevronLowerLightFront.mapMaterial("PegasusChevronOn", "PegasusChevronOff");
			ChevronUpperFront.mapMaterial("PegasusChevronOn", "PegasusChevronOff");
			if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
				ChevronLowerLightBack.mapMaterial("PegasusChevronOff", "PegasusChevronOff");
				ChevronUpperBack.mapMaterial("PegasusChevronOff", "PegasusChevronOff");
			}
		}

		ChevronLowerLightFront.render(tessellator);
		if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
			ChevronLowerLightBack.render(tessellator);
		}

		ChevronUpperFront.render(tessellator);
		if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
			ChevronUpperBack.render(tessellator);
		}

		GL11.glPushMatrix();

		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	void loadEventHorizonTexture() {
		this.loadTexture("/assets/stargate/textures/eventhorizon_pegasus.png");
	}
}
