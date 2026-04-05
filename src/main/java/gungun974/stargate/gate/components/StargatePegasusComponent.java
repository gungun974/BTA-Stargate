package gungun974.stargate.gate.components;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.core.StargatePegasusAddress;
import gungun974.stargate.core.StargateState;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.sound.SoundCategory;

public class StargatePegasusComponent extends StargateComponent {
	public final static double symbolAngle = 360.0 / StargatePegasusAddress.NUMBER_OF_SYMBOL;
	int currentSymbol = 0;
	boolean ringMoveShouldGoCenterFirst = false;

	public StargatePegasusComponent(TileEntityStargate stargateTile) {
		super(stargateTile);
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.Pegasus;
	}

	@Override
	protected void updateRotation() {
		if (!lastRingMove && ringMove) {
			playSoundAtCenter("stargate:stargate.pegasus.roll", SoundCategory.WORLD_SOUNDS, 1.0f, 1.0f, false);
		}

		if (lastRingMove && !ringMove) {
			stopSoundAtCenter("stargate:stargate.pegasus.roll");
		}

		lastRingMove = ringMove;
		lastAngle = currentAngle;

		double angleDistance = angularDistance(targetAngle, currentAngle);

		if (ringMoveShouldGoCenterFirst) {
			angleDistance += 360;
		}

		if (angleDistance < 0.01) {
			ringMove = false;
			return;
		}

		ringMove = true;

		double angleSpeed = Math.min(angleDistance, 10.5);

		if (ringDirection) {
			currentAngle += angleSpeed;
		} else {
			currentAngle -= angleSpeed;
		}

		if (ringMoveShouldGoCenterFirst) {
			double normalizedLast = ((lastAngle % 360) + 360) % 360;
			double normalizedCurrent = ((currentAngle % 360) + 360) % 360;

			if ((normalizedLast > 270 && normalizedCurrent < 90) || (normalizedLast < 90 && normalizedCurrent > 270)) {
				ringMoveShouldGoCenterFirst = false;
			}
		}
	}

	@Override
	public int getCurrentSymbol() {
		return currentSymbol;
	}

	public void encode() {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}

			int currentSegment = ((int) Math.round((((currentAngle % 360) + 360) % 360) / symbolAngle)) % StargatePegasusAddress.NUMBER_OF_SYMBOL;

			if (currentSegment % 4 != 0) {
				return;
			}

			int currentSymbol = getCurrentSymbol();

			for (int i = 0; i < currentDialingAddressSize; i++) {
				if (currentDialingAddress[i] == currentSymbol) {
					return;
				}
			}

			currentDialingAddress[currentDialingAddressSize] = currentSymbol;
			currentDialingAddressSize += 1;

			if (currentSymbol == 0 || currentDialingAddressSize == 9) {
				state = StargateState.AWAIT;
			} else {
				state = StargateState.DIALLING;
			}

			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}

	@Override
	public void moveToSymbol(int symbol) {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}

			final int[] ORDER = {1, 2, 3, 6, 7, 8, 4, 5, 0};

			if (currentDialingAddressSize >= ORDER.length) {
				return;
			}

			state = StargateState.DIALLING;
			currentSymbol = symbol;

			int targetChevron = ORDER[currentDialingAddressSize];

			if (symbol == 0) {
				targetChevron = 0;
			}

			ringMoveShouldGoCenterFirst = targetChevron != 0;
			ringDirection = currentDialingAddressSize % 2 == 1;

			lastAngle = currentAngle;
			targetAngle = targetChevron * 4 * symbolAngle;

			if (stargateTile.worldObj != null) {
				stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
			}
		});
	}

	@Override
	public void fastEncode(int symbol) {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}

			for (int i = 0; i < currentDialingAddressSize; i++) {
				if (currentDialingAddress[i] == symbol) {
					return;
				}
			}

			int sourceChevron = 0;

			switch (currentDialingAddressSize) {
				case 0:
					sourceChevron = 1;
					break;
				case 1:
					sourceChevron = 2;
					break;
				case 2:
					sourceChevron = 3;
					break;
				case 3:
					sourceChevron = 6;
					break;
				case 4:
					sourceChevron = 7;
					break;
				case 5:
					sourceChevron = 8;
					break;
				case 6:
					sourceChevron = 4;
					break;
				case 7:
					sourceChevron = 5;
					break;
			}

			if (symbol == 0) {
				sourceChevron = 0;
			}

			currentAngle = sourceChevron * 4 * symbolAngle;
			lastAngle = currentAngle;
			targetAngle = currentAngle;

			currentDialingAddress[currentDialingAddressSize] = symbol;
			currentDialingAddressSize += 1;


			if (symbol == 0 || currentDialingAddressSize == 9) {
				state = StargateState.AWAIT;
			} else {
				state = StargateState.DIALLING;
			}

			currentSymbol = symbol;

			stopSoundAtCenter("stargate:stargate.pegasus.roll");
			playAnimation(StargateAnimation.FAST_ENCODE_CHEVRON);
		});
	}
}
