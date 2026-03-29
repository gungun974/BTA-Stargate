package gungun974.stargate.mixins;

import gungun974.stargate.StargateItems;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.crafting.LookupFuelFurnaceBlast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LookupFuelFurnaceBlast.class, remap = false)
public abstract class LookupFuelFurnaceBlastMixin extends LookupFuelFurnace {
	@Inject(method = "register", at = @At("TAIL"))
	void register(CallbackInfo ci) {
		addFuelEntry(StargateItems.NAQUADAH.id, 12800);
	}
}
