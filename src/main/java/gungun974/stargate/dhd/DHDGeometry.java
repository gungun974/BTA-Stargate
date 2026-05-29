package gungun974.stargate.dhd;

import org.joml.Vector3d;
import org.joml.Vector3dc;

public class DHDGeometry {
	public static final double OFFSET = 0.0059345;

	public static final Vector3dc AXIS_A = new Vector3d(0.0, 1.01898, 0.010445);
	public static final Vector3dc AXIS_B = new Vector3d(0.0, 0.954843, -0.026586);

	public static Vector3dc rotateAroundAxis(Vector3dc p, Vector3dc axisA, Vector3dc axisB, double angleDeg) {
		double theta = Math.toRadians(angleDeg);
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);

		Vector3d k = new Vector3d(axisB).sub(axisA).normalize();
		Vector3d v = new Vector3d(p).sub(axisA);

		Vector3d part1 = new Vector3d(v).mul(cos);
		Vector3d part2 = new Vector3d(k).cross(v).mul(sin);
		Vector3d part3 = new Vector3d(k).mul(k.dot(v) * (1.0 - cos));

		return part1.add(part2).add(part3).add(axisA);
	}

	public static KeyPositions calculateKeyPositions(int i, int segments, double angle) {
		Vector3dc a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4;

		if (i >= segments) {
			double outer_width = 1.0 / segments * 1.6 - OFFSET;
			double inner_width = 1.0 / segments * 0.88 - OFFSET;

			double outer = outer_width / 2;
			double inner = inner_width / 2;
			double mid = (outer + inner) / 2;

			double m1y = 1.03246;
			double m1z = -0.271884;

			double m2y = 1.00585;
			double m2z = -0.158142;

			a1 = rotateAroundAxis(new Vector3d(-outer, m1y, m1z), AXIS_A, AXIS_B, angle);
			a2 = rotateAroundAxis(new Vector3d(outer, m1y, m1z), AXIS_A, AXIS_B, angle);
			a3 = rotateAroundAxis(new Vector3d(inner, m2y, m2z), AXIS_A, AXIS_B, angle);
			a4 = rotateAroundAxis(new Vector3d(-inner, m2y, m2z), AXIS_A, AXIS_B, angle);

			b1 = rotateAroundAxis(new Vector3d(-outer, 1.02661, -0.273251), AXIS_A, AXIS_B, angle);
			b2 = rotateAroundAxis(new Vector3d(outer, 1.02661, -0.273251), AXIS_A, AXIS_B, angle);
			b3 = rotateAroundAxis(new Vector3d(inner, 1, -0.159509), AXIS_A, AXIS_B, angle);
			b4 = rotateAroundAxis(new Vector3d(-inner, 1, -0.159509), AXIS_A, AXIS_B, angle);

			double m3y = m2y + (m1y - m2y) * 0.88;
			double m3z = m2z + (m1z - m2z) * 0.88;

			double m4y = m1y + (m2y - m1y) * 0.88;
			double m4z = m1z + (m2z - m1z) * 0.88;

			c1 = rotateAroundAxis(new Vector3d(-mid, m3y, m3z), AXIS_A, AXIS_B, angle);
			c2 = rotateAroundAxis(new Vector3d(mid, m3y, m3z), AXIS_A, AXIS_B, angle);
			c3 = rotateAroundAxis(new Vector3d(mid, m4y, m4z), AXIS_A, AXIS_B, angle);
			c4 = rotateAroundAxis(new Vector3d(-mid, m4y, m4z), AXIS_A, AXIS_B, angle);
		} else {
			double outer_width = 1.0 / segments * 2.35 - OFFSET;
			double inner_width = 1.0 / segments * 1.68 - OFFSET;

			double outer = outer_width / 2;
			double inner = inner_width / 2;
			double mid = (outer + inner) / 2;

			double m1y = 1.04325;
			double m1z = -0.405211;

			double m2y = 1.0265;
			double m2z = -0.290294;

			a1 = rotateAroundAxis(new Vector3d(-outer, m1y, m1z), AXIS_A, AXIS_B, angle);
			a2 = rotateAroundAxis(new Vector3d(outer, m1y, m1z), AXIS_A, AXIS_B, angle);
			a3 = rotateAroundAxis(new Vector3d(inner, m2y, m2z), AXIS_A, AXIS_B, angle);
			a4 = rotateAroundAxis(new Vector3d(-inner, m2y, m2z), AXIS_A, AXIS_B, angle);

			b1 = rotateAroundAxis(new Vector3d(-outer, 1.03732, -0.406076), AXIS_A, AXIS_B, angle);
			b2 = rotateAroundAxis(new Vector3d(outer, 1.03732, -0.406076), AXIS_A, AXIS_B, angle);
			b3 = rotateAroundAxis(new Vector3d(inner, 1.02056, -0.29116), AXIS_A, AXIS_B, angle);
			b4 = rotateAroundAxis(new Vector3d(-inner, 1.02056, -0.29116), AXIS_A, AXIS_B, angle);

			double m3y = m2y + (m1y - m2y) * 0.88;
			double m3z = m2z + (m1z - m2z) * 0.88;

			double m4y = m1y + (m2y - m1y) * 0.88;
			double m4z = m1z + (m2z - m1z) * 0.88;

			mid *= 0.85;

			c1 = rotateAroundAxis(new Vector3d(-mid, m3y, m3z), AXIS_A, AXIS_B, angle);
			c2 = rotateAroundAxis(new Vector3d(mid, m3y, m3z), AXIS_A, AXIS_B, angle);
			c3 = rotateAroundAxis(new Vector3d(mid, m4y, m4z), AXIS_A, AXIS_B, angle);
			c4 = rotateAroundAxis(new Vector3d(-mid, m4y, m4z), AXIS_A, AXIS_B, angle);
		}

		return new KeyPositions(a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4);
	}

	public record KeyPositions(Vector3dc a1, Vector3dc a2, Vector3dc a3, Vector3dc a4, Vector3dc b1, Vector3dc b2,
							   Vector3dc b3, Vector3dc b4, Vector3dc c1, Vector3dc c2, Vector3dc c3, Vector3dc c4) {
	}
}
