package gungun974.stargate.gate.blocks.core;

import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

enum TextureIndex {
	RING_FACE(1),
	RING(0),
	RING_SYMBOL(32),
	CHEVRON(3),
	CHEVRON_LIT(2);

	public final int index;

	TextureIndex(int index) {
		this.index = index;
	}
}

public class TileEntityRenderStargateCore extends TileEntityRenderer<TileEntityStargateCore> {
	final static int numRingSegments = 32;
	final static double ringInnerRadius = 2.0;
	final static double ringMidRadius = 2.25;
	final static double ringOuterRadius = 2.5;
	final static double ringDepth = 0.5;
	final static double ringOverlap = 1 / 64.0;
	final static double ringZOffset = 0.0001;
	final static double chevronInnerRadius = 2.25;
	final static double chevronOuterRadius = ringOuterRadius + 1 / 16.0;
	final static double chevronWidth = (chevronOuterRadius - chevronInnerRadius) * 1.5;
	final static double chevronBorderWidth = chevronWidth / 6;
	final static double chevronDepth = 0.125;
	final static double chevronMotionDistance = 1 / 8.0;
	final static int textureTilesWide = 32;
	final static int textureTilesHigh = 2;
	final static double textureScaleU = 1.0 / (textureTilesWide * 16);
	final static double textureScaleV = 1.0 / (textureTilesHigh * 16);
	final static double ringSymbolTextureLength = 512.0;
	final static double ringSymbolTextureHeight = 16.0;
	final static double ringSymbolSegmentWidth = ringSymbolTextureLength / numRingSegments;

	static double[] ringSinValues = new double[numRingSegments + 1];
	static double[] ringCosValues = new double[numRingSegments + 1];


	static {
		for (int i = 0; i <= numRingSegments; i++) {
			double a = 2 * Math.PI * i / numRingSegments;
			ringSinValues[i] = Math.sin(a);
			ringCosValues[i] = Math.cos(a);
		}
	}

	private double u0, v0;

	@Override
	public void doRender(Tessellator tessellator, TileEntityStargateCore tileEntity, double x, double y, double z, float partialTicks) {
		if (!tileEntity.isAssembled()) {
			return;
		}

		GL11.glPushMatrix();

		GL11.glTranslatef((float) x, (float) y, (float) z);

		if (LightmapHelper.isLightmapEnabled()) {
			LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
		}

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		switch (tileEntity.getDirection()) {
			case NORTH:
				GL11.glTranslated(0.5, 2.5, 0.5);
				break;
			case EAST:
				GL11.glRotatef(-90, 0, 180, 0);
				GL11.glTranslated(0.5, 2.5, -0.5);
				break;
			case SOUTH:
				GL11.glRotatef(180, 0, 180, 0);
				GL11.glTranslated(-0.5, 2.5, -0.5);
				break;
			case WEST:
				GL11.glRotatef(90, 0, 180, 0);
				GL11.glTranslated(-0.5, 2.5, 0.5);
				break;
		}

		renderStargate(tessellator, tileEntity, partialTicks);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	void renderStargate(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
		this.loadTexture("/assets/minecraft/textures/block/chest/planks/top.png");

		renderOuterRing(tessellator);
		renderInnerRing(tessellator, stargateCore, partialTicks);
		renderChevrons(tessellator);
		renderIris(tessellator, stargateCore, partialTicks);
		renderEventHorizon(tessellator, stargateCore, partialTicks);
	}

	private void renderOuterRing(Tessellator tessellator) {
		double z = ringDepth / 2 + ringZOffset;
		double u, du, dv;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 1, 0);
		for (int i = 0; i < numRingSegments; i++) {
			selectTile(TextureIndex.RING);
			tessellator.setNormal((float) ringCosValues[i], (float) ringSinValues[i], 0);
			vertex(tessellator, ringOuterRadius * ringCosValues[i], ringOuterRadius * ringSinValues[i], z, 0, 0);
			vertex(tessellator, ringOuterRadius * ringCosValues[i], ringOuterRadius * ringSinValues[i], -z, 0, 16);
			vertex(tessellator, ringOuterRadius * ringCosValues[i + 1], ringOuterRadius * ringSinValues[i + 1], -z, 16, 16);
			vertex(tessellator, ringOuterRadius * ringCosValues[i + 1], ringOuterRadius * ringSinValues[i + 1], z, 16, 0);
			// Back
			tessellator.setNormal(0, 0, -1);
			vertex(tessellator, (ringMidRadius - ringOverlap) * ringCosValues[i], (ringMidRadius - ringOverlap) * ringSinValues[i], -z, 0, 16);
			vertex(tessellator, (ringMidRadius - ringOverlap) * ringCosValues[i + 1], (ringMidRadius - ringOverlap) * ringSinValues[i + 1], -z, 16, 16);
			vertex(tessellator, ringOuterRadius * ringCosValues[i + 1], ringOuterRadius * ringSinValues[i + 1], -z, 16, 0);
			vertex(tessellator, ringOuterRadius * ringCosValues[i], ringOuterRadius * ringSinValues[i], -z, 0, 0);
			// Front
			tessellator.setNormal(0, 0, 1);
			selectTile(TextureIndex.RING_FACE);
			u = 0;
			du = 16;
			dv = 16;
			vertex(tessellator, (ringMidRadius - ringOverlap) * ringCosValues[i], (ringMidRadius - ringOverlap) * ringSinValues[i], z, u + du, dv);
			vertex(tessellator, ringOuterRadius * ringCosValues[i], ringOuterRadius * ringSinValues[i], z, u + du, 0);
			vertex(tessellator, ringOuterRadius * ringCosValues[i + 1], ringOuterRadius * ringSinValues[i + 1], z, u, 0);
			vertex(tessellator, (ringMidRadius - ringOverlap) * ringCosValues[i + 1], (ringMidRadius - ringOverlap) * ringSinValues[i + 1], z, u, dv);
		}

		tessellator.draw();
	}

	void renderInnerRing(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
		GL11.glPushMatrix();
		double z = ringDepth / 2 + (double) 0;
		double u = 0, du = 0, dv = 0;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 1, 0);
		for (int i = 0; i < numRingSegments; i++) {
			selectTile(TextureIndex.RING);
			tessellator.setNormal((float) -ringCosValues[i], (float) -ringSinValues[i], 0);
			vertex(tessellator, ringInnerRadius * ringCosValues[i], ringInnerRadius * ringSinValues[i], -z, 0, 0);
			vertex(tessellator, ringInnerRadius * ringCosValues[i], ringInnerRadius * ringSinValues[i], z, 0, 16);
			vertex(tessellator, ringInnerRadius * ringCosValues[i + 1], ringInnerRadius * ringSinValues[i + 1], z, 16, 16);
			vertex(tessellator, ringInnerRadius * ringCosValues[i + 1], ringInnerRadius * ringSinValues[i + 1], -z, 16, 0);
			// Back
			tessellator.setNormal(0, 0, -1);
			vertex(tessellator, ringInnerRadius * ringCosValues[i], ringInnerRadius * ringSinValues[i], -z, 0, 16);
			vertex(tessellator, ringInnerRadius * ringCosValues[i + 1], ringInnerRadius * ringSinValues[i + 1], -z, 16, 16);
			vertex(tessellator, ringMidRadius * ringCosValues[i + 1], ringMidRadius * ringSinValues[i + 1], -z, 16, 0);
			vertex(tessellator, ringMidRadius * ringCosValues[i], ringMidRadius * ringSinValues[i], -z, 0, 0);
			// Front
			tessellator.setNormal(0, 0, 1);
			selectTile(TextureIndex.RING_SYMBOL);
			u = ringSymbolTextureLength - (i + 1) * ringSymbolSegmentWidth;
			du = ringSymbolSegmentWidth;
			dv = ringSymbolTextureHeight;
			vertex(tessellator, ringInnerRadius * ringCosValues[i], ringInnerRadius * ringSinValues[i], z, u + du, dv);
			vertex(tessellator, ringMidRadius * ringCosValues[i], ringMidRadius * ringSinValues[i], z, u + du, 0);
			vertex(tessellator, ringMidRadius * ringCosValues[i + 1], ringMidRadius * ringSinValues[i + 1], z, u, 0);
			vertex(tessellator, ringInnerRadius * ringCosValues[i + 1], ringInnerRadius * ringSinValues[i + 1], z, u, dv);
		}

		tessellator.draw();

		GL11.glPopMatrix();
	}

	void renderChevrons(Tessellator tessellator) {
		for (int i = 1; i < 8; i++) {
			renderChevronAtPosition(tessellator, i, 45, false);
		}
	}

	void renderChevronAtPosition(Tessellator tessellator, int chevronNumber, float angleBetweenChevrons, boolean engaged) {
		GL11.glPushMatrix();

		GL11.glRotatef(90 - (chevronNumber - 4) * angleBetweenChevrons, 0, 0, 1);

		chevron(tessellator, engaged);
		GL11.glPopMatrix();
	}

	void chevron(Tessellator tessellator, boolean engaged) {
		double z2 = ringDepth / 2;
		double z1 = z2 + chevronDepth;
		double w1 = chevronBorderWidth;
		double w2 = w1 * 1.25;
		double x1 = chevronInnerRadius, y1 = chevronWidth / 4;
		double x2 = chevronOuterRadius, y2 = chevronWidth / 2;

		if (engaged)
			GL11.glTranslated(-chevronMotionDistance, 0, 0);
		tessellator.startDrawingQuads();

		selectTile(TextureIndex.CHEVRON);

		// Face 1
		vertex(tessellator, x2, y2, z1, 0, 2);
		vertex(tessellator, x1, y1, z1, 0, 16);
		vertex(tessellator, x1 + w1, y1 - w1, z1, 4, 12);
		vertex(tessellator, x2, y2 - w2, z1, 4, 2);

		// Side 1
		vertex(tessellator, x2, y2, z1, 0, 0);
		vertex(tessellator, x2, y2, z2, 0, 4);
		vertex(tessellator, x1, y1, z2, 16, 4);
		vertex(tessellator, x1, y1, z1, 16, 0);

		// End 1
		vertex(tessellator, x2, y2, z1, 16, 0);
		vertex(tessellator, x2, y2 - w2, z1, 12, 0);
		vertex(tessellator, x2, y2 - w2, z2, 12, 4);
		vertex(tessellator, x2, y2, z2, 16, 4);

		// Face 2
		vertex(tessellator, x1 + w1, y1 - w1, z1, 4, 12);
		vertex(tessellator, x1, y1, z1, 0, 16);
		vertex(tessellator, x1, -y1, z1, 16, 16);
		vertex(tessellator, x1 + w1, -y1 + w1, z1, 12, 12);

		// Side 2
		vertex(tessellator, x1, y1, z1, 0, 0);
		vertex(tessellator, x1, y1, z2, 0, 4);
		vertex(tessellator, x1, -y1, z2, 16, 4);
		vertex(tessellator, x1, -y1, z1, 16, 0);

		// Face 3
		vertex(tessellator, x2, -y2 + w2, z1, 12, 0);
		vertex(tessellator, x1 + w1, -y1 + w1, z1, 12, 12);
		vertex(tessellator, x1, -y1, z1, 16, 16);
		vertex(tessellator, x2, -y2, z1, 16, 0);

		// Side 3
		vertex(tessellator, x1, -y1, z1, 0, 0);
		vertex(tessellator, x1, -y1, z2, 0, 4);
		vertex(tessellator, x2, -y2, z2, 16, 4);
		vertex(tessellator, x2, -y2, z1, 16, 0);

		// End 3
		vertex(tessellator, x2, -y2, z1, 0, 0);
		vertex(tessellator, x2, -y2, z2, 0, 4);
		vertex(tessellator, x2, -y2 + w2, z2, 4, 4);
		vertex(tessellator, x2, -y2 + w2, z1, 4, 0);

		// Back
		vertex(tessellator, x2, -y2, z2, 0, 0);
		vertex(tessellator, x1, -y1, z2, 0, 16);
		vertex(tessellator, x1, y1, z2, 16, 16);
		vertex(tessellator, x2, y2, z2, 16, 0);

		tessellator.draw();

		selectTile(TextureIndex.CHEVRON_LIT);
		if (!engaged)
			GL11.glColor3d(0.5, 0.5, 0.5);
		else {
			GL11.glDisable(GL11.GL_LIGHTING);
		}
		tessellator.startDrawingQuads();

		// Face 4
		vertex(tessellator, x2, y2 - w2, z1, 0, 4);
		vertex(tessellator, x1 + w1, y1 - w1, z1, 4, 16);
		vertex(tessellator, x1 + w1, 0, z1, 8, 16);
		vertex(tessellator, x2, 0, z1, 8, 4);

		vertex(tessellator, x2, 0, z1, 8, 4);
		vertex(tessellator, x1 + w1, 0, z1, 8, 16);
		vertex(tessellator, x1 + w1, -y1 + w1, z1, 12, 16);
		vertex(tessellator, x2, -y2 + w2, z1, 16, 4);

		// End 4
		vertex(tessellator, x2, y2 - w2, z2, 0, 0);
		vertex(tessellator, x2, y2 - w2, z1, 0, 4);
		vertex(tessellator, x2, -y2 + w2, z1, 16, 4);
		vertex(tessellator, x2, -y2 + w2, z2, 16, 0);

		GL11.glColor3f(1, 1, 1);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	void renderEventHorizon(Tessellator tessellator, TileEntityStargateCore tileEntity, float partialTicks) {
	}

	void renderIris(Tessellator tessellator, TileEntityStargateCore tileEntity, float partialTicks) {
	}

	void selectTile(TextureIndex index) {
		u0 = (index.index % textureTilesWide) * (textureScaleU * 16);
		v0 = ((double) index.index / textureTilesWide) * (textureScaleV * 16);
	}

	void vertex(Tessellator tessellator, double x, double y, double z, double u, double v) {
		tessellator.setTextureUV(u0 + u * textureScaleU, v0 + v * textureScaleV);
		tessellator.addVertex(x, y, z);
	}
}
