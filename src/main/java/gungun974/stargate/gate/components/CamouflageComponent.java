package gungun974.stargate.gate.components;

import com.mojang.nbt.tags.CompoundTag;

public class CamouflageComponent {
	private int blockId = 0;
	private int blockMeta = 0;

	public CamouflageComponent() {
	}

	public boolean hasCamouflage() {
		return this.blockId != 0;
	}

	public void setCamouflage(int blockId, int blockMeta) {
		this.blockId = blockId;
		this.blockMeta = blockMeta;
	}

	public void clearCamouflage() {
		this.blockId = 0;
		this.blockMeta = 0;
	}

	public void readFromNBT(CompoundTag compoundTag) {
		blockId = compoundTag.getIntegerOrDefault("CamouflageBlockId", blockId);
		blockMeta = compoundTag.getIntegerOrDefault("CamouflageMetaId", blockMeta);
	}


	public void writeToNBT(CompoundTag compoundTag) {
		compoundTag.putInt("CamouflageBlockId", blockId);
		compoundTag.putInt("CamouflageMetaId", blockMeta);
	}

	public int getBlockId() {
		return blockId;
	}

	public int getBlockMeta() {
		return blockMeta;
	}
}
