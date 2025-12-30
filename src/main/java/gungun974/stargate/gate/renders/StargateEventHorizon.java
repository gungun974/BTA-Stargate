package gungun974.stargate.gate.renders;

import java.util.Random;

public class StargateEventHorizon {
	private static final Random random = new Random();

	double[][][] eventHorizonGrid;

	public static double sinWave(double t) {
		double sum = 0;

		sum += 0.7 * Math.sin(1.3 * t + 0.5);
		sum += 0.5 * Math.sin(2.1 * t + 1.7);
		sum += 0.9 * Math.sin(0.7 * t + 3.2);
		sum += 0.6 * Math.sin(3.5 * t + 2.4);
		sum += 0.4 * Math.sin(4.2 * t + 5.1);

		return (sum + 3.1) / 6.2;
	}

	static public double getEventHorizonVortexShape(double distance, double diameter, double angle, double t) {
		final double endCap = 0.75;

		final double wave = Math.sin(distance * 25) * 0.025 + (0.5 - sinWave(distance * Math.sin(Math.PI * 4 * angle) * diameter * 10 + t) * 0.5);

		if (distance < endCap) {
			return diameter + wave;
		}

		final double x = (distance - endCap) / (1 - endCap);

		return diameter - Math.pow(x, 2) * (diameter) + wave;
	}

	public double[][][] getEventHorizonGrid() {
		if (eventHorizonGrid == null) {
			int m = TileEntityRenderStargate.eventHorizonGridRadialSize;
			int n = TileEntityRenderStargate.eventHorizonGridPolarSize;
			eventHorizonGrid = new double[2][n + 2][m + 1];
			for (int i = 0; i < 2; i++) {
				eventHorizonGrid[i][0] = eventHorizonGrid[i][n];
				eventHorizonGrid[i][n + 1] = eventHorizonGrid[i][1];
			}
		}
		return eventHorizonGrid;
	}

	public void applyRandomImpulse() {
		double[][] v = getEventHorizonGrid()[1];
		int m = TileEntityRenderStargate.eventHorizonGridRadialSize;
		int n = TileEntityRenderStargate.eventHorizonGridPolarSize;
		int i = random.nextInt(m - 1) + 1;
		int j = random.nextInt(n) + 1;
		v[j][i] += 0.02 * random.nextGaussian();
	}

	public void updateEventHorizon() {
		double[][][] grid = getEventHorizonGrid();
		double[][] u = grid[0];
		double[][] v = grid[1];
		int m = TileEntityRenderStargate.eventHorizonGridRadialSize;
		int n = TileEntityRenderStargate.eventHorizonGridPolarSize;
		double dt = 1.0;
		double asq = 0.03;
		double d = 0.95;
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++) {
				double du_dr = 0.5 * (u[j][i + 1] - u[j][i - 1]);
				double d2u_drsq = u[j][i + 1] - 2 * u[j][i] + u[j][i - 1];
				double d2u_dthsq = u[j + 1][i] - 2 * u[j][i] + u[j - 1][i];
				v[j][i] = d * v[j][i] + (asq * dt) * (d2u_drsq + du_dr / i + d2u_dthsq / (i * i));
			}
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++)
				u[j][i] += v[j][i] * dt;
		double u0 = 0, v0 = 0;
		for (int j = 1; j <= n; j++) {
			u0 += u[j][1];
			v0 += v[j][1];
		}
		u0 /= n;
		v0 /= n;
		for (int j = 1; j <= n; j++) {
			u[j][0] = u0;
			v[j][0] = v0;
		}
	}

}
