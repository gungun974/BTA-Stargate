package gungun974.stargate.dhd.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererDHD extends TileEntityRenderer<TileEntityDHD> {
	static private final WavefrontLoader DHD = new WavefrontLoader("/assets/stargate/models/DHD/DHD.obj");

	@Override
	public void doRender(Tessellator tessellator, TileEntityDHD tileEntity, double x, double y, double z, float partialTicks) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);

//		if (LightmapHelper.isLightmapEnabled()) {
//			LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
//		}

		DHD.render(tessellator);

		GL11.glPopMatrix();
	}
}
