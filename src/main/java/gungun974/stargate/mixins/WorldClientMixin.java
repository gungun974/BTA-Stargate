package gungun974.stargate.mixins;

import gungun974.stargate.core.StargateSessionManager;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = WorldClient.class, remap = false)
public class WorldClientMixin extends World {
	public void tick() {
		StargateSessionManager.getInstance().tick();
		super.tick();
	}
}
