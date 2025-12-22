package gungun974.stargate.gate.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.gate.tiles.TileEntityStargateCore;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.tessellator.Tessellator;
import org.lwjgl.opengl.GL11;

public class TileEntityRenderStargateMilkyWay extends TileEntityRenderStargateCore {
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
    protected void renderFrame(Tessellator tessellator) {
        MilkywayRing.render(tessellator);
    }

    @Override
    protected void renderSymbolRing(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
        GL11.glPushMatrix();

        GL11.glRotatef((float) (stargateCore.interpolatedRingAngle(partialTicks)), 0, 0, 1);

        MilkywayGlyphs.render(tessellator);

        GL11.glPopMatrix();
    }

    protected void chevron(Tessellator tessellator, TileEntityStargateCore stargateCore, int chevron, float partialTicks) {
        double chevronDistance = stargateCore.interpolatedChevronDistance(chevron, partialTicks);

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

        if (chevronDistance != 0) {
            GL11.glPushMatrix();
            GL11.glTranslated(0, chevronDistance, 0);
        }
        ChevronLower.render(tessellator);

        if (!RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
            ChevronLowerLightBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronStatic");
            ChevronLowerLightBack.render(tessellator);

            if (chevronDistance != 0) {
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0, -chevronDistance, 0);
            }

            ChevronUpperBack.mapMaterial("MilkywayChevronOff", "MilkywayChevronStatic");
            ChevronUpperBack.render(tessellator);
        }

        if (chevronDistance != 0) {
            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);

        if (chevronActive && LightmapHelper.isLightmapEnabled()) {
            LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
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
            GL11.glPushMatrix();
            GL11.glTranslated(0, chevronDistance, 0);
        }
        ChevronLowerLightFront.render(tessellator);
        if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
            ChevronLowerLightBack.render(tessellator);
        }
        if (chevronDistance != 0) {
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0, -chevronDistance, 0);
        }

        ChevronUpperFront.render(tessellator);
        if (RENDER_CHEVRONS_LIGHTS_ON_BOTH_SIDES) {
            ChevronUpperBack.render(tessellator);
        }
        if (chevronDistance != 0) {
            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
