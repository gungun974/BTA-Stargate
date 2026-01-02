package gungun974.stargate.gate.items;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.blocks.BlockLogicStargate;
import gungun974.stargate.gate.blocks.BlockLogicStargateBuildPart;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemStargateWand extends Item {
	public ItemStargateWand(NamespaceID namespaceId, int id) {
		super(namespaceId, id);
	}

	@Override
	public ItemStack onUseItem(ItemStack itemstack, World world, Player player) {
		if (player.isSneaking()) {
			ItemStack newItemStack = itemstack.copy();
			newItemStack.setMetadata((itemstack.getMetadata() + 1) % 3);

			player.sendStatusMessage(getTranslatedName(newItemStack));

			return newItemStack;
		}

		return itemstack;
	}

	@Override
	public String getTranslatedName(ItemStack itemstack) {
		switch (itemstack.getMetadata()) {
			case 0:
				return I18n.getInstance().translateKey(itemstack.getItemKey() + ".milkyway.name");
			case 1:
				return I18n.getInstance().translateKey(itemstack.getItemKey() + ".pegasus.name");
			case 2:
				return I18n.getInstance().translateKey(itemstack.getItemKey() + ".universe.name");
		}
		return super.getTranslatedName(itemstack);
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
		if (player.isSneaking()) {
			return false;
		}

		int cx = blockX + side.getOffsetX();
		int cy = blockY + side.getOffsetY();
		int cz = blockZ + side.getOffsetZ();

		Direction orientation = player.getPlacementDirection(side).getOpposite();

		if (orientation.isHorizontal()) {
			if (!BlockLogicStargateBuildPart.canBuildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite())) {
				player.sendStatusMessage(I18n.getInstance().translateKey("item.stargate.stargate_wand.error"));
				return false;
			}
		} else {
			if (!BlockLogicStargateBuildPart.canBuildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite())) {
				player.sendStatusMessage(I18n.getInstance().translateKey("item.stargate.stargate_wand.error"));
				return false;
			}
		}

		if (orientation.isHorizontal()) {
			switch (itemstack.getMetadata()) {
				case 0:
					BlockLogicStargateBuildPart.buildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), StargateFamily.MilkyWay);
					break;
				case 1:
					BlockLogicStargateBuildPart.buildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), StargateFamily.Pegasus);
					break;
				case 2:
					BlockLogicStargateBuildPart.buildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), StargateFamily.Universe);
					break;
			}
		} else if (orientation == Direction.DOWN) {
			switch (itemstack.getMetadata()) {
				case 0:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), Direction.DOWN, StargateFamily.MilkyWay);
					break;
				case 1:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), Direction.DOWN, StargateFamily.Pegasus);
					break;
				case 2:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), Direction.DOWN, StargateFamily.Universe);
					break;
			}
		} else {
			switch (itemstack.getMetadata()) {
				case 0:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), Direction.UP, StargateFamily.MilkyWay);
					break;
				case 1:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), Direction.UP, StargateFamily.Pegasus);
					break;
				case 2:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).getOpposite(), Direction.UP, StargateFamily.Universe);
					break;
			}
		}

		return true;
	}

	@Override
	public boolean beforeDestroyBlock(World world, ItemStack itemStack, int blockId, int x, int y, int z, Side side, Player player) {
		BlockLogicStargate blockLogicStargate = world.getBlockLogic(x, y, z, BlockLogicStargate.class);
		if (blockLogicStargate == null) {
			return true;
		}

		TileEntityStargate stargate = blockLogicStargate.findMainTileEntityStargate(world, x, y, z);
		if (stargate == null) {
			return true;
		}

		StargateComponent stargateComponent = stargate.getStargateComponent();

		if (stargateComponent == null) {
			return true;
		}

		stargateComponent.destroyGate();

		return true;
	}
}
