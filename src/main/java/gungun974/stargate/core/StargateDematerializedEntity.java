package gungun974.stargate.core;

import com.mojang.nbt.tags.CompoundTag;

public class StargateDematerializedEntity {
	final public int destinationX;
	final public int destinationY;
	final public int destinationZ;
	final public int destinationDim;

	final public CompoundTag dematerializedData;

	final public String entityId;
	final public String passengerId;

	public StargateDematerializedEntity(
		int destinationX,
		int destinationY,
		int destinationZ,
		int destinationDim,
		CompoundTag dematerializedData,
		String entityId,
		String passengerId
	) {
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		this.destinationZ = destinationZ;
		this.destinationDim = destinationDim;
		this.dematerializedData = dematerializedData;
		this.entityId = entityId;
		this.passengerId = passengerId;
	}
}
