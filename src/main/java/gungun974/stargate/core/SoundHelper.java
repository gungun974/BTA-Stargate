package gungun974.stargate.core;

import gungun974.stargate.StargateMod;
import gungun974.stargate.mixins.SoundEngineAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.SoundCategoryHelper;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundRepository;
import net.minecraft.core.sound.SoundCategory;
import paulscode.sound.SoundSystem;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.net.URL;

public class SoundHelper {
	static public void playShortSoundAt(String name, SoundCategory category, float x, float y, float z, float volume, float pitch) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		Minecraft.getMinecraft().sndManager.playSoundAt(name, category, x, y, z, volume, pitch);
	}

	static public void playSingleSoundAt(String name, SoundCategory category, float x, float y, float z, float volume, float pitch) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		String id = String.format("stargate_%f_%f_%f_%s", x, y, z, name);

		playLongSoundWithIdAtPos(name, category, x, y, z, volume, pitch, id, false);
	}

	static public void playSingleSoundAtWithLoop(String name, SoundCategory category, float x, float y, float z, float volume, float pitch) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		String id = String.format("stargate_%f_%f_%f_%s", x, y, z, name);

		playLongSoundWithIdAtPos(name, category, x, y, z, volume, pitch, id, true);
	}

	private static void playLongSoundWithIdAtPos(String name, SoundCategory category, float x, float y, float z, float volume, float pitch, String id, boolean loop) {
		SoundEntry entry = SoundRepository.SOUNDS.getSoundEntry(name);

		SoundSystem soundSystem = ((SoundEngineAccessor) Minecraft.getMinecraft().sndManager).getSoundSystem();

		if (soundSystem == null) {
			return;
		}

		try {
			if (entry == null) {
				return;
			}

			float soundDistance = (float) entry.attenuationDistance;
			if (volume > 1.0F) {
				soundDistance *= volume;
			}

			if (entry.type == SoundEntry.Type.FILE) {
				URL url = entry.getURL();
				if (url != null) {
					if (entry.shouldStream) {
						soundSystem.newStreamingSource(volume > 1.0F, id, url, entry.name, loop, x, y, z, 2, soundDistance);
					} else {
						soundSystem.newSource(volume > 1.0F, id, url, entry.name, loop, x, y, z, 2, soundDistance);
					}

					if (volume > 1.0F) {
						volume = 1.0F;
					}

					soundSystem.setPitch(id, pitch * entry.pitch);
					soundSystem.setVolume(id, volume * SoundCategoryHelper.getEffectiveVolume(category, Minecraft.getMinecraft().gameSettings) * entry.volume);
					soundSystem.play(id);
				}
			}

			if (entry.parent.hasSubtitle()) {
				Minecraft.getMinecraft().subtitleTracker.heardSound(entry.parent, x, y, z);
			}
		} catch (Exception e) {
			StargateMod.LOGGER.error("Unexpected exception while playing sound '{}' at {} {} {}!", entry, x, y, z, e);
		}

	}

	static public void stopSingleSoundAt(String name, float x, float y, float z) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		String id = String.format("stargate_%f_%f_%f_%s", x, y, z, name);

		((SoundEngineAccessor) Minecraft.getMinecraft().sndManager).getSoundSystem().stop(id);
	}
}
