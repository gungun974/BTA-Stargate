package gungun974.stargate.core;

import net.minecraft.client.GLAllocation;
import net.minecraft.core.util.phys.Vec3;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class CustomLighting {
	private static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

	public static void disable() {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_LIGHT0);
		GL11.glDisable(GL11.GL_LIGHT1);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}

	public static void enableInventoryLight() {
		GL11.glPushMatrix();
		GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(155.0F, 1.0F, 0.0F, 0.0F);
		enableLight(0.4F, 0.5F);
		GL11.glPopMatrix();
	}

	public static void enableLight() {
		enableLight(0.4F, 0.6F);
	}

	public static void enableLight(float ambientBrightness, float lightBrightness) {
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_LIGHT1);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);

		Vec3 vec = Vec3.getTempVec3(0.6, 1.0F, -0.7).normalize();
		GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, getBuffer(vec.x, vec.y, vec.z, 0.0F));
		GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, getBuffer(lightBrightness, lightBrightness, lightBrightness, 1.0F));
		GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		vec = Vec3.getTempVec3(-0.6, 1.0F, 0.7).normalize();
		GL11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, getBuffer(vec.x, vec.y, vec.z, 0.0F));
		GL11.glLightfv(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, getBuffer(lightBrightness, lightBrightness, lightBrightness, 1.0F));
		GL11.glLightfv(GL11.GL_LIGHT1, GL11.GL_AMBIENT, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glLightfv(GL11.GL_LIGHT1, GL11.GL_SPECULAR, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glLightModelfv(GL11.GL_LIGHT_MODEL_AMBIENT, getBuffer(ambientBrightness, ambientBrightness, ambientBrightness, 1.0F));
	}

	private static FloatBuffer getBuffer(double d, double d1, double d2, double d3) {
		return getBuffer((float) d, (float) d1, (float) d2, (float) d3);
	}

	private static FloatBuffer getBuffer(float f, float f1, float f2, float f3) {
		buffer.clear();
		buffer.put(f).put(f1).put(f2).put(f3);
		buffer.flip();
		return buffer;
	}
}
