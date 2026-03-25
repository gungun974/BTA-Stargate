package gungun974.stargate.dhd.renders;

import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public class ItemModelDHD extends ItemModelStandard {
	public ItemModelDHD(Item item, String namespace) {
		super(item, namespace);
	}

	@Override
	public void renderItemInWorld(Tessellator tessellator, Entity entity, ItemStack itemStack, float brightness, float alpha, boolean worldTransform) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if (this.useColor) {
			int color = this.getColor(itemStack);
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;
			GL11.glColor4f(r * brightness, g * brightness, b * brightness, alpha);
		} else {
			GL11.glColor4f(brightness, brightness, brightness, alpha);
		}

		IconCoordinate tex = this.getIcon(entity, itemStack);
		tex.parentAtlas.bind();
		int tileWidth = tex.width;
		float uMin = (float) tex.getIconUMin();
		float uMax = (float) tex.getIconUMax();
		float vMin = (float) tex.getIconVMin();
		float vMax = (float) tex.getIconVMax();
		float uDiff = uMin - uMax;
		float vDiff = vMin - vMax;
		float width = 1.0F;
		float foon = 0.5F / (float) tex.parentAtlas.getHeight();
		float goon = 0.0625F * (16.0F / (float) tileWidth);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float thickness = 0.0625F;
		float pixelWidth = 1.0F / (float) tileWidth;
		if (worldTransform) {
			GL11.glTranslatef(-0.5F, -0.5F, 0.03125F);
		}

		GL11.glTexEnvi(GL13.GL_TEXTURE_ENV, GL13.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
		GL11.glTexEnvi(GL13.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_PRIMARY_COLOR);
		GL11.glTexEnvi(GL13.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE, GL13.GL_PRIMARY_COLOR);
		GL11.glTexEnvi(GL13.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL13.GL_PRIMARY_COLOR);
		GL11.glTexEnvi(GL13.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_TEXTURE);
		GL11.glColor4f(0.22f, 0.22f, 0.22f, 1f);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV((double) 0.0F, (double) 0.0F, (double) 0.0F, (double) uMax, (double) vMax);
		tessellator.addVertexWithUV((double) 1.0F, (double) 0.0F, (double) 0.0F, (double) uMin, (double) vMax);
		tessellator.addVertexWithUV((double) 1.0F, (double) 1.0F, (double) 0.0F, (double) uMin, (double) vMin);
		tessellator.addVertexWithUV((double) 0.0F, (double) 1.0F, (double) 0.0F, (double) uMax, (double) vMin);
		tessellator.draw();
		GL11.glTexEnvi(GL13.GL_TEXTURE_ENV, GL13.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		GL11.glColor4f(1, 1, 1, 1f);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV((double) 0.0F, (double) 1.0F, (double) -0.0625F, (double) uMax, (double) vMin);
		tessellator.addVertexWithUV((double) 1.0F, (double) 1.0F, (double) -0.0625F, (double) uMin, (double) vMin);
		tessellator.addVertexWithUV((double) 1.0F, (double) 0.0F, (double) -0.0625F, (double) uMin, (double) vMax);
		tessellator.addVertexWithUV((double) 0.0F, (double) 0.0F, (double) -0.0625F, (double) uMax, (double) vMax);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = (float) i * pixelWidth;
			float u = uMax + uDiff * texProgress - foon;
			float x = 1.0F * texProgress;
			tessellator.addVertexWithUV((double) x, (double) 0.0F, (double) -0.0625F, (double) u, (double) vMax);
			tessellator.addVertexWithUV((double) x, (double) 0.0F, (double) 0.0F, (double) u, (double) vMax);
			tessellator.addVertexWithUV((double) x, (double) 1.0F, (double) 0.0F, (double) u, (double) vMin);
			tessellator.addVertexWithUV((double) x, (double) 1.0F, (double) -0.0625F, (double) u, (double) vMin);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = (float) i * pixelWidth;
			float u = uMax + uDiff * texProgress - foon;
			float x = 1.0F * texProgress + goon;
			tessellator.addVertexWithUV((double) x, (double) 1.0F, (double) -0.0625F, (double) u, (double) vMin);
			tessellator.addVertexWithUV((double) x, (double) 1.0F, (double) 0.0F, (double) u, (double) vMin);
			tessellator.addVertexWithUV((double) x, (double) 0.0F, (double) 0.0F, (double) u, (double) vMax);
			tessellator.addVertexWithUV((double) x, (double) 0.0F, (double) -0.0625F, (double) u, (double) vMax);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = (float) i * pixelWidth;
			float v = vMax + vDiff * texProgress - foon;
			float y = 1.0F * texProgress + goon;
			tessellator.addVertexWithUV((double) 0.0F, (double) y, (double) 0.0F, (double) uMax, (double) v);
			tessellator.addVertexWithUV((double) 1.0F, (double) y, (double) 0.0F, (double) uMin, (double) v);
			tessellator.addVertexWithUV((double) 1.0F, (double) y, (double) -0.0625F, (double) uMin, (double) v);
			tessellator.addVertexWithUV((double) 0.0F, (double) y, (double) -0.0625F, (double) uMax, (double) v);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = (float) i * pixelWidth;
			float v = vMax + vDiff * texProgress - foon;
			float y = 1.0F * texProgress;
			tessellator.addVertexWithUV((double) 1.0F, (double) y, (double) 0.0F, (double) uMin, (double) v);
			tessellator.addVertexWithUV((double) 0.0F, (double) y, (double) 0.0F, (double) uMax, (double) v);
			tessellator.addVertexWithUV((double) 0.0F, (double) y, (double) -0.0625F, (double) uMax, (double) v);
			tessellator.addVertexWithUV((double) 1.0F, (double) y, (double) -0.0625F, (double) uMin, (double) v);
		}

		tessellator.draw();

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
