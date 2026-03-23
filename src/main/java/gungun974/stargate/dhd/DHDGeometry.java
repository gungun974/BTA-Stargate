package gungun974.stargate.dhd;

import net.minecraft.core.util.phys.Vec3;

public class DHDGeometry {
	public static final double OFFSET = 0.0059345;

	public static final Vec3 AXIS_A = Vec3.getPermanentVec3(0.0, 1.01898, 0.010445);
	public static final Vec3 AXIS_B = Vec3.getPermanentVec3(0.0, 0.954843, -0.026586);

	public static Vec3 rotateAroundAxis(Vec3 p, Vec3 axisA, Vec3 axisB, double angleDeg) {
		double theta = Math.toRadians(angleDeg);
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);

		Vec3 k = axisA.vectorTo(axisB).normalize();
		Vec3 v = axisA.vectorTo(p);

		Vec3 part1 = v.scale(cos);
		Vec3 part2 = k.crossProduct(v).scale(sin);
		Vec3 part3 = k.scale(k.dotProduct(v) * (1.0 - cos));

		return part1
			.add(part2.x, part2.y, part2.z)
			.add(part3.x, part3.y, part3.z)
			.add(axisA.x, axisA.y, axisA.z);
	}

	public static KeyPositions calculateKeyPositions(int i, int segments, double angle) {
		Vec3 a1, a2, a3, a4, b1, b2, b3, b4;

		if (i >= segments) {
			double outer_width = 1.0 / segments * 1.6 - OFFSET;
			double inner_width = 1.0 / segments * 0.88 - OFFSET;

			double outer = outer_width / 2;
			double inner = inner_width / 2;

			a1 = rotateAroundAxis(Vec3.getTempVec3(-outer, 1.03246, -0.271884), AXIS_A, AXIS_B, angle);
			a2 = rotateAroundAxis(Vec3.getTempVec3(outer, 1.03246, -0.271884), AXIS_A, AXIS_B, angle);
			a3 = rotateAroundAxis(Vec3.getTempVec3(inner, 1.00585, -0.158142), AXIS_A, AXIS_B, angle);
			a4 = rotateAroundAxis(Vec3.getTempVec3(-inner, 1.00585, -0.158142), AXIS_A, AXIS_B, angle);

			b1 = rotateAroundAxis(Vec3.getTempVec3(-outer, 1.02661, -0.273251), AXIS_A, AXIS_B, angle);
			b2 = rotateAroundAxis(Vec3.getTempVec3(outer, 1.02661, -0.273251), AXIS_A, AXIS_B, angle);
			b3 = rotateAroundAxis(Vec3.getTempVec3(inner, 1, -0.159509), AXIS_A, AXIS_B, angle);
			b4 = rotateAroundAxis(Vec3.getTempVec3(-inner, 1, -0.159509), AXIS_A, AXIS_B, angle);
		} else {
			double outer_width = 1.0 / segments * 2.35 - OFFSET;
			double inner_width = 1.0 / segments * 1.68 - OFFSET;

			double outer = outer_width / 2;
			double inner = inner_width / 2;

			a1 = rotateAroundAxis(Vec3.getTempVec3(-outer, 1.04325, -0.405211), AXIS_A, AXIS_B, angle);
			a2 = rotateAroundAxis(Vec3.getTempVec3(outer, 1.04325, -0.405211), AXIS_A, AXIS_B, angle);
			a3 = rotateAroundAxis(Vec3.getTempVec3(inner, 1.0265, -0.290294), AXIS_A, AXIS_B, angle);
			a4 = rotateAroundAxis(Vec3.getTempVec3(-inner, 1.0265, -0.290294), AXIS_A, AXIS_B, angle);

			b1 = rotateAroundAxis(Vec3.getTempVec3(-outer, 1.03732, -0.406076), AXIS_A, AXIS_B, angle);
			b2 = rotateAroundAxis(Vec3.getTempVec3(outer, 1.03732, -0.406076), AXIS_A, AXIS_B, angle);
			b3 = rotateAroundAxis(Vec3.getTempVec3(inner, 1.02056, -0.29116), AXIS_A, AXIS_B, angle);
			b4 = rotateAroundAxis(Vec3.getTempVec3(-inner, 1.02056, -0.29116), AXIS_A, AXIS_B, angle);
		}

		return new KeyPositions(a1.makePermanent(), a2.makePermanent(), a3.makePermanent(), a4.makePermanent(), b1.makePermanent(), b2.makePermanent(), b3.makePermanent(), b4.makePermanent());
	}

	public static class KeyPositions {
		public final Vec3 a1, a2, a3, a4;
		public final Vec3 b1, b2, b3, b4;

		public KeyPositions(Vec3 a1, Vec3 a2, Vec3 a3, Vec3 a4, Vec3 b1, Vec3 b2, Vec3 b3, Vec3 b4) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.b1 = b1;
			this.b2 = b2;
			this.b3 = b3;
			this.b4 = b4;
		}
	}
}
