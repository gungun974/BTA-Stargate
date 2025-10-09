package gungun974.stargate.gate.blocks.core;

public enum StargateAnimation {
	NONE(0),
	ENCODE_CHEVRON(48),
	FAST_ENCODE_CHEVRON(8);

	final int duration;

	StargateAnimation(int duration) {
		this.duration = duration;
	}
}
