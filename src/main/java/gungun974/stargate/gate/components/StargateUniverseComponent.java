package gungun974.stargate.gate.components;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.core.StargateState;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.sound.SoundCategory;

public class StargateUniverseComponent extends StargateComponent {
	public final static double symbolAngle = 360.0 / 54;
	private boolean shouldResetRingDirection = false;

	public StargateUniverseComponent(TileEntity stargateTile) {
		super(stargateTile);
	}

	@Override
	public StargateFamily getFamily() {
		return StargateFamily.Universe;
	}

	@Override
	protected void updateRotation() {
		if (!lastRingMove && ringMove) {
			playSoundAtCenter("stargate:stargate.universe.roll", SoundCategory.WORLD_SOUNDS, 1.0f, 1.0f, false);
		}

		if (lastRingMove && !ringMove) {
			stopSoundAtCenter("stargate:stargate.universe.roll");
		}

		lastRingMove = ringMove;
		lastAngle = currentAngle;

		double angleDistance = angularDistance(targetAngle, currentAngle);

		if (angleDistance < 0.01) {
			ringMove = false;
			if (shouldResetRingDirection) {
				ringDirection = false;
				shouldResetRingDirection = false;
			}
			return;
		}

		ringMove = true;

		double angleSpeed = Math.min(angleDistance, 2);

		if (ringDirection) {
			currentAngle += angleSpeed;
		} else {
			currentAngle -= angleSpeed;
		}
	}

	public int getCurrentSymbol() {
		int targetPos = (int) Math.round(((((currentAngle - 10) % 360) + 360) % 360) / symbolAngle);

		int symbol = 0;
		int pos = 0;
		while (pos < targetPos) {
			symbol++;
			pos++;
			if (symbol % 4 == 0) {
				pos += 2;
			}
		}

		return symbol;
	}

	public void moveToSymbol(int symbol) {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}

			int targetPos = symbol;

			for (int i = 4; i <= symbol; i += 4) {
				targetPos += 2;
			}

			targetAngle = targetPos * symbolAngle + 10;

			state = StargateState.DIALLING;

			if (stargateTile.worldObj != null) {
				stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
			}
		});
	}

	public void encode() {
		commandQueue.add(() -> {
			if (!(state == StargateState.IDLE || state == StargateState.DIALLING)) {
				return;
			}

			int currentSymbol = getCurrentSymbol();

			for (int i = 0; i < currentDialingAddressSize; i++) {
				if (currentDialingAddress[i] == currentSymbol) {
					return;
				}
			}

			ringDirection = !ringDirection;
			currentDialingAddress[currentDialingAddressSize] = currentSymbol;
			currentDialingAddressSize += 1;

			if (currentSymbol == 0 || currentDialingAddressSize == 9) {
				state = StargateState.AWAIT;
			} else {
				state = StargateState.DIALLING;
			}

			playAnimation(StargateAnimation.UNIVERSE_ENCODE_CHEVRON);
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

			ringDirection = !ringDirection;
			currentDialingAddress[currentDialingAddressSize] = symbol;
			currentDialingAddressSize += 1;

			if (symbol == 0 || currentDialingAddressSize == 9) {
				state = StargateState.AWAIT;
			} else {
				state = StargateState.DIALLING;
			}

			stopSoundAtCenter("stargate:stargate.universe.roll");
			playAnimation(StargateAnimation.UNIVERSE_FAST_ENCODE_CHEVRON);
		});
	}

	@Override
	protected void resetGateDirection() {
		this.ringDirection = true;

		targetAngle = 0;
		shouldResetRingDirection = true;

		if (stargateTile.worldObj != null) {
			stargateTile.worldObj.markBlockNeedsUpdate(stargateTile.x, stargateTile.y, stargateTile.z);
		}
	}
}
