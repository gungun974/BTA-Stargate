package gungun974.stargate.gate.renders;

enum TextureIndex {
	RING_FACE(1),
	RING(0),
	RING_SYMBOL(32),
	CHEVRON(3),
	CHEVRON_LIT(2);

	public final int index;

	TextureIndex(int index) {
		this.index = index;
	}
}
