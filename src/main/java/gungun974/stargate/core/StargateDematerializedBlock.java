package gungun974.stargate.core;

import net.minecraft.core.block.entity.TileEntity;

import javax.annotation.Nullable;

public class StargateDematerializedBlock {
	final public int destinationX;
	final public int destinationY;
	final public int destinationZ;
	final public int destinationDim;

	final public int dematerializedId;
	final public int dematerializedMeta;
	@Nullable
	final public TileEntity dematerializedTile;
	final public int dematerializedX;
	final public int dematerializedY;
	final public int dematerializedZ;

	public StargateDematerializedBlock(
		int destinationX,
		int destinationY,
		int destinationZ,
		int destinationDim,
		int dematerializedId,
		int dematerializedMeta,
		@Nullable TileEntity dematerializedTile,
		int dematerializedX,
		int dematerializedY,
		int dematerializedZ
	) {
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		this.destinationZ = destinationZ;
		this.destinationDim = destinationDim;
		this.dematerializedId = dematerializedId;
		this.dematerializedMeta = dematerializedMeta;
		this.dematerializedTile = dematerializedTile;
		this.dematerializedX = dematerializedX;
		this.dematerializedY = dematerializedY;
		this.dematerializedZ = dematerializedZ;
	}
}
