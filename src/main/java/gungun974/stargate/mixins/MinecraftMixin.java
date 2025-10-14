package gungun974.stargate.mixins;

import gungun974.stargate.IWorldDirNameAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.type.WorldTypeGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMixin implements IWorldDirNameAccess {
	@Unique
	public String worldDirName;

	@Inject(
		method = "startWorld(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/core/world/type/WorldTypeGroups$Group;)V",
		at = @At("HEAD")
	)
	private void rememberWorldDirName(String worldDirName, String worldName, long seed, WorldTypeGroups.Group worldTypeGroup, CallbackInfo ci) {
		this.worldDirName = worldDirName;
	}

	@Override
	public String bta_stargate$getWorldDirName() {
		return worldDirName;
	}
}

