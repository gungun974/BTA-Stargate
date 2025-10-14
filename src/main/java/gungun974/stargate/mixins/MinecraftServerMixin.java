package gungun974.stargate.mixins;

import gungun974.stargate.IWorldDirNameAccess;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, remap = false)
public abstract class MinecraftServerMixin implements IWorldDirNameAccess {
	@Unique
	public String worldDirName;

	@Inject(
		method = "initWorld",
		at = @At("HEAD")
	)
	private void rememberWorldDirName(ISaveFormat saveFormat, String worldDirName, long l, CallbackInfo ci) {
		this.worldDirName = worldDirName;
	}

	@Override
	public String bta_stargate$getWorldDirName() {
		return worldDirName;
	}
}

