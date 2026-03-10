package gungun974.stargate.dhd.renders;

import gungun974.stargate.core.WavefrontLoader;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererDHD extends TileEntityRenderer<TileEntityDHD> {
	static private final WavefrontLoader DHD = new WavefrontLoader("/assets/stargate/models/DHD/DHD.obj");

	static private final WavefrontLoader K001 = new WavefrontLoader("/assets/stargate/models/DHD/001.obj");
	static private final WavefrontLoader K002 = new WavefrontLoader("/assets/stargate/models/DHD/002.obj");
	static private final WavefrontLoader K003 = new WavefrontLoader("/assets/stargate/models/DHD/003.obj");
	static private final WavefrontLoader K004 = new WavefrontLoader("/assets/stargate/models/DHD/004.obj");
	static private final WavefrontLoader K005 = new WavefrontLoader("/assets/stargate/models/DHD/005.obj");
	static private final WavefrontLoader K006 = new WavefrontLoader("/assets/stargate/models/DHD/006.obj");
	static private final WavefrontLoader K007 = new WavefrontLoader("/assets/stargate/models/DHD/007.obj");
	static private final WavefrontLoader K008 = new WavefrontLoader("/assets/stargate/models/DHD/008.obj");
	static private final WavefrontLoader K009 = new WavefrontLoader("/assets/stargate/models/DHD/009.obj");
	static private final WavefrontLoader K010 = new WavefrontLoader("/assets/stargate/models/DHD/010.obj");
	static private final WavefrontLoader K011 = new WavefrontLoader("/assets/stargate/models/DHD/011.obj");
	static private final WavefrontLoader K012 = new WavefrontLoader("/assets/stargate/models/DHD/012.obj");
	static private final WavefrontLoader K014 = new WavefrontLoader("/assets/stargate/models/DHD/014.obj");
	static private final WavefrontLoader K015 = new WavefrontLoader("/assets/stargate/models/DHD/015.obj");
	static private final WavefrontLoader K016 = new WavefrontLoader("/assets/stargate/models/DHD/016.obj");
	static private final WavefrontLoader K017 = new WavefrontLoader("/assets/stargate/models/DHD/017.obj");
	static private final WavefrontLoader K018 = new WavefrontLoader("/assets/stargate/models/DHD/018.obj");
	static private final WavefrontLoader K019 = new WavefrontLoader("/assets/stargate/models/DHD/019.obj");
	static private final WavefrontLoader K020 = new WavefrontLoader("/assets/stargate/models/DHD/020.obj");
	static private final WavefrontLoader K021 = new WavefrontLoader("/assets/stargate/models/DHD/021.obj");
	static private final WavefrontLoader K022 = new WavefrontLoader("/assets/stargate/models/DHD/022.obj");
	static private final WavefrontLoader K023 = new WavefrontLoader("/assets/stargate/models/DHD/023.obj");
	static private final WavefrontLoader K024 = new WavefrontLoader("/assets/stargate/models/DHD/024.obj");
	static private final WavefrontLoader K025 = new WavefrontLoader("/assets/stargate/models/DHD/025.obj");
	static private final WavefrontLoader K026 = new WavefrontLoader("/assets/stargate/models/DHD/026.obj");
	static private final WavefrontLoader K027 = new WavefrontLoader("/assets/stargate/models/DHD/027.obj");
	static private final WavefrontLoader K028 = new WavefrontLoader("/assets/stargate/models/DHD/028.obj");
	static private final WavefrontLoader K029 = new WavefrontLoader("/assets/stargate/models/DHD/029.obj");
	static private final WavefrontLoader K030 = new WavefrontLoader("/assets/stargate/models/DHD/030.obj");
	static private final WavefrontLoader K031 = new WavefrontLoader("/assets/stargate/models/DHD/031.obj");
	static private final WavefrontLoader K032 = new WavefrontLoader("/assets/stargate/models/DHD/032.obj");
	static private final WavefrontLoader K033 = new WavefrontLoader("/assets/stargate/models/DHD/033.obj");
	static private final WavefrontLoader K034 = new WavefrontLoader("/assets/stargate/models/DHD/034.obj");
	static private final WavefrontLoader K035 = new WavefrontLoader("/assets/stargate/models/DHD/035.obj");
	static private final WavefrontLoader K036 = new WavefrontLoader("/assets/stargate/models/DHD/036.obj");
	static private final WavefrontLoader K037 = new WavefrontLoader("/assets/stargate/models/DHD/037.obj");
	static private final WavefrontLoader K038 = new WavefrontLoader("/assets/stargate/models/DHD/038.obj");
	static private final WavefrontLoader K039 = new WavefrontLoader("/assets/stargate/models/DHD/039.obj");

	@Override
	public void doRender(Tessellator tessellator, TileEntityDHD tileEntity, double x, double y, double z, float partialTicks) {
		GL11.glPushMatrix();

		GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);

//		if (LightmapHelper.isLightmapEnabled()) {
//			LightmapHelper.setLightmapCoord(LightmapHelper.getLightmapCoord(15, 15));
//		}

		DHD.render(tessellator);

		K001.render(tessellator);
		K002.render(tessellator);
		K003.render(tessellator);
		K004.render(tessellator);
		K005.render(tessellator);
		K006.render(tessellator);
		K007.render(tessellator);
		K008.render(tessellator);
		K009.render(tessellator);
		K010.render(tessellator);
		K011.render(tessellator);
		K012.render(tessellator);
		K014.render(tessellator);
		K015.render(tessellator);
		K016.render(tessellator);
		K017.render(tessellator);
		K018.render(tessellator);
		K019.render(tessellator);
		K020.render(tessellator);
		K021.render(tessellator);
		K022.render(tessellator);
		K023.render(tessellator);
		K024.render(tessellator);
		K025.render(tessellator);
		K026.render(tessellator);
		K027.render(tessellator);
		K028.render(tessellator);
		K029.render(tessellator);
		K030.render(tessellator);
		K031.render(tessellator);
		K032.render(tessellator);
		K033.render(tessellator);
		K034.render(tessellator);
		K035.render(tessellator);
		K036.render(tessellator);
		K037.render(tessellator);
		K038.render(tessellator);
		K039.render(tessellator);

		GL11.glPopMatrix();
	}
}
