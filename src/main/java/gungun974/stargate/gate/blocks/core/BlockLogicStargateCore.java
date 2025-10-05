package gungun974.stargate.gate.blocks.core;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockLogicStargateCore extends BlockLogicRotatable {
	public BlockLogicStargateCore(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
		super.onBlockPlacedByMob(world, x, y, z, side, mob, xPlaced, yPlaced);
		TileEntityStargateCore stargateCore = TileEntityStargateCore.findStargateCore(world, x, y, z);
		if (stargateCore != null) {
			stargateCore.checkAndAssemble();
		}
	}

	@Override
	public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
		super.onBlockPlacedOnSide(world, x, y, z, side, xPlaced, yPlaced);
		TileEntityStargateCore stargateCore = TileEntityStargateCore.findStargateCore(world, x, y, z);
		if (stargateCore != null) {
			stargateCore.checkAndAssemble();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		TileEntityStargateCore stargateCore = TileEntityStargateCore.findStargateCore(world, x, y, z);
		if (stargateCore != null) {
			stargateCore.checkAndAssemble();
		}
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		TileEntityStargateCore stargateCore = TileEntityStargateCore.findStargateCore(world, x, y, z);
		if (stargateCore != null) {
			stargateCore.checkAndAssemble();
		}
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}
}
