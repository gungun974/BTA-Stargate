package gungun974.stargate.mixins;

import gungun974.stargate.gate.tiles.TileEntityStargateCore;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = Chunk.class, remap = false)
public class ChunkMixin {
	@Shadow
	public Map<ChunkPosition, TileEntity> tileEntityMap;

	@Inject(method = "needsSaving", at = @At("TAIL"), cancellable = true)
	void forceStargateToSaveState(boolean saveImmediately, CallbackInfoReturnable<Boolean> cir) {
		for (Map.Entry<ChunkPosition, TileEntity> entry : this.tileEntityMap.entrySet()) {
			if (entry.getValue() instanceof TileEntityStargateCore) {
				cir.setReturnValue(true);
				return;
			}
		}
	}
}
