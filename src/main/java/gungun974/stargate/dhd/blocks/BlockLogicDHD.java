package gungun974.stargate.dhd.blocks;

import gungun974.stargate.core.RaycastHelper;
import gungun974.stargate.dhd.DHDGeometry;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

public class BlockLogicDHD extends BlockLogicRotatable {
	public static final int[] KEY_IDS = {23, 5, 28, 37, 33, 11, 36, 10, 20, 2, 3, 19, 8, 4, 31, 0, 18, 21, 6, 27, 9, 32, 38, 25, 22, 17, 13, 16, 1, 24, 35, 7, 26, 30, 14, 34, 29, 15};
	private static final RaycastHelper.KeyTriangles[] KEY_TRIANGLES = generateKeyTriangles();

	public BlockLogicDHD(Block<?> block, Material material) {
		super(block, material);
	}

	public static int getSegments() {
		return 19;
	}

	private static RaycastHelper.KeyTriangles[] generateKeyTriangles() {
		int segments = getSegments();

		RaycastHelper.KeyTriangles[] triangles = new RaycastHelper.KeyTriangles[1 + segments * 2];

		triangles[0] = new RaycastHelper.KeyTriangles(0, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.077111006f, 0.9903358f, -0.08806147f, 7.224759e-09f, 0.954843f, -0.0265861f, -0.049883917f, 1.0009317f, -0.10641402f),
			new RaycastHelper.Triangle(-0.049883917f, 1.0009317f, -0.10641402f, 7.224759e-09f, 0.954843f, -0.0265861f, -0.017251132f, 1.0065331f, -0.11611598f),
			new RaycastHelper.Triangle(0.017251082f, 1.0065331f, -0.116115995f, -0.017251132f, 1.0065331f, -0.11611598f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.049883872f, 1.0009317f, -0.10641405f, 0.017251082f, 1.0065331f, -0.116115995f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.077110976f, 0.9903358f, -0.08806151f, 0.049883872f, 1.0009317f, -0.10641405f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.09598191f, 0.9758938f, -0.063047156f, 0.077110976f, 0.9903358f, -0.08806151f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.104451716f, 0.9591706f, -0.03408168f, 0.09598191f, 0.9758938f, -0.063047156f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.101602554f, 0.9419784f, -0.0043039396f, 0.104451716f, 0.9591706f, -0.03408168f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.08774318f, 0.9261803f, 0.023059182f, 0.101602554f, 0.9419784f, -0.0043039396f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.06437547f, 0.9134882f, 0.045042478f, 0.08774318f, 0.9261803f, 0.023059182f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(0.03403169f, 0.9052776f, 0.0592637f, 0.06437547f, 0.9134882f, 0.045042478f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(4.3269875e-08f, 0.90243816f, 0.06418176f, 0.03403169f, 0.9052776f, 0.0592637f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(-0.034031615f, 0.9052776f, 0.059263714f, 4.3269875e-08f, 0.90243816f, 0.06418176f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(-0.0643754f, 0.9134882f, 0.04504253f, -0.034031615f, 0.9052776f, 0.059263714f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(-0.08774313f, 0.9261803f, 0.023059241f, -0.0643754f, 0.9134882f, 0.04504253f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(-0.10160253f, 0.9419784f, -0.004303891f, -0.08774313f, 0.9261803f, 0.023059241f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(-0.1044517f, 0.9591706f, -0.034081608f, -0.10160253f, 0.9419784f, -0.004303891f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(-0.09598193f, 0.9758938f, -0.063047074f, -0.1044517f, 0.9591706f, -0.034081608f, 7.224759e-09f, 0.954843f, -0.0265861f),
			new RaycastHelper.Triangle(-0.077111006f, 0.9903358f, -0.08806147f, -0.09598193f, 0.9758938f, -0.063047074f, 7.224759e-09f, 0.954843f, -0.0265861f)
		});

		for (int i = 0; i < segments * 2; i++) {
			int keyId = KEY_IDS[i];

			double angle = 360.0 * i / segments;

			if ((segments % 2) == 0) {
				angle += 180.0 / segments;
			}

			DHDGeometry.KeyPositions positions = DHDGeometry.calculateKeyPositions(i, segments, angle);

			Vec3 a1 = positions.a1;
			Vec3 a2 = positions.a2;
			Vec3 a3 = positions.a3;
			Vec3 a4 = positions.a4;

			triangles[i + 1] = new RaycastHelper.KeyTriangles(keyId + 1, new RaycastHelper.Triangle[]{
				new RaycastHelper.Triangle((float) a1.x, (float) a1.y, (float) a1.z, (float) a2.x, (float) a2.y, (float) a2.z, (float) a3.x, (float) a3.y, (float) a3.z),
				new RaycastHelper.Triangle((float) a1.x, (float) a1.y, (float) a1.z, (float) a3.x, (float) a3.y, (float) a3.z, (float) a4.x, (float) a4.y, (float) a4.z)
			});
		}

		return triangles;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		Direction direction = getDirectionFromMeta(world.getBlockMetadata(x, y, z));
		int keyId = RaycastHelper.detectPressedKey(KEY_TRIANGLES, x, y, z, direction, player);

		if (keyId != -1) {
			if (keyId == -0) {
				((TileEntityDHD) tileEntity).dial();
			} else {
				((TileEntityDHD) tileEntity).encode(keyId - 1);
			}

			return true;
		}

		return false;
	}
}
