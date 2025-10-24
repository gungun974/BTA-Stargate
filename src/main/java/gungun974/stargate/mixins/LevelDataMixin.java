package gungun974.stargate.mixins;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.core.StargateDematerializedManager;
import gungun974.stargate.core.StargateSessionManager;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelData.class, remap = false)
public class LevelDataMixin {
	@Inject(method = "updateTagCompound", at = @At("HEAD"))
	void saveStargateSessions(CompoundTag levelTag, CompoundTag playerTag, CallbackInfo ci) {
		levelTag.putCompound("StargateSessions", StargateSessionManager.getInstance().createNBTData());
		levelTag.putCompound("StargateDematerialized", StargateDematerializedManager.getInstance().createNBTData());
	}

	@Inject(method = "readFromCompoundTag", at = @At("HEAD"))
	void loadStargateSessions(CompoundTag tag, CallbackInfo ci) {
		StargateSessionManager.getInstance().loadNBTData(tag.getCompound("StargateSessions"));
		StargateDematerializedManager.getInstance().loadNBTData(tag.getCompound("StargateDematerialized"));
	}
}
