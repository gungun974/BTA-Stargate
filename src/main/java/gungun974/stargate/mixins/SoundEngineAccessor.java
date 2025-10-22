package gungun974.stargate.mixins;

import net.minecraft.client.sound.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulscode.sound.SoundSystem;

@Mixin(value = SoundEngine.class, remap = false)
public interface SoundEngineAccessor {
	@Accessor("soundSystem")
	SoundSystem getSoundSystem();
}
