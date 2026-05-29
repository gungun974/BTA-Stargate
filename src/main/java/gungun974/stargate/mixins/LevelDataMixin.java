package gungun974.stargate.mixins;

import com.mojang.nbt.tags.CompoundTag;
import gungun974.stargate.core.StargateDematerializedManager;
import gungun974.stargate.core.StargateNetworkManager;
import gungun974.stargate.core.StargateSessionManager;
import net.minecraft.core.world.save.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LevelData.class, remap = false)
public class LevelDataMixin {
	@Inject(method = "serialize", at = @At("HEAD"))
	private static void saveStargateSessions(LevelData levelData, CompoundTag out, CallbackInfoReturnable<CompoundTag> cir) {
		out.putCompound("StargateNetworks", StargateNetworkManager.getInstance().createNBTData());
		out.putCompound("StargateSessions", StargateSessionManager.getInstance().createNBTData());
		out.putCompound("StargateDematerialized", StargateDematerializedManager.getInstance().createNBTData());
	}

	@Inject(method = "deserialize", at = @At("HEAD"))
	private static void loadStargateSessions(CompoundTag tag, CallbackInfoReturnable<LevelData> cir) {
		StargateNetworkManager.getInstance().loadNBTData(tag.getCompound("StargateNetworks"));
		StargateSessionManager.getInstance().loadNBTData(tag.getCompound("StargateSessions"));
		StargateDematerializedManager.getInstance().loadNBTData(tag.getCompound("StargateDematerialized"));
	}
}
