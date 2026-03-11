package gungun974.stargate.dhd.blocks;

import gungun974.stargate.dhd.tiles.TileEntityDHD;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicDHD extends BlockLogicRotatable {
	public BlockLogicDHD(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);

		((TileEntityDHD) tileEntity).dial();

		return true;
	}
}
