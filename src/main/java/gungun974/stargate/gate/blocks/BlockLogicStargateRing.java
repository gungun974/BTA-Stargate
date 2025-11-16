package gungun974.stargate.gate.blocks;

import gungun974.stargate.gate.tiles.TileEntityStargateCore;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;

public class BlockLogicStargateRing extends BlockLogic {
	public BlockLogicStargateRing(Block<?> block, Material material) {
		super(block, material);
	}

	public String getLanguageKey(int meta) {
		if (meta == 1) {
			return this.block.getKey().replace(".ring", ".chevron");
		}
		return this.block.getKey();
	}

	@Override
	public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
		if (stack.getMetadata() == 1) {
			return 1;
		}
		return 0;
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
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		if (EnvironmentHelper.isClientWorld()) {
			return false;
		}

		TileEntityStargateCore stargateCore = TileEntityStargateCore.findStargateCore(world, x, y, z);
		if (stargateCore != null && stargateCore.isAssembled()) {
			stargateCore.autoDial();
			return true;
		}
		return false;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}
}
