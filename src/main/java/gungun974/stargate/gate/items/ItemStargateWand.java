package gungun974.stargate.gate.items;

import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.blocks.BlockLogicStargate;
import gungun974.stargate.gate.blocks.BlockLogicStargateBuildPart;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStargateWand extends Item {
	public ItemStargateWand(@NotNull NamespaceID namespaceId, @NotNull String translationKey, int id) {
		super(namespaceId, translationKey, id);
	}

	@Override
	public @Nullable ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (player.isSneaking()) {
			ItemStack newItemStack = selfStack.copy();
			newItemStack.setMetadata((selfStack.getMetadata() + 1) % 3);

			player.sendStatusMessage(getTranslatedName(newItemStack));

			return newItemStack;
		}

		return selfStack;
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
	public boolean onUseOnBlock(@NotNull ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
		if (player.isSneaking()) {
			return false;
		}

		int cx = blockPos.x() + side.offsetX();
		int cy = blockPos.y() + side.offsetY();
		int cz = blockPos.z() + side.offsetZ();

		Direction orientation = player.getPlacementDirection(side).opposite();

		if (orientation.isHorizontal()) {
			if (!BlockLogicStargateBuildPart.canBuildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite())) {
				player.sendStatusMessage(I18n.getInstance().translateKey("item.stargate.stargate_wand.error"));
				return false;
			}
		} else {
			if (!BlockLogicStargateBuildPart.canBuildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite())) {
				player.sendStatusMessage(I18n.getInstance().translateKey("item.stargate.stargate_wand.error"));
				return false;
			}
		}

		if (orientation.isHorizontal()) {
			switch (selfStack.getMetadata()) {
				case 0:
					BlockLogicStargateBuildPart.buildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), StargateFamily.MilkyWay);
					break;
				case 1:
					BlockLogicStargateBuildPart.buildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), StargateFamily.Pegasus);
					break;
				case 2:
					BlockLogicStargateBuildPart.buildVerticalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), StargateFamily.Universe);
					break;
			}
		} else if (orientation == Direction.DOWN) {
			switch (selfStack.getMetadata()) {
				case 0:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), Direction.DOWN, StargateFamily.MilkyWay);
					break;
				case 1:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), Direction.DOWN, StargateFamily.Pegasus);
					break;
				case 2:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), Direction.DOWN, StargateFamily.Universe);
					break;
			}
		} else {
			switch (selfStack.getMetadata()) {
				case 0:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), Direction.UP, StargateFamily.MilkyWay);
					break;
				case 1:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), Direction.UP, StargateFamily.Pegasus);
					break;
				case 2:
					BlockLogicStargateBuildPart.buildHorizontalStargate(world, cx, cy, cz, player.getHorizontalPlacementDirection(side).opposite(), Direction.UP, StargateFamily.Universe);
					break;
			}
		}

		return true;
	}

	@Override
	public boolean beforeBlockDestroyed(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player, @NotNull Block block, @NotNull TilePosc blockPos, @NotNull Side side) {
		BlockLogicStargate blockLogicStargate = world.getBlockLogic(blockPos, BlockLogicStargate.class);
		if (blockLogicStargate == null) {
			return true;
		}

		TileEntityStargate stargate = blockLogicStargate.findMainTileEntityStargate(world, blockPos);
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
