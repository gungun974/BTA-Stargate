package gungun974.stargate.core;

import com.mojang.nbt.tags.CompoundTag;

public record StargateDematerializedEntity(int destinationX, int destinationY, int destinationZ, int destinationDim,
										   CompoundTag dematerializedData, String entityId, String passengerId) {
}
