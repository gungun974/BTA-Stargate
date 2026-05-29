package gungun974.stargate.gate.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.gate.components.StargateComponent;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.util.helper.LightIndexHelper;

public class TileEntityRenderStargateMilkyWay extends TileEntityRenderStargate {
	static private final WavefrontLoader MilkywayRing = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayRing.obj");
	static private final WavefrontLoader MilkywayGlyphs = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayGlyphs.obj");
	static private final WavefrontLoader ChevronStatic = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayChevronStatic.obj");
	static private final WavefrontLoader ChevronUpperFront = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayChevronUpperFront.obj");
	static private final WavefrontLoader ChevronUpperBack = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayChevronUpperBack.obj");
	static private final WavefrontLoader ChevronLower = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayChevronLower.obj");
	static private final WavefrontLoader ChevronLowerLightFront = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayChevronLowerLightFront.obj");
	static private final WavefrontLoader ChevronLowerLightBack = new WavefrontLoader("/assets/stargate/models/Milkyway/MilkywayChevronLowerLightBack.obj");

	static private final boolean RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES = false;

	@Override
	protected void renderFrame(TessellatorGeneral tessellator, StargateComponent stargateCore, float partialTicks) {
		MilkywayRing.render(tessellator);
	}

	@Override
	protected void renderSymbolRing(TessellatorGeneral tessellator, StargateComponent stargateCore, float partialTicks) {
		GLRenderer.pushFrame();

		GLRenderer.modelM4f().rotate((float) Math.toRadians(stargateCore.interpolatedRingAngle(partialTicks)), 0, 0, 1);

		MilkywayGlyphs.render(tessellator);

		GLRenderer.popFrame();
	}

	protected void chevron(TessellatorGeneral tessellator, StargateComponent stargateCore, int chevron, float partialTicks) {
		double chevronDistance = stargateCore.interpolatedChevronDistance(chevron, partialTicks);

		boolean chevronActive = stargateCore.interpolatedChevronActive(chevron, partialTicks);

		ChevronStatic.render(tessellator);

		if (chevronDistance != 0) {
			GLRenderer.pushFrame();
			GLRenderer.modelM4f().translate(0, (float) chevronDistance, 0);
		}
		ChevronLower.render(tessellator);

		if (!RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
			ChevronLowerLightBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronStatic");
			ChevronLowerLightBack.render(tessellator);

			if (chevronDistance != 0) {
				GLRenderer.popFrame();
				GLRenderer.pushFrame();
				GLRenderer.modelM4f().translate(0, (float) -chevronDistance, 0);
			}

			ChevronUpperBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronStatic");
			ChevronUpperBack.render(tessellator);
		}

		if (chevronDistance != 0) {
			GLRenderer.popFrame();
		}

		GLRenderer.pushFrame();

		GLRenderer.enableState(State.BLEND);
		GLRenderer.globalSetLightEnabled(false);

		if (chevronActive) {
			GLRenderer.setLightmapCoord1i(LightIndexHelper.lightIndex2i(15, 15));
		}

		if (chevronActive) {
			ChevronLowerLightFront.mapMaterial("MilkywayChevronOn", "MilkywayChevronOn");
			ChevronUpperFront.mapMaterial("MilkywayChevronOn", "MilkywayChevronOn");
			if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
				ChevronLowerLightBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronOff");
				ChevronUpperBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronOn");
			}
		} else {
			ChevronLowerLightFront.mapMaterial("MilkywayChevronOn", "MilkywayChevronOff");
			ChevronUpperFront.mapMaterial("MilkywayChevronOn", "MilkywayChevronOff");
			if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
				ChevronLowerLightBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronOff");
				ChevronUpperBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronOff");
			}
		}

		if (chevronDistance != 0) {
			GLRenderer.pushFrame();
			GLRenderer.modelM4f().translate(0, (float) chevronDistance, 0);
		}
		ChevronLowerLightFront.render(tessellator);
		if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
			ChevronLowerLightBack.render(tessellator);
		}
		if (chevronDistance != 0) {
			GLRenderer.popFrame();
			GLRenderer.pushFrame();
			GLRenderer.modelM4f().translate(0, (float) -chevronDistance, 0);
		}

		ChevronUpperFront.render(tessellator);
		if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
			ChevronUpperBack.render(tessellator);
		}
		if (chevronDistance != 0) {
			GLRenderer.popFrame();
		}

		GLRenderer.pushFrame();

		GLRenderer.popFrame();

		GLRenderer.globalSetLightEnabled(true);
		GLRenderer.popFrame();
	}

	@Override
	void loadEventHorizonTexture() {
		this.bindTexture("/assets/stargate/textures/eventhorizon_milkyway.png");
	}
}
