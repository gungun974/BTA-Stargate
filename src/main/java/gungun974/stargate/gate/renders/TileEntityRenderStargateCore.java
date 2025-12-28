package gungun974.stargate.gate.renders;

import gungun974.stargate.core.CustomLighting;
import gungun974.stargate.core.StargateState;
import gungun974.stargate.gate.tiles.TileEntityStargateCore;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.util.helper.Direction;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class TileEntityRenderStargateCore extends TileEntityRenderer<TileEntityStargateCore> {
	public final static int eventHorizonGridRadialSize = 8;
	final static int numRingSegments = 39 * 2;
	public final static int eventHorizonGridPolarSize = numRingSegments;
	final static double ringInnerRadius = 2.05;
	public final static double eventHorizonBandWidth = ringInnerRadius / eventHorizonGridRadialSize;
	public final static double eventHorizonVortexBaseWidth = ringInnerRadius * 0.48;
	public final static double eventHorizonVortexMaxDepth = ringInnerRadius * 1.55;

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

		CustomLighting.enableLight();

		GL11.glPushMatrix();

		GL11.glTranslatef((float) x, (float) y, (float) z);

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (tileEntity.getOrientation() == Direction.NORTH) {
			switch (tileEntity.getDirection()) {
				case NORTH:
					GL11.glTranslated(0.5, 3.5, 0.5);
					break;
				case EAST:
					GL11.glRotatef(-90, 0, 180, 0);
					GL11.glTranslated(0.5, 3.5, -0.5);
					break;
				case SOUTH:
					GL11.glRotatef(180, 0, 180, 0);
					GL11.glTranslated(-0.5, 3.5, -0.5);
					break;
				case WEST:
					GL11.glRotatef(90, 0, 180, 0);
					GL11.glTranslated(-0.5, 3.5, 0.5);
					break;
			}
		} else if (tileEntity.getOrientation() == Direction.UP) {
			switch (tileEntity.getDirection()) {
				case NORTH:
					GL11.glRotated(-90, 0, 0, 0);
					GL11.glTranslated(0.5, 2.5, 0.5);
					break;
				case EAST:
					GL11.glRotated(-90, 0, 0, 0);
					GL11.glRotatef(-90, 0, 0, 1);
					GL11.glTranslated(0.5, 3.5, 0.5);
					break;
				case SOUTH:
					GL11.glRotated(-90, 0, 0, 0);
					GL11.glRotatef(180, 0, 0, 1);
					GL11.glTranslated(-0.5, 3.5, 0.5);
					break;
				case WEST:
					GL11.glRotated(-90, 0, 0, 0);
					GL11.glRotatef(90, 0, 0, 1);
					GL11.glTranslated(-0.5, 2.5, 0.5);
					break;
			}
		} else {
			switch (tileEntity.getDirection()) {
				case NORTH:
					GL11.glRotated(90, 0, 0, 0);
					GL11.glRotatef(180, 0, 0, 1);
					GL11.glTranslated(-0.5, 2.5, -0.5);
					break;
				case EAST:
					GL11.glRotated(90, 0, 0, 0);
					GL11.glRotatef(-90, 0, 0, 1);
					GL11.glTranslated(-0.5, 3.5, -0.5);
					break;
				case SOUTH:
					GL11.glRotated(90, 0, 0, 0);
					GL11.glTranslated(0.5, 3.5, -0.5);
					break;
				case WEST:
					GL11.glRotated(90, 0, 0, 0);
					GL11.glRotatef(90, 0, 0, 1);
					GL11.glTranslated(0.5, 2.5, -0.5);
					break;
			}
		}

		GL11.glPushMatrix();

		if (LightmapHelper.isLightmapEnabled()) {
			LightmapHelper.setLightmapCoord(tileEntity.getLightmap());
		}

		renderFrame(tessellator, tileEntity, partialTicks);
		renderSymbolRing(tessellator, tileEntity, partialTicks);
		renderChevrons(tessellator, tileEntity, partialTicks);

		GL11.glPopMatrix();

		GL11.glScaled(1.4, 1.4, 1.4);

		if (LightmapHelper.isLightmapEnabled()) {
			LightmapHelper.setLightmapCoord(tileEntity.getLightmap());
		}


		renderIris(tessellator, tileEntity, partialTicks);

		if (LightmapHelper.isLightmapEnabled()) {
			LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
		}
		switch (tileEntity.getState()) {
			case OPENING:
			case CONNECTED:
			case CLOSING:
				renderEventHorizon(tessellator, tileEntity, partialTicks);

				float exposure = tileEntity.interpolatedEventHorizonExposure(partialTicks);

				if (exposure > 0f) {
					GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);

					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glColor4f(exposure, exposure, exposure, 1f);
					renderEventHorizon(tessellator, tileEntity, partialTicks);
					GL11.glEnable(GL11.GL_TEXTURE_2D);

					GL11.glPopAttrib();
				}

				if (tileEntity.getState() == StargateState.CONNECTED && tileEntity.getOrientation() == Direction.NORTH) {
					GL11.glPushMatrix();
					switch (tileEntity.getDirection()) {
						case NORTH:
							if (z < -0.4) {
								GL11.glTranslated(0, 0, -0.16);
								renderEventHorizon(tessellator, tileEntity, partialTicks);
							}
							break;
						case EAST:
							if (x > -0.6) {
								GL11.glTranslated(0, 0, -0.16);
								renderEventHorizon(tessellator, tileEntity, partialTicks);
							}
							break;
						case SOUTH:
							if (z > -0.6) {
								GL11.glTranslated(0, 0, -0.16);
								renderEventHorizon(tessellator, tileEntity, partialTicks);
							}
							break;
						case WEST:
							if (x < -0.4) {
								GL11.glTranslated(0, 0, -0.16);
								renderEventHorizon(tessellator, tileEntity, partialTicks);
							}
							break;
					}
					GL11.glPopMatrix();
				}
				renderEventHorizonVortex(tessellator, tileEntity, partialTicks);
				break;
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		Lighting.enableLight();
	}

	protected void renderFrame(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
	}

	protected void renderSymbolRing(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
	}

	private void renderChevrons(Tessellator tessellator, TileEntityStargateCore stargateCore, float partialTicks) {
		for (int i = 0; i < 9; i++) {
			renderChevronAtPosition(tessellator, i, stargateCore, partialTicks);
		}
	}

	private void renderChevronAtPosition(Tessellator tessellator, int chevronNumber, TileEntityStargateCore stargateCore, float partialTicks) {
		GL11.glPushMatrix();

		GL11.glRotatef(-(chevronNumber + 1) * (float) 40.0, 0, 0, 1);

		int chevron = chevronNumber;

		if (chevronNumber == 3) {
			chevron = 7;
		}
		if (chevronNumber == 4) {
			chevron = 8;
		}
		if (chevronNumber >= 5) {
			chevron -= 2;
		}

		chevron(tessellator, stargateCore, chevron, partialTicks);
		GL11.glPopMatrix();
	}

	protected void chevron(Tessellator tessellator, TileEntityStargateCore stargateCore, int chevron, float partialTicks) {
	}

	abstract void loadEventHorizonTexture();

	private void renderEventHorizon(Tessellator tessellator, TileEntityStargateCore tileEntity, float partialTicks) {
		if (!tileEntity.interpolatedShowEventHorizon()) {
			return;
		}

		final double time = tileEntity.interpolatedEventHorizonTick(partialTicks) / 3;

		final int frame = (int) (time % 14);

		loadEventHorizonTexture();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);

		double[][] eventHorizonGrid = tileEntity.eventHorizon.getEventHorizonGrid()[0];

		double notShieldedRadius = 2.5;

		float showProgress = tileEntity.interpolatedEventHorizonFormationProgress(partialTicks);

		for (int i = 0; i < eventHorizonGridRadialSize; i++) {
			if (i == 0 && showProgress >= 1) {
				continue;
			}

			tessellator.startDrawing(GL11.GL_QUAD_STRIP);
			tessellator.setNormal(0, 0, 1);
			for (int j = 0; j <= eventHorizonGridPolarSize; j++) {
				eventHorizonVertex(tessellator, eventHorizonGrid, i, j, notShieldedRadius, frame, showProgress);
				eventHorizonVertex(tessellator, eventHorizonGrid, i + 1, j, notShieldedRadius, frame, showProgress);
			}
			tessellator.draw();
		}

		if (showProgress >= 1) {
			tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);

			tessellator.setTextureUV(0.25 + (double) frame / 14, 0.5);
			tessellator.addVertex(0, 0, eventHorizonClip(eventHorizonGrid[1][0], 0, notShieldedRadius));

			for (int j = 0; j <= eventHorizonGridPolarSize; j++) {
				eventHorizonVertex(tessellator, eventHorizonGrid, 1, j, notShieldedRadius, frame, showProgress);
			}

			tessellator.draw();

		}

		GL11.glDepthMask(true);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private void renderEventHorizonVortex(Tessellator tessellator, TileEntityStargateCore tileEntity, float partialTicks) {
		final int split = 100;

		final double rawDistance = tileEntity.interpolatedUnstableVortexDistance(partialTicks);

		if (rawDistance == 0) {
			return;
		}

		final double depth = eventHorizonVortexMaxDepth;

		final double time = tileEntity.interpolatedAnimationTick(partialTicks) / 4;

		final int frame = (int) (time * 4 % 14);

		final double diameter = tileEntity.interpolatedUnstableVortexDiameter(partialTicks);

		final double distance = depth - rawDistance * depth + StargateEventHorizon.sinWave(time / 4);

		final int vortexSide = 30;

		loadEventHorizonTexture();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_CULL_FACE);

		GL11.glColor4f(1, 1, 1, 1f);

		double[][] eventHorizonGrid = tileEntity.eventHorizon.getEventHorizonGrid()[0];

		GL11.glRotatef(90, 0, 0, 1);

		double notShieldedRadius = 2.5;
		tessellator.startDrawing(GL11.GL_QUAD_STRIP);

		tessellator.setNormal(0, 0, 1);

		for (int i = 0; i < split; i++) {
			for (int j = 0; j <= vortexSide; j++) {
				double maxClip = eventHorizonGrid[(int) (
					(float) i / split * TileEntityRenderStargateCore.eventHorizonGridPolarSize
				)][5];

				eventHorizonVortexVertex(
					tessellator,
					StargateEventHorizon.getEventHorizonVortexShape(
						(double) (i + 1) / split,
						diameter,
						(double) j / vortexSide,
						time
					),
					j * numRingSegments / vortexSide,
					Math.max((double) (i + 1) / split * depth - distance, maxClip),
					notShieldedRadius,
					frame
				);
				eventHorizonVortexVertex(
					tessellator,
					StargateEventHorizon.getEventHorizonVortexShape(
						(double) i / split,
						diameter,
						(double) j / vortexSide,
						time
					),
					j * numRingSegments / vortexSide,
					Math.max((double) i / split * depth - distance, maxClip),
					notShieldedRadius,
					frame
				);
			}
		}
		tessellator.draw();

		tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);

		tessellator.setTextureUV(0.25 + (double) frame / 14, 0.5);
		tessellator.addVertex(0, 0, eventHorizonClip(Math.max(depth - distance, 0), 0, notShieldedRadius));

		for (int j = 0; j <= vortexSide; j++) {
			eventHorizonVortexVertex(
				tessellator,
				StargateEventHorizon.getEventHorizonVortexShape(
					1,
					diameter,
					(double) j / vortexSide,
					time
				),
				j * numRingSegments / vortexSide,
				Math.max(depth - distance, 0),
				notShieldedRadius,
				frame
			);
		}

		tessellator.draw();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private void eventHorizonVortexVertex(Tessellator tessellator, double i, int j, double h, double notShieldedRadius, int frame) {
		double r = i * eventHorizonVortexBaseWidth;
		double x = r * ringCosValues[j];
		double y = r * ringSinValues[j];
		double z = eventHorizonClip(h, r, notShieldedRadius);
		tessellator.setTextureUV(x / 56 + 0.25 + (double) frame / 14, y / 4 + 0.5);
		tessellator.addVertex(x, y, z);
	}


	private void eventHorizonVertex(Tessellator tessellator, double[][] grid, int i, int j, double notShieldedRadius, int frame, double showProgress) {
		double r = i * eventHorizonBandWidth;
		double R = eventHorizonGridRadialSize * eventHorizonBandWidth;
		double min = R * (1.0 - showProgress);

		if (r + eventHorizonBandWidth < min) {
			return;
		}

		double clipped = Math.max(r, min);
		double x = clipped * ringCosValues[j];
		double y = clipped * ringSinValues[j];
		double z = eventHorizonClip(grid[j + 1][i], clipped, notShieldedRadius);
		tessellator.setTextureUV(x / 56 + 0.25 + (double) frame / 14, y / 4 + 0.5);
		tessellator.addVertex(x, y, z);
	}

	private double eventHorizonClip(double z, double r, double radius) {
		if (r >= radius)
			z = Math.min(z, 0);
		return z;
	}

	private void renderIris(Tessellator tessellator, TileEntityStargateCore tileEntity, float partialTicks) {
	}
}
