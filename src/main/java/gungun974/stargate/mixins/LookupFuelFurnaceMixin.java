package gungun974.stargate.mixins;

import gungun974.stargate.StargateItems;
import net.minecraft.core.crafting.LookupFuelFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LookupFuelFurnace.class, remap = false)
public abstract class LookupFuelFurnaceMixin {
	@Shadow
	public abstract void addFuelEntry(int id, int fuelYield);

	@Inject(method = "register", at = @At("TAIL"))
	void register(CallbackInfo ci) {
		addFuelEntry(StargateItems.NAQUADAH.id, 51200);
	}
}
