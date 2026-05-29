package gungun974.stargate.core;

import net.minecraft.client.render.renderer.GLRenderer;
import org.joml.Math;

public class CustomLighting {
	public static void disable() {
		GLRenderer.globalSetLightEnabled(false);
		GLRenderer.globalGetLight0().enabled = false;
		GLRenderer.globalGetLight1().enabled = false;
		GLRenderer.setLightMapStrength(0.0F);
	}

	public static void enableInventoryLight() {
		enableLight(0.4F, 0.5F);
		GLRenderer.setLightMapStrength(0.0F);
		GLRenderer.globalGetNormalTransformMatrix()
			.rotateY(Math.toRadians(-30.0F))
			.rotateX(Math.toRadians(155.0F))
			.invert();
	}

	public static void enableLight() {
		enableLight(0.4F, 0.6F);
	}

	public static void enableLight(float ambientBrightness, float lightBrightness) {
		GLRenderer.globalGetNormalTransformMatrix().identity();
		GLRenderer.globalSetLightEnabled(true);
		GLRenderer.globalGetLight0().enabled = true;
		GLRenderer.globalGetLight1().enabled = true;
		GLRenderer.setLightMapStrength(1.0F);

		GLRenderer.globalGetLight0().position.set(0.2F, 1.0F, -0.7F).normalize();
		GLRenderer.globalGetLight0().diffuse.set(lightBrightness, lightBrightness, lightBrightness, 1.0F);
		GLRenderer.globalGetLight0().ambient.set(0.0F, 0.0F, 0.0F, 1.0F);
		GLRenderer.globalGetLight0().specular.set(0.0F, 0.0F, 0.0F, 1.0F);

		GLRenderer.globalGetLight1().position.set(-0.2F, 1.0F, 0.7F).normalize();
		GLRenderer.globalGetLight1().diffuse.set(lightBrightness, lightBrightness, lightBrightness, 1.0F);
		GLRenderer.globalGetLight1().ambient.set(0.0F, 0.0F, 0.0F, 1.0F);
		GLRenderer.globalGetLight1().specular.set(0.0F, 0.0F, 0.0F, 1.0F);

		GLRenderer.globalGetLightModelAmbient().set(ambientBrightness, ambientBrightness, ambientBrightness, 1.0F);
	}
}
