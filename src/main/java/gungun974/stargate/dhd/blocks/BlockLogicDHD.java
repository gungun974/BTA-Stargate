package gungun974.stargate.dhd.blocks;

import gungun974.stargate.core.RaycastHelper;
import gungun974.stargate.dhd.tiles.TileEntityDHD;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicDHD extends BlockLogicRotatable {
	private static final RaycastHelper.KeyTriangles[] KEY_TRIANGLES = {
		new RaycastHelper.KeyTriangles(0, new RaycastHelper.Triangle[]{
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
		}),
		new RaycastHelper.KeyTriangles(1, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.375151f, 0.874379f, -0.112708f, -0.266264f, 0.906793f, -0.082962f, -0.245975f, 0.946853f, -0.152349f),
			new RaycastHelper.Triangle(-0.375151f, 0.874379f, -0.112708f, -0.245975f, 0.946853f, -0.152349f, -0.346285f, 0.931373f, -0.211425f)
		}),
		new RaycastHelper.KeyTriangles(2, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.080121f, 0.786164f, 0.154708f, 0.043864f, 0.869111f, 0.078696f, 0.002036f, 0.865621f, 0.08474f),
			new RaycastHelper.Triangle(0.080121f, 0.786164f, 0.154708f, 0.002036f, 0.865621f, 0.08474f, 0.002585f, 0.779694f, 0.165913f)
		}),
		new RaycastHelper.KeyTriangles(3, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.119238f, 0.678527f, 0.226517f, 0.084248f, 0.767494f, 0.15831f, 0.002725f, 0.760692f, 0.170091f),
			new RaycastHelper.Triangle(0.119238f, 0.678527f, 0.226517f, 0.002725f, 0.760692f, 0.170091f, 0.003255f, 0.66885f, 0.243278f)
		}),
		new RaycastHelper.KeyTriangles(4, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.003255f, 0.66885f, 0.243279f, -0.002725f, 0.760692f, 0.170091f, -0.084248f, 0.767494f, 0.15831f),
			new RaycastHelper.Triangle(-0.003255f, 0.66885f, 0.243279f, -0.084248f, 0.767494f, 0.15831f, -0.119238f, 0.678527f, 0.226517f)
		}),
		new RaycastHelper.KeyTriangles(5, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.317146f, 0.755546f, 0.093116f, -0.225351f, 0.822407f, 0.063198f, -0.258551f, 0.860251f, -0.00235f),
			new RaycastHelper.Triangle(-0.317146f, 0.755546f, 0.093116f, -0.258551f, 0.860251f, -0.00235f, -0.36438f, 0.809387f, -0.00014f)
		}),
		new RaycastHelper.KeyTriangles(6, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.065214f, 1.042719f, -0.404283f, 0.046701f, 1.026047f, -0.289517f, 0.124873f, 1.012629f, -0.266276f),
			new RaycastHelper.Triangle(0.065214f, 1.042719f, -0.404283f, 0.124873f, 1.012629f, -0.266276f, 0.17643f, 1.023629f, -0.371217f)
		}),
		new RaycastHelper.KeyTriangles(7, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.17643f, 1.023629f, -0.371217f, -0.124873f, 1.012629f, -0.266276f, -0.046701f, 1.026047f, -0.289517f),
			new RaycastHelper.Triangle(-0.17643f, 1.023629f, -0.371217f, -0.046701f, 1.026047f, -0.289517f, -0.065214f, 1.042719f, -0.404283f)
		}),
		new RaycastHelper.KeyTriangles(8, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.158224f, 0.807297f, 0.118104f, -0.088225f, 0.881114f, 0.057905f, -0.116946f, 0.896714f, 0.030886f),
			new RaycastHelper.Triangle(-0.158224f, 0.807297f, 0.118104f, -0.116946f, 0.896714f, 0.030886f, -0.211464f, 0.836214f, 0.068018f)
		}),
		new RaycastHelper.KeyTriangles(9, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.233946f, 0.709565f, 0.172757f, -0.166393f, 0.789721f, 0.119811f, -0.22237f, 0.820125f, 0.06715f),
			new RaycastHelper.Triangle(-0.233946f, 0.709565f, 0.172757f, -0.22237f, 0.820125f, 0.06715f, -0.313586f, 0.752821f, 0.097836f)
		}),
		new RaycastHelper.KeyTriangles(10, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.044403f, 1.032031f, -0.271147f, 0.02522f, 1.005514f, -0.157562f, 0.065328f, 0.99863f, -0.145637f),
			new RaycastHelper.Triangle(0.044403f, 1.032031f, -0.271147f, 0.065328f, 0.99863f, -0.145637f, 0.118752f, 1.019269f, -0.249043f)
		}),
		new RaycastHelper.KeyTriangles(11, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.313586f, 0.752821f, 0.097836f, 0.22237f, 0.820125f, 0.06715f, 0.166393f, 0.789721f, 0.119811f),
			new RaycastHelper.Triangle(0.313586f, 0.752821f, 0.097836f, 0.166393f, 0.789721f, 0.119811f, 0.233946f, 0.709565f, 0.172757f)
		}),
		new RaycastHelper.KeyTriangles(12, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.375688f, 0.871135f, -0.10709f, 0.266715f, 0.904077f, -0.078258f, 0.259889f, 0.862893f, -0.006926f),
			new RaycastHelper.Triangle(0.375688f, 0.871135f, -0.10709f, 0.259889f, 0.862893f, -0.006926f, 0.365978f, 0.812542f, -0.005605f)
		}),
		new RaycastHelper.KeyTriangles(14, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.211464f, 0.836214f, 0.068018f, 0.116946f, 0.896714f, 0.030886f, 0.088225f, 0.881114f, 0.057905f),
			new RaycastHelper.Triangle(0.211464f, 0.836214f, 0.068018f, 0.088225f, 0.881114f, 0.057905f, 0.158224f, 0.807297f, 0.118104f)
		}),
		new RaycastHelper.KeyTriangles(15, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.253202f, 0.918624f, -0.074719f, -0.140373f, 0.94297f, -0.049231f, -0.129963f, 0.963524f, -0.084832f),
			new RaycastHelper.Triangle(-0.253202f, 0.918624f, -0.074719f, -0.129963f, 0.963524f, -0.084832f, -0.233905f, 0.956725f, -0.140712f)
		}),
		new RaycastHelper.KeyTriangles(16, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.118752f, 1.019269f, -0.249043f, -0.065329f, 0.99863f, -0.145637f, -0.02522f, 1.005514f, -0.157562f),
			new RaycastHelper.Triangle(-0.118752f, 1.019269f, -0.249043f, -0.02522f, 1.005514f, -0.157562f, -0.044403f, 1.032031f, -0.271147f)
		}),
		new RaycastHelper.KeyTriangles(17, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.154144f, 0.80571f, 0.120854f, 0.085011f, 0.879864f, 0.060072f, 0.047716f, 0.869772f, 0.077551f),
			new RaycastHelper.Triangle(0.154144f, 0.80571f, 0.120854f, 0.047716f, 0.869772f, 0.077551f, 0.085011f, 0.787003f, 0.153255f)
		}),
		new RaycastHelper.KeyTriangles(18, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.245868f, 0.874372f, 0.001927f, 0.136207f, 0.917836f, -0.005698f, 0.119173f, 0.898419f, 0.027933f),
			new RaycastHelper.Triangle(0.245868f, 0.874372f, 0.001927f, 0.119173f, 0.898419f, 0.027933f, 0.214292f, 0.838378f, 0.06427f)
		}),
		new RaycastHelper.KeyTriangles(19, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.34367f, 0.934354f, -0.216588f, -0.243786f, 0.949349f, -0.156672f, -0.19858f, 0.983945f, -0.216593f),
			new RaycastHelper.Triangle(-0.34367f, 0.934354f, -0.216588f, -0.19858f, 0.983945f, -0.216593f, -0.279356f, 0.983573f, -0.301839f)
		}),
		new RaycastHelper.KeyTriangles(20, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.125395f, 0.679584f, 0.224687f, -0.089403f, 0.768379f, 0.156777f, -0.162091f, 0.788047f, 0.12271f),
			new RaycastHelper.Triangle(-0.125395f, 0.679584f, 0.224687f, -0.162091f, 0.788047f, 0.12271f, -0.228809f, 0.707566f, 0.17622f)
		}),
		new RaycastHelper.KeyTriangles(21, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.228809f, 0.707566f, 0.17622f, 0.162092f, 0.788047f, 0.12271f, 0.089403f, 0.768379f, 0.156777f),
			new RaycastHelper.Triangle(0.228809f, 0.707566f, 0.17622f, 0.089403f, 0.768379f, 0.156777f, 0.125395f, 0.679584f, 0.224687f)
		}),
		new RaycastHelper.KeyTriangles(22, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.274947f, 0.985968f, -0.305987f, -0.194889f, 0.98595f, -0.220066f, -0.129667f, 1.011332f, -0.26403f),
			new RaycastHelper.Triangle(-0.274947f, 0.985968f, -0.305987f, -0.129667f, 1.011332f, -0.26403f, -0.182155f, 1.02208f, -0.368534f)
		}),
		new RaycastHelper.KeyTriangles(23, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.253629f, 0.916048f, -0.070257f, 0.140709f, 0.94094f, -0.045717f, 0.137207f, 0.91981f, -0.009117f),
			new RaycastHelper.Triangle(0.253629f, 0.916048f, -0.070257f, 0.137207f, 0.91981f, -0.009117f, 0.247137f, 0.876878f, -0.002413f)
		}),
		new RaycastHelper.KeyTriangles(24, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.058793f, 1.043255f, -0.40521f, -0.041325f, 1.026496f, -0.290294f, 0.041325f, 1.026496f, -0.290294f),
			new RaycastHelper.Triangle(-0.058793f, 1.043255f, -0.40521f, 0.041325f, 1.026496f, -0.290294f, 0.058793f, 1.043255f, -0.405211f)
		}),
		new RaycastHelper.KeyTriangles(25, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.002585f, 0.779694f, 0.165913f, -0.002036f, 0.865621f, 0.08474f, -0.043864f, 0.869111f, 0.078696f),
			new RaycastHelper.Triangle(-0.002585f, 0.779694f, 0.165913f, -0.043864f, 0.869111f, 0.078696f, -0.080121f, 0.786164f, 0.154708f)
		}),
		new RaycastHelper.KeyTriangles(26, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.233904f, 0.956725f, -0.140713f, 0.129963f, 0.963524f, -0.084833f, 0.140373f, 0.94297f, -0.049232f),
			new RaycastHelper.Triangle(0.233904f, 0.956725f, -0.140713f, 0.140373f, 0.94297f, -0.049232f, 0.253202f, 0.918624f, -0.074719f)
		}),
		new RaycastHelper.KeyTriangles(27, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.214292f, 0.838378f, 0.06427f, -0.119173f, 0.898419f, 0.027933f, -0.136207f, 0.917836f, -0.005698f),
			new RaycastHelper.Triangle(-0.214292f, 0.838378f, 0.06427f, -0.136207f, 0.917836f, -0.005698f, -0.245868f, 0.874372f, 0.001927f)
		}),
		new RaycastHelper.KeyTriangles(28, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.021203f, 1.00585f, -0.158142f, 0.039304f, 1.03246f, -0.271884f, -0.039304f, 1.03246f, -0.271884f),
			new RaycastHelper.Triangle(0.021203f, 1.00585f, -0.158142f, -0.039304f, 1.03246f, -0.271884f, -0.021203f, 1.00585f, -0.158142f)
		}),
		new RaycastHelper.KeyTriangles(29, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.182155f, 1.02208f, -0.368534f, 0.129667f, 1.011332f, -0.26403f, 0.194889f, 0.98595f, -0.220066f),
			new RaycastHelper.Triangle(0.182155f, 1.02208f, -0.368534f, 0.194889f, 0.98595f, -0.220066f, 0.274947f, 0.985968f, -0.305987f)
		}),
		new RaycastHelper.KeyTriangles(30, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.185332f, 0.993898f, -0.205098f, -0.102375f, 0.984637f, -0.121402f, -0.06891f, 0.997661f, -0.143959f),
			new RaycastHelper.Triangle(-0.185332f, 0.993898f, -0.205098f, -0.06891f, 0.997661f, -0.143959f, -0.123299f, 1.018039f, -0.246912f)
		}),
		new RaycastHelper.KeyTriangles(31, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.247137f, 0.876878f, -0.002413f, -0.137207f, 0.91981f, -0.009117f, -0.140709f, 0.94094f, -0.045717f),
			new RaycastHelper.Triangle(-0.247137f, 0.876878f, -0.002413f, -0.140709f, 0.94094f, -0.045717f, -0.253629f, 0.916048f, -0.070257f)
		}),
		new RaycastHelper.KeyTriangles(32, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.365978f, 0.812542f, -0.005605f, -0.259889f, 0.862893f, -0.006926f, -0.266714f, 0.904076f, -0.078258f),
			new RaycastHelper.Triangle(-0.365978f, 0.812542f, -0.005605f, -0.266714f, 0.904076f, -0.078258f, -0.375688f, 0.871135f, -0.10709f)
		}),
		new RaycastHelper.KeyTriangles(33, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.123299f, 1.018039f, -0.246912f, 0.06891f, 0.997661f, -0.143959f, 0.102375f, 0.984637f, -0.121402f),
			new RaycastHelper.Triangle(0.123299f, 1.018039f, -0.246912f, 0.102375f, 0.984637f, -0.121402f, 0.185332f, 0.993898f, -0.205098f)
		}),
		new RaycastHelper.KeyTriangles(34, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.346285f, 0.931373f, -0.211426f, 0.245975f, 0.946853f, -0.152349f, 0.266264f, 0.906793f, -0.082962f),
			new RaycastHelper.Triangle(0.346285f, 0.931373f, -0.211426f, 0.266264f, 0.906793f, -0.082962f, 0.375151f, 0.874379f, -0.112709f)
		}),
		new RaycastHelper.KeyTriangles(35, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.231828f, 0.959092f, -0.144813f, -0.128327f, 0.965389f, -0.088062f, -0.105133f, 0.983139f, -0.118807f),
			new RaycastHelper.Triangle(-0.231828f, 0.959092f, -0.144813f, -0.105133f, 0.983139f, -0.118807f, -0.188833f, 0.991996f, -0.201804f)
		}),
		new RaycastHelper.KeyTriangles(36, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(-0.085011f, 0.787003f, 0.153255f, -0.047716f, 0.869772f, 0.077551f, -0.085011f, 0.879864f, 0.060071f),
			new RaycastHelper.Triangle(-0.085011f, 0.787003f, 0.153255f, -0.085011f, 0.879864f, 0.060071f, -0.154144f, 0.80571f, 0.120854f)
		}),
		new RaycastHelper.KeyTriangles(37, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.36438f, 0.809387f, -0.00014f, 0.258551f, 0.860251f, -0.00235f, 0.225351f, 0.822407f, 0.063198f),
			new RaycastHelper.Triangle(0.36438f, 0.809387f, -0.00014f, 0.225351f, 0.822407f, 0.063198f, 0.317146f, 0.755546f, 0.093116f)
		}),
		new RaycastHelper.KeyTriangles(38, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.279356f, 0.983574f, -0.30184f, 0.19858f, 0.983945f, -0.216593f, 0.243786f, 0.949349f, -0.156672f),
			new RaycastHelper.Triangle(0.279356f, 0.983574f, -0.30184f, 0.243786f, 0.949349f, -0.156672f, 0.34367f, 0.934354f, -0.216588f)
		}),
		new RaycastHelper.KeyTriangles(39, new RaycastHelper.Triangle[]{
			new RaycastHelper.Triangle(0.188833f, 0.991996f, -0.201804f, 0.105133f, 0.983139f, -0.118807f, 0.128327f, 0.965389f, -0.088062f),
			new RaycastHelper.Triangle(0.188833f, 0.991996f, -0.201804f, 0.128327f, 0.965389f, -0.088062f, 0.231828f, 0.959092f, -0.144813f)
		})
	};

	public BlockLogicDHD(Block<?> block, Material material) {
		super(block, material);
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
			((TileEntityDHD) tileEntity).dial(keyId);

			return true;
		}

		return false;
	}
}
