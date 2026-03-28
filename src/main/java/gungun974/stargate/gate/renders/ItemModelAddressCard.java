package gungun974.stargate.gate.renders;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.items.ItemAddressCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.PlayerSkinParser;
import net.minecraft.client.render.entity.MobRendererPlayer;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import org.lwjgl.opengl.GL11;

public class ItemModelAddressCard extends ItemModelStandard {
	public ItemModelAddressCard(Item item, String namespace) {
		super(item, namespace);
	}

	private static void drawSymbol39(Tessellator tessellator, int keyId, double x, double y, double size) {
		float uvWidth = 1 / 6f;
		float uvHeight = 1 / 7f;

		int j = keyId % 6;
		int k = keyId / 6;

		float u0 = j * uvWidth;
		float u1 = j * uvWidth + uvWidth;

		float v0 = k * uvHeight;
		float v1 = k * uvHeight + uvHeight;

		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y, 0.0F, u0, v0);
		tessellator.addVertexWithUV(x, y + size, 0.0F, u0, v1);
		tessellator.addVertexWithUV(x + size, y + size, 0.0F, u1, v1);
		tessellator.addVertexWithUV(x + size, y, 0.0F, u1, v0);
		tessellator.draw();
	}

	private static void drawSymbol36(Tessellator tessellator, int keyId, double x, double y, double size) {
		float uvWidth = 1 / 6f;
		float uvHeight = 1 / 6f;

		int j = keyId % 6;
		int k = keyId / 6;

		float u0 = j * uvWidth;
		float u1 = j * uvWidth + uvWidth;

		float v0 = k * uvHeight;
		float v1 = k * uvHeight + uvHeight;

		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y, 0.0F, u0, v0);
		tessellator.addVertexWithUV(x, y + size, 0.0F, u0, v1);
		tessellator.addVertexWithUV(x + size, y + size, 0.0F, u1, v1);
		tessellator.addVertexWithUV(x + size, y, 0.0F, u1, v0);
		tessellator.draw();
	}

	public void renderItemFirstPerson(Tessellator tessellator, ItemRenderer renderer, Player player, ItemStack stack, float partialTick) {
		int blockX = MathHelper.floor(player.x);
		int blockY = MathHelper.floor(player.y);
		int blockZ = MathHelper.floor(player.z);
		float brightness = 1.0F;

		Minecraft mc = Minecraft.getMinecraft();

		if (LightmapHelper.isLightmapEnabled()) {
			LightmapHelper.setLightmapCoord(mc.currentWorld.getLightmapCoord(blockX, blockY, blockZ, 0));
		} else if (!mc.fullbright) {
			brightness = mc.currentWorld.getLightBrightness(blockX, blockY, blockZ);
		}

		GL11.glColor4f(brightness, brightness, brightness, 1.0F);
		float rotationPitch = player.xRotO + (player.xRot - player.xRotO) * partialTick;
		float swingProgress = player.getSwingProgress(partialTick);
		float f_val = MathHelper.clamp(1.0F - rotationPitch / 48.0F + 0.1F, 0.0F, 1.0F);
		f_val = -MathHelper.cos(f_val * (float) Math.PI) * 0.5F + 0.5F;

		GL11.glTranslatef(0.0F, -(1.0F - renderer.getEquippedProgress(partialTick)) * 1.2F - f_val * 0.5F + 0.04F, -0.71999997F);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(f_val * -85.0F, 0.0F, 0.0F, 1.0F);
		GL11.glEnable(32826);

		mc.textureManager.bindDownloadableTexture(mc.thePlayer.skinURL, mc.thePlayer.getEntityTexture(), PlayerSkinParser.instance);
		MobRendererPlayer playerRenderer = (MobRendererPlayer) (Object) EntityRenderDispatcher.instance.getRenderer(mc.thePlayer);

		GL11.glPushMatrix();
		GL11.glTranslatef(-0.0F, -0.6F, -1.1F);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(31.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(65.0F, 0.0F, 1.0F, 0.0F);
		playerRenderer.drawFirstPersonHand(mc.thePlayer, true);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		float f_z = MathHelper.sin(swingProgress * (float) Math.PI);
		float f_x = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
		GL11.glTranslatef(f_x * 0.4F, -MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, -f_z * 0.2F);
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.0F, -0.6F, 1.1F);
		GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-31.0F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(-65.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0, MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, 0);
		playerRenderer.drawFirstPersonHand(mc.thePlayer, false);
		GL11.glPopMatrix();
		GL11.glPopMatrix();

		GL11.glScalef(0.38F, 0.38F, 0.38F);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-1.0F, -1.0F, 0.0F);

		GL11.glScalef(0.015625F, 0.015625F, 0.015625F);
		GL11.glNormal3f(0.0F, 0.0F, -1.0F);
		mc.textureManager.bindTexture(mc.textureManager.loadTexture("/assets/stargate/textures/address_card_bg.png"));
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0F, 135.0F, 0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(135.0F, 135.0F, 0.0F, 1.0F, 1.0F);
		tessellator.addVertexWithUV(135.0F, 79F, 0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(-7.0F, 79F, 0.0F, 0.0F, 0.0F);
		tessellator.draw();


		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, -1.0F);

		if (ItemAddressCard.hasAddress(stack, StargateFamily.MilkyWay)) {
			mc.textureManager.bindTexture(mc.textureManager.loadTexture("/assets/stargate/textures/font/milkyway_glyphs.png"));

			for (int i = 0; i < 9; i++) {
				drawSymbol39(tessellator, ItemAddressCard.getSymbol(stack, StargateFamily.MilkyWay, i), i * 14, 86, 14);
			}
		} else {
			GL11.glTranslatef(0, -7.0F, 0);
		}

		if (ItemAddressCard.hasAddress(stack, StargateFamily.Pegasus)) {
			mc.textureManager.bindTexture(mc.textureManager.loadTexture("/assets/stargate/textures/font/pegasus_glyphs.png"));

			for (int i = 0; i < 9; i++) {
				drawSymbol36(tessellator, ItemAddressCard.getSymbol(stack, StargateFamily.Pegasus, i), i * 14, 100, 14);
			}
		} else {
			GL11.glTranslatef(0, -7.0F, 0);
		}

		if (ItemAddressCard.hasAddress(stack, StargateFamily.Universe)) {
			mc.textureManager.bindTexture(mc.textureManager.loadTexture("/assets/stargate/textures/font/universe_glyphs.png"));

			for (int i = 0; i < 9; i++) {
				drawSymbol36(tessellator, ItemAddressCard.getSymbol(stack, StargateFamily.Universe, i), i * 14, 114, 14);
			}
		}

		GL11.glPopMatrix();

	}
}
