package gungun974.stargate.mixins;

import gungun974.stargate.core.StargateSessionManager;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.halplibe.helper.EnvironmentHelper;

@Mixin(value = World.class, remap = false)
public class WorldMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (!EnvironmentHelper.isSinglePlayer()) {
			return;
		}
		StargateSessionManager.getInstance().tick();
	}
}
