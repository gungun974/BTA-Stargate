package gungun974.stargate.gate.renders;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.items.ItemAddressCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.PlayerSkinParser;
import net.minecraft.client.render.entity.MobRendererPlayer;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;

public class ItemModelAddressCard extends ItemModelStandard {
	private final Minecraft mc = Minecraft.getMinecraft();

	public ItemModelAddressCard(Item item, boolean defaultTextureLookup) {
		super(item, defaultTextureLookup);
	}

	private static void drawSymbol39(TessellatorGeneral tessellator, int keyId, double x, double y, double size) {
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

	private static void drawSymbol36(TessellatorGeneral tessellator, int keyId, double x, double y, double size) {
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

	@Override
	public void renderFirstPerson(@NotNull TessellatorGeneral tessellator, @NotNull ItemRenderer itemRenderer, @NotNull Player player, @NotNull ItemStack itemStack, byte lightIndex, float partialTick) {
		GLRenderer.pushFrame();
		float rotationPitch = player.xRotO + (player.xRot - player.xRotO) * partialTick;
		float swingProgress = player.getSwingProgress(partialTick);
		float f_z = MathHelper.sin(swingProgress * (float) Math.PI);
		float f_x = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
		float f_val = MathHelper.clamp(1.0F - rotationPitch / 48.0F + 0.1F, 0.0F, 1.0F);
		f_val = -MathHelper.cos(f_val * (float) Math.PI) * 0.5F + 0.5F;

		GLRenderer.modelM4f().translate(0.0F, -(1.0F - itemRenderer.getEquippedProgress(partialTick)) * 1.2F - f_val * 0.5F + 0.04F, -0.71999997F);
		GLRenderer.modelM4f().rotateY(((float) Math.PI / 2F));
		GLRenderer.modelM4f().rotateZ(org.joml.Math.toRadians(f_val * -85.0F));

		this.mc.textureManager.bindDownloadableTexture(this.mc.thePlayer.skinURL, this.mc.thePlayer.getEntityTexture(), this.mc.thePlayer.slimModel ? PlayerSkinParser.instanceAlex : PlayerSkinParser.instanceSteve);
		MobRendererPlayer playerRenderer = (MobRendererPlayer) (Object) EntityRendererDispatcher.instance.getRenderer(this.mc.thePlayer);

		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate(-0.0F, -0.6F, -1.1F);
		GLRenderer.modelM4f().rotateY((float) Math.PI);
		GLRenderer.modelM4f().rotateX(-(float) Math.PI / 4F);
		GLRenderer.modelM4f().rotateZ(org.joml.Math.toRadians(31.0F));
		GLRenderer.modelM4f().rotateY(org.joml.Math.toRadians(65.0F));
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().scale(0.0625F, 0.0625F, -0.0625F);
		GLRenderer.modelM4f().translate(0.0F, 16.0F, 0.0F);
		GLRenderer.modelM4f().rotateX((float) Math.PI);
		GLRenderer.modelM4f().translate(0.0F, -16.0F, 0.0F);
		GLRenderer.modelM4f().translate(0.0F, 8.0F, 0.0F);
		playerRenderer.drawFirstPersonHand(GLRenderer.getTessellator(), this.mc.thePlayer, true);
		GLRenderer.popFrame();
		GLRenderer.popFrame();

		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate(f_x * 0.4F, -MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, -f_z * 0.2F);
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate(-0.0F, -0.6F, 1.1F);
		GLRenderer.modelM4f().rotateX(-(float) Math.PI / 4F);
		GLRenderer.modelM4f().rotateZ(org.joml.Math.toRadians(-31.0F));
		GLRenderer.modelM4f().rotateY(org.joml.Math.toRadians(-65.0F));
		GLRenderer.modelM4f().translate(0.0F, MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, 0.0F);
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().scale(0.0625F, 0.0625F, -0.0625F);
		GLRenderer.modelM4f().translate(0.0F, 16.0F, 0.0F);
		GLRenderer.modelM4f().rotateX((float) Math.PI);
		GLRenderer.modelM4f().translate(0.0F, -16.0F, 0.0F);
		GLRenderer.modelM4f().translate(0.0F, 8.0F, 0.0F);
		playerRenderer.drawFirstPersonHand(GLRenderer.getTessellator(), this.mc.thePlayer, false);
		GLRenderer.popFrame();
		GLRenderer.popFrame();
		GLRenderer.popFrame();

		GLRenderer.modelM4f().scale(0.38F, 0.38F, 0.38F);
		GLRenderer.modelM4f().rotateY(((float) Math.PI / 2F));
		GLRenderer.modelM4f().rotateZ((float) Math.PI);
		GLRenderer.modelM4f().translate(-1.0F, -1.0F, 0.0F);
		GLRenderer.modelM4f().scale(0.015625F, 0.015625F, 0.015625F);

		this.mc.textureManager.bindTexture(this.mc.textureManager.loadTexture("/assets/stargate/textures/address_card_bg.png"));
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0F, 135.0F, 0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(135.0F, 135.0F, 0.0F, 1.0F, 1.0F);
		tessellator.addVertexWithUV(135.0F, 79.0F, 0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(-7.0F, 79.0F, 0.0F, 0.0F, 0.0F);
		tessellator.draw();

		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate(0.0F, 0.0F, -1.0F);

		if (ItemAddressCard.hasAddress(itemStack, StargateFamily.MilkyWay)) {
			this.mc.textureManager.bindTexture(this.mc.textureManager.loadTexture("/assets/stargate/textures/font/milkyway_glyphs.png"));
			for (int i = 0; i < 9; i++) {
				drawSymbol39(tessellator, ItemAddressCard.getSymbol(itemStack, StargateFamily.MilkyWay, i), i * 14, 86, 14);
			}
		} else {
			GLRenderer.modelM4f().translate(0.0F, -7.0F, 0.0F);
		}

		if (ItemAddressCard.hasAddress(itemStack, StargateFamily.Pegasus)) {
			this.mc.textureManager.bindTexture(this.mc.textureManager.loadTexture("/assets/stargate/textures/font/pegasus_glyphs.png"));
			for (int i = 0; i < 9; i++) {
				drawSymbol36(tessellator, ItemAddressCard.getSymbol(itemStack, StargateFamily.Pegasus, i), i * 14, 100, 14);
			}
		} else {
			GLRenderer.modelM4f().translate(0.0F, -7.0F, 0.0F);
		}

		if (ItemAddressCard.hasAddress(itemStack, StargateFamily.Universe)) {
			this.mc.textureManager.bindTexture(this.mc.textureManager.loadTexture("/assets/stargate/textures/font/universe_glyphs.png"));
			for (int i = 0; i < 9; i++) {
				drawSymbol36(tessellator, ItemAddressCard.getSymbol(itemStack, StargateFamily.Universe, i), i * 14, 114, 14);
			}
		}

		GLRenderer.popFrame();
		GLRenderer.popFrame();
	}
}
