package gungun974.stargate.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin {
	@WrapOperation(
		method = "destroyBlock",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;setBlockTypeNotify(Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/block/Block;)Z")
	)
	boolean removeCamouflageBlock(World world, TilePosc tilePos, Block block, Operation<Boolean> original) {
		TileEntity entity = world.getTileEntity(tilePos);
		if (!(entity instanceof TileEntityStargate)) {
			return original.call(world, tilePos, block);
		}
		CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();
		if (!camouflageComponent.hasCamouflage()) {
			return original.call(world, tilePos, block);
		}
		world.notifyBlockChange(tilePos, entity.getBlock());
		return true;
	}
}
