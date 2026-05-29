package gungun974.stargate.dhd.renders;

import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.item.Item;
import org.jetbrains.annotations.NotNull;

public class ItemModelDHD extends ItemModelStandard {
	public ItemModelDHD(Item item, boolean defaultTextureLookup) {
		super(item, defaultTextureLookup);
	}

	@Override
	protected void renderCoordinate(@NotNull TessellatorGeneral tessellator, @NotNull IconCoordinate coordinate, byte lightIndex, int color, boolean items3d, boolean mirrorX) {
		if (!items3d) {
			super.renderCoordinate(tessellator, coordinate, lightIndex, color, false, mirrorX);
			return;
		}

		double cUMin = mirrorX ? coordinate.getIconUMax() : coordinate.getIconUMin();
		double cUMax = mirrorX ? coordinate.getIconUMin() : coordinate.getIconUMax();
		double cVMin = coordinate.getIconVMin();
		double cVMax = coordinate.getIconVMax();
		coordinate.parentAtlas.bind();

		int pxH = coordinate.height;
		int pxW = coordinate.width;
		int darkColor = (color & 0xFF000000) | 0x383838;

		tessellator.startDrawingQuads();
		tessellator.setLightmapCoord1i(lightIndex);
		tessellator.setColor1i(darkColor);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(-0.5F, 0.5F, -0.03125F, cUMin, cVMin);
		tessellator.addVertexWithUV(0.5F, 0.5F, -0.03125F, cUMax, cVMin);
		tessellator.addVertexWithUV(0.5F, -0.5F, -0.03125F, cUMax, cVMax);
		tessellator.addVertexWithUV(-0.5F, -0.5F, -0.03125F, cUMin, cVMax);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setLightmapCoord1i(lightIndex);
		tessellator.setColor1i(color);

		for (int h = 0; h < pxH; ++h) {
			double y1 = (double) h / (double) pxH;
			double y2 = (double) (h + 1) / (double) pxH;
			double vMin = coordinate.getSubIconV(y1);
			double vMax = coordinate.getSubIconV(y2);
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			tessellator.addVertexWithUV(-0.5F, (double) 0.5F - y1, -0.03125F, cUMin, vMin);
			tessellator.addVertexWithUV(-0.5F, (double) 0.5F - y1, 0.03125F, cUMin, vMax);
			tessellator.addVertexWithUV(0.5F, (double) 0.5F - y1, 0.03125F, cUMax, vMax);
			tessellator.addVertexWithUV(0.5F, (double) 0.5F - y1, -0.03125F, cUMax, vMin);
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			tessellator.addVertexWithUV(-0.5F, (double) 0.5F - y2, -0.03125F, cUMin, vMin);
			tessellator.addVertexWithUV(0.5F, (double) 0.5F - y2, -0.03125F, cUMax, vMin);
			tessellator.addVertexWithUV(0.5F, (double) 0.5F - y2, 0.03125F, cUMax, vMax);
			tessellator.addVertexWithUV(-0.5F, (double) 0.5F - y2, 0.03125F, cUMin, vMax);
		}

		for (int w = 0; w < pxW; ++w) {
			double x1 = (double) w / (double) pxW;
			double x2 = (double) (w + 1) / (double) pxW;
			double uMin = coordinate.getSubIconU(mirrorX ? (double) 1.0F - x1 : x1);
			double uMax = coordinate.getSubIconU(mirrorX ? (double) 1.0F - x2 : x2);
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(x1 - (double) 0.5F, 0.5F, -0.03125F, uMin, cVMin);
			tessellator.addVertexWithUV(x1 - (double) 0.5F, -0.5F, -0.03125F, uMax, cVMax);
			tessellator.addVertexWithUV(x1 - (double) 0.5F, -0.5F, 0.03125F, uMax, cVMax);
			tessellator.addVertexWithUV(x1 - (double) 0.5F, 0.5F, 0.03125F, uMin, cVMin);
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(x2 - (double) 0.5F, 0.5F, -0.03125F, uMin, cVMin);
			tessellator.addVertexWithUV(x2 - (double) 0.5F, 0.5F, 0.03125F, uMin, cVMin);
			tessellator.addVertexWithUV(x2 - (double) 0.5F, -0.5F, 0.03125F, uMax, cVMax);
			tessellator.addVertexWithUV(x2 - (double) 0.5F, -0.5F, -0.03125F, uMax, cVMax);
		}

		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(-0.5F, 0.5F, 0.03125F, cUMin, cVMin);
		tessellator.addVertexWithUV(-0.5F, -0.5F, 0.03125F, cUMin, cVMax);
		tessellator.addVertexWithUV(0.5F, -0.5F, 0.03125F, cUMax, cVMax);
		tessellator.addVertexWithUV(0.5F, 0.5F, 0.03125F, cUMax, cVMin);
		tessellator.draw();
	}
}
