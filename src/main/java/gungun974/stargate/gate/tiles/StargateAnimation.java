package gungun974.stargate.gate.tiles;

public enum StargateAnimation {
	NONE(0),
	ENCODE_CHEVRON(48),
	FAST_ENCODE_CHEVRON(8),
	UNIVERSE_START(20),
	UNIVERSE_ENCODE_CHEVRON(30),
	UNIVERSE_FAST_ENCODE_CHEVRON(16),
	KAWOOSH(56),
	CLOSING(56),
	CANCEL(40);

	final int duration;

	StargateAnimation(int duration) {
		this.duration = duration;
	}
}
