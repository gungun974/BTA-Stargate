package gungun974.stargate.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import gungun974.stargate.StargateBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.menu.MenuInventoryCreative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = MenuInventoryCreative.class, remap = false)
public class MenuInventoryCreativeMixin {
	@Shadow
	public static List<ItemStack> creativeItems;

	@Shadow
	public static int creativeItemsCount;
	@Unique
	private static int extraCount = 0;

	@Inject(
		method = "<clinit>",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/Block;hasTag(Lnet/minecraft/core/data/tag/Tag;)Z")

	)
	private static void addBlocks(CallbackInfo ci, @Local(name = "id") int i) {
		Block<?> block = Blocks.blocksList[i];

		if (block == null) {
			return;
		}

		if (block.id() == StargateBlocks.STARGATE_BUILD_PART_MILKYWAY.id()) {
			int before = creativeItems.size();
			creativeItems.add(new ItemStack(block, 1, 1));
			creativeItems.add(new ItemStack(block, 1, 2));
			extraCount += creativeItems.size() - before;
		}

		if (block.id() == StargateBlocks.STARGATE_BUILD_PART_PEGASUS.id()) {
			int before = creativeItems.size();
			creativeItems.add(new ItemStack(block, 1, 1));
			creativeItems.add(new ItemStack(block, 1, 2));
			extraCount += creativeItems.size() - before;
		}

		if (block.id() == StargateBlocks.STARGATE_BUILD_PART_UNIVERSE.id()) {
			int before = creativeItems.size();
			creativeItems.add(new ItemStack(block, 1, 1));
			creativeItems.add(new ItemStack(block, 1, 2));
			extraCount += creativeItems.size() - before;
		}
	}

	@Inject(
		method = "<clinit>",
		at = @At(value = "FIELD", target = "Lnet/minecraft/core/player/inventory/menu/MenuInventoryCreative;creativeItemsCount:I", shift = At.Shift.AFTER)
	)
	private static void addUpItemCount(CallbackInfo ci) {
		creativeItemsCount += extraCount;
	}
}
