package gungun974.stargate.core;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Direction;

public class RaycastHelper {
	private static final double EPSILON = 0.0000001;

	public static Double rayTriangleIntersection(
		double rayX, double rayY, double rayZ,
		double dirX, double dirY, double dirZ,
		double v0x, double v0y, double v0z,
		double v1x, double v1y, double v1z,
		double v2x, double v2y, double v2z
	) {
		double edge1x = v1x - v0x;
		double edge1y = v1y - v0y;
		double edge1z = v1z - v0z;
		double edge2x = v2x - v0x;
		double edge2y = v2y - v0y;
		double edge2z = v2z - v0z;

		double hx = dirY * edge2z - dirZ * edge2y;
		double hy = dirZ * edge2x - dirX * edge2z;
		double hz = dirX * edge2y - dirY * edge2x;

		double a = edge1x * hx + edge1y * hy + edge1z * hz;

		if (a > -EPSILON && a < EPSILON) {
			return null;
		}

		double f = 1.0 / a;
		double sx = rayX - v0x;
		double sy = rayY - v0y;
		double sz = rayZ - v0z;

		double u = f * (sx * hx + sy * hy + sz * hz);

		if (u < 0.0 || u > 1.0) {
			return null;
		}

		double qx = sy * edge1z - sz * edge1y;
		double qy = sz * edge1x - sx * edge1z;
		double qz = sx * edge1y - sy * edge1x;

		double v = f * (dirX * qx + dirY * qy + dirZ * qz);

		if (v < 0.0 || u + v > 1.0) {
			return null;
		}

		double t = f * (edge2x * qx + edge2y * qy + edge2z * qz);

		return t;
	}

	public static int detectPressedKey(KeyTriangles[] keyTriangles, int x, int y, int z, Direction direction, Player player) {
		double rayStartX = player.x;
		double rayStartY = player.y + player.getHeadHeight();
		double rayStartZ = player.z;

		double reach = 5.0;
		double pitch = Math.toRadians(player.xRot);
		double yaw = Math.toRadians(player.yRot);

		double rayDirX = -Math.sin(yaw) * Math.cos(pitch);
		double rayDirY = -Math.sin(pitch);
		double rayDirZ = Math.cos(yaw) * Math.cos(pitch);

		double closestDistance = Double.MAX_VALUE;
		int closestKey = -1;

		for (KeyTriangles keyTriangle : keyTriangles) {
			for (Triangle triangle : keyTriangle.triangles) {
				float v0x = triangle.v0x;
				float v0y = triangle.v0y;
				float v0z = triangle.v0z;
				float v1x = triangle.v1x;
				float v1y = triangle.v1y;
				float v1z = triangle.v1z;
				float v2x = triangle.v2x;
				float v2y = triangle.v2y;
				float v2z = triangle.v2z;

				float worldV0x, worldV0z, worldV1x, worldV1z, worldV2x, worldV2z;

				switch (direction) {
					case EAST:
						worldV0x = v0z;
						worldV0z = -v0x;
						worldV1x = v1z;
						worldV1z = -v1x;
						worldV2x = v2z;
						worldV2z = -v2x;
						break;
					case NORTH:
						worldV0x = -v0x;
						worldV0z = -v0z;
						worldV1x = -v1x;
						worldV1z = -v1z;
						worldV2x = -v2x;
						worldV2z = -v2z;
						break;
					case WEST:
						worldV0x = -v0z;
						worldV0z = v0x;
						worldV1x = -v1z;
						worldV1z = v1x;
						worldV2x = -v2z;
						worldV2z = v2x;
						break;
					case SOUTH:
					default:
						worldV0x = v0x;
						worldV0z = v0z;
						worldV1x = v1x;
						worldV1z = v1z;
						worldV2x = v2x;
						worldV2z = v2z;
						break;
				}

				double worldV0xAbs = x + 0.5 + worldV0x;
				double worldV0yAbs = y + v0y;
				double worldV0zAbs = z + 0.5 + worldV0z;
				double worldV1xAbs = x + 0.5 + worldV1x;
				double worldV1yAbs = y + v1y;
				double worldV1zAbs = z + 0.5 + worldV1z;
				double worldV2xAbs = x + 0.5 + worldV2x;
				double worldV2yAbs = y + v2y;
				double worldV2zAbs = z + 0.5 + worldV2z;

				Double t = rayTriangleIntersection(
					rayStartX, rayStartY, rayStartZ,
					rayDirX, rayDirY, rayDirZ,
					worldV0xAbs, worldV0yAbs, worldV0zAbs,
					worldV1xAbs, worldV1yAbs, worldV1zAbs,
					worldV2xAbs, worldV2yAbs, worldV2zAbs
				);

				if (t != null && t > 0 && t < reach && t < closestDistance) {
					closestDistance = t;
					closestKey = keyTriangle.id;
				}
			}
		}

		if (closestKey != -1) {
			return closestKey;
		}

		return -1;
	}

	public static class Triangle {
		public final float v0x, v0y, v0z;
		public final float v1x, v1y, v1z;
		public final float v2x, v2y, v2z;

		public Triangle(float v0x, float v0y, float v0z, float v1x, float v1y, float v1z, float v2x, float v2y, float v2z) {
			this.v0x = v0x;
			this.v0y = v0y;
			this.v0z = v0z;
			this.v1x = v1x;
			this.v1y = v1y;
			this.v1z = v1z;
			this.v2x = v2x;
			this.v2y = v2y;
			this.v2z = v2z;
		}
	}

	public static class KeyTriangles {
		public final int id;
		public final Triangle[] triangles;

		public KeyTriangles(int id, Triangle[] triangles) {
			this.id = id;
			this.triangles = triangles;
		}
	}
}
