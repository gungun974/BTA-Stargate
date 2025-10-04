package gungun974.stargate.gate.blocks.ring;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicStargateRing extends BlockLogic {
	public BlockLogicStargateRing(Block<?> block, Material material) {
		super(block, material);
	}

	public String getLanguageKey(int meta) {
		if (meta == 1) {
			return "tile.stargate.stargate.chevron";
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
}
