package gungun974.stargate.core;

import net.minecraft.core.block.entity.TileEntity;
import org.jetbrains.annotations.Nullable;

public record StargateDematerializedBlock(int destinationX, int destinationY, int destinationZ, int destinationDim,
										  int dematerializedId, int dematerializedMeta,
										  @Nullable TileEntity dematerializedTile, int dematerializedX,
										  int dematerializedY, int dematerializedZ) {
}
