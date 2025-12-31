package gungun974.stargate.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin {
	@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;setBlockWithNotify(IIII)Z"))
	boolean removeCamouflageBlock(World world, int x, int y, int z, int id, Operation<Boolean> original) {
		TileEntity entity = world.getTileEntity(x, y, z);
		if (!(entity instanceof TileEntityStargate)) {
			return original.call(world, x, y, z, id);
		}

		CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

		if (!camouflageComponent.hasCamouflage()) {
			return original.call(world, x, y, z, id);
		}

		world.notifyBlockChange(x, y, z, entity.getBlockId());

		return true;
	}
}
