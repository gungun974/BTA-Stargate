package gungun974.stargate.gate.renders;

import gungun974.stargate.core.CustomLighting;
import gungun974.stargate.core.StargateState;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.renderer.*;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.LightIndexHelper;

public abstract class TileEntityRenderStargate extends TileEntityRenderer<TileEntityStargate> {
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

	@Override
	public void doRender(TessellatorGeneral tessellator, TileEntityStargate tileEntity, double x, double y, double z, float partialTicks) {
		StargateComponent gate = tileEntity.getStargateComponent();

		if (gate == null) {
			return;
		}

		CustomLighting.enableLight();

		GLRenderer.pushFrame();

		GLRenderer.modelM4f().translate((float) x, (float) y, (float) z);

		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (gate.getOrientation() == Direction.NORTH) {
			switch (gate.getDirection()) {
				case NORTH:
					GLRenderer.modelM4f().translate(0.5f, 3.5f, 0.5f);
					break;
				case EAST:
					GLRenderer.modelM4f().rotateY((float) Math.toRadians(-90));
					GLRenderer.modelM4f().translate(0.5f, 3.5f, -0.5f);
					break;
				case SOUTH:
					GLRenderer.modelM4f().rotateY((float) Math.toRadians(180));
					GLRenderer.modelM4f().translate(-0.5f, 3.5f, -0.5f);
					break;
				case WEST:
					GLRenderer.modelM4f().rotateY((float) Math.toRadians(90));
					GLRenderer.modelM4f().translate(-0.5f, 3.5f, 0.5f);
					break;
			}
		} else if (gate.getOrientation() == Direction.UP) {
			switch (gate.getDirection()) {
				case NORTH:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(-90));
					GLRenderer.modelM4f().translate(0.5f, 2.5f, 0.5f);
					break;
				case EAST:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(-90));
					GLRenderer.modelM4f().rotateZ((float) Math.toRadians(-90));
					GLRenderer.modelM4f().translate(0.5f, 3.5f, 0.5f);
					break;
				case SOUTH:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(-90));
					GLRenderer.modelM4f().rotateZ((float) Math.toRadians(180));
					GLRenderer.modelM4f().translate(-0.5f, 3.5f, 0.5f);
					break;
				case WEST:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(-90));
					GLRenderer.modelM4f().rotateZ((float) Math.toRadians(90));
					GLRenderer.modelM4f().translate(-0.5f, 2.5f, 0.5f);
					break;
			}
		} else {
			switch (gate.getDirection()) {
				case NORTH:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(90));
					GLRenderer.modelM4f().rotateZ((float) Math.toRadians(180));
					GLRenderer.modelM4f().translate(-0.5f, 2.5f, -0.5f);
					break;
				case EAST:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(90));
					GLRenderer.modelM4f().rotateZ((float) Math.toRadians(-90));
					GLRenderer.modelM4f().translate(-0.5f, 3.5f, -0.5f);
					break;
				case SOUTH:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(90));
					GLRenderer.modelM4f().translate(0.5f, 3.5f, -0.5f);
					break;
				case WEST:
					GLRenderer.modelM4f().rotateX((float) Math.toRadians(90));
					GLRenderer.modelM4f().rotateZ((float) Math.toRadians(90));
					GLRenderer.modelM4f().translate(0.5f, 2.5f, -0.5f);
					break;
			}
		}

		GLRenderer.pushFrame();

		GLRenderer.setLightmapCoord1i(gate.getLightmap());

		renderFrame(tessellator, gate, partialTicks);
		renderSymbolRing(tessellator, gate, partialTicks);
		renderChevrons(tessellator, gate, partialTicks);

		GLRenderer.popFrame();

		GLRenderer.modelM4f().scale(1.4f, 1.4f, 1.4f);

		GLRenderer.setLightmapCoord1i(gate.getLightmap());

		renderIris(tessellator, gate, partialTicks);

		GLRenderer.setLightmapCoord1i(LightIndexHelper.lightIndex2i(15, 15));

		switch (gate.getState()) {
			case OPENING:
			case CONNECTED:
			case CLOSING:
				renderEventHorizon(tessellator, gate, partialTicks);

				float exposure = gate.interpolatedEventHorizonExposure(partialTicks);

				if (exposure > 0f) {
					GLRenderer.pushFrame();

					GLRenderer.enableState(State.BLEND);
					GLRenderer.setBlendFunc(BlendFactor.ONE, BlendFactor.ONE);

					GLRenderer.setColor4f(exposure, exposure, exposure, 1f);
					renderEventHorizon(tessellator, gate, partialTicks, false);

					GLRenderer.popFrame();
				}

				if (gate.getState() == StargateState.CONNECTED && gate.getOrientation() == Direction.NORTH) {
					GLRenderer.pushFrame();
					switch (gate.getDirection()) {
						case NORTH:
							if (z < -0.4) {
								GLRenderer.modelM4f().translate(0f, 0f, -0.16f);
								renderEventHorizon(tessellator, gate, partialTicks);
							}
							break;
						case EAST:
							if (x > -0.6) {
								GLRenderer.modelM4f().translate(0f, 0f, -0.16f);
								renderEventHorizon(tessellator, gate, partialTicks);
							}
							break;
						case SOUTH:
							if (z > -0.6) {
								GLRenderer.modelM4f().translate(0f, 0f, -0.16f);
								renderEventHorizon(tessellator, gate, partialTicks);
							}
							break;
						case WEST:
							if (x < -0.4) {
								GLRenderer.modelM4f().translate(0f, 0f, -0.16f);
								renderEventHorizon(tessellator, gate, partialTicks);
							}
							break;
					}
					GLRenderer.popFrame();
				}
				renderEventHorizonVortex(tessellator, gate, partialTicks);
				break;
		}

		GLRenderer.popFrame();
		Lighting.enableLight();
	}

	protected void renderFrame(TessellatorGeneral tessellator, StargateComponent stargateCore, float partialTicks) {
	}

	protected void renderSymbolRing(TessellatorGeneral tessellator, StargateComponent stargateCore, float partialTicks) {
	}

	private void renderChevrons(TessellatorGeneral tessellator, StargateComponent stargateCore, float partialTicks) {
		for (int i = 0; i < 9; i++) {
			renderChevronAtPosition(tessellator, i, stargateCore, partialTicks);
		}
	}

	private void renderChevronAtPosition(TessellatorGeneral tessellator, int chevronNumber, StargateComponent stargateCore, float partialTicks) {
		GLRenderer.pushFrame();

		GLRenderer.modelM4f().rotateZ((float) Math.toRadians(-(chevronNumber + 1) * 40.0));

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
		GLRenderer.popFrame();
	}

	protected void chevron(TessellatorGeneral tessellator, StargateComponent stargateCore, int chevron, float partialTicks) {
	}

	abstract void loadEventHorizonTexture();

	private void renderEventHorizon(TessellatorGeneral tessellator, StargateComponent tileEntity, float partialTicks) {
		renderEventHorizon(tessellator, tileEntity, partialTicks, true);
	}

	private void renderEventHorizon(TessellatorGeneral tessellator, StargateComponent tileEntity, float partialTicks, boolean withTexture) {
		if (!tileEntity.interpolatedShowEventHorizon()) {
			return;
		}

		final double time = tileEntity.interpolatedEventHorizonTick(partialTicks) / 3;

		final int frame = (int) (time % 14);

		if (withTexture) {
			loadEventHorizonTexture();
		} else {
			GLRenderer.setShader(Shaders.COLOR);
		}

		GLRenderer.globalSetLightEnabled(false);
		GLRenderer.disableState(State.CULL_FACE);

		double[][] eventHorizonGrid = tileEntity.eventHorizon.getEventHorizonGrid()[0];

		double notShieldedRadius = 2.5;

		float showProgress = tileEntity.interpolatedEventHorizonFormationProgress(partialTicks);

		for (int i = 0; i < eventHorizonGridRadialSize; i++) {
			if (i == 0 && showProgress >= 1) {
				continue;
			}

			tessellator.startDrawing(DrawMode.TRIANGLE_STRIP);
			tessellator.setNormal(0, 0, 1);
			for (int j = 0; j <= eventHorizonGridPolarSize; j++) {
				eventHorizonVertex(tessellator, eventHorizonGrid, i, j, notShieldedRadius, frame, showProgress);
				eventHorizonVertex(tessellator, eventHorizonGrid, i + 1, j, notShieldedRadius, frame, showProgress);
			}
			tessellator.draw();
		}

		if (showProgress >= 1) {
			tessellator.startDrawing(DrawMode.TRIANGLE_FAN);
			tessellator.setTextureUV(0.25 + (double) frame / 14, 0.5);
			tessellator.addVertex(0, 0, eventHorizonClip(eventHorizonGrid[1][0], 0, notShieldedRadius));

			for (int j = 0; j <= eventHorizonGridPolarSize; j++) {
				eventHorizonVertex(tessellator, eventHorizonGrid, 1, j, notShieldedRadius, frame, showProgress);
			}

			tessellator.draw();

		}

		GLRenderer.setDepthMask(true);

		GLRenderer.globalSetLightEnabled(true);
		GLRenderer.enableState(State.CULL_FACE);
	}

	private void renderEventHorizonVortex(TessellatorGeneral tessellator, StargateComponent tileEntity, float partialTicks) {
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

		GLRenderer.globalSetLightEnabled(false);
		GLRenderer.enableState(State.CULL_FACE);

		GLRenderer.setColor4f(1, 1, 1, 1f);

		double[][] eventHorizonGrid = tileEntity.eventHorizon.getEventHorizonGrid()[0];

		GLRenderer.modelM4f().rotate((float) Math.toRadians(90), 0, 0, 1);

		double notShieldedRadius = 2.5;
		tessellator.startDrawing(DrawMode.TRIANGLE_STRIP);

		tessellator.setNormal(0, 0, 1);

		for (int i = 0; i < split; i++) {
			for (int j = 0; j <= vortexSide; j++) {
				double maxClip = eventHorizonGrid[(int) (
					(float) i / split * TileEntityRenderStargate.eventHorizonGridPolarSize
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

		tessellator.startDrawing(DrawMode.TRIANGLE_FAN);

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

		GLRenderer.setDepthMask(true);
		GLRenderer.globalSetLightEnabled(true);
	}

	private void eventHorizonVortexVertex(TessellatorGeneral tessellator, double i, int j, double h, double notShieldedRadius, int frame) {
		double r = i * eventHorizonVortexBaseWidth;
		double x = r * ringCosValues[j];
		double y = r * ringSinValues[j];
		double z = eventHorizonClip(h, r, notShieldedRadius);
		tessellator.setTextureUV(x / 56 + 0.25 + (double) frame / 14, y / 4 + 0.5);
		tessellator.addVertex(x, y, z);
	}


	private void eventHorizonVertex(TessellatorGeneral tessellator, double[][] grid, int i, int j, double notShieldedRadius, int frame, double showProgress) {
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

	private void renderIris(TessellatorGeneral tessellator, StargateComponent tileEntity, float partialTicks) {
	}
}
