package gungun974.stargate.core;

import gungun974.stargate.StargateMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.SoundCategoryHelper;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundRepository;
import net.minecraft.core.sound.SoundCategory;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.net.URL;

public class SoundHelper {
	private static int idCounter = 0;

	static public void playShortSoundAt(String name, SoundCategory category, float x, float y, float z, float volume, float pitch) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		idCounter = (idCounter + 1) % 256;
		String soundName = "stargate_sound_" + idCounter;

		playSoundWithIdAtPos(name, category, x, y, z, volume, pitch, soundName, false);
	}

	static public void playSingleSoundAt(String name, SoundCategory category, float x, float y, float z, float volume, float pitch) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		String id = String.format("stargate_%f_%f_%f_%s", x, y, z, name);

		playSoundWithIdAtPos(name, category, x, y, z, volume, pitch, id, false);
	}

	static public void playSingleSoundAtWithLoop(String name, SoundCategory category, float x, float y, float z, float volume, float pitch) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		String id = String.format("stargate_%f_%f_%f_%s", x, y, z, name);

		playSoundWithIdAtPos(name, category, x, y, z, volume, pitch, id, true);
	}

	private static void playSoundWithIdAtPos(String name, SoundCategory category, float x, float y, float z, float volume, float pitch, String id, boolean loop) {
		SoundEntry entry = SoundRepository.SOUNDS.getSoundEntry(name);

		Object soundSystem = getSoundSystem();

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
					Class<?> ss = soundSystem.getClass();
					if (entry.shouldStream) {
						ss.getMethod("newStreamingSource", boolean.class, String.class, URL.class, String.class, boolean.class, float.class, float.class, float.class, int.class, float.class)
							.invoke(soundSystem, volume > 1.0F, id, url, entry.name, loop, x, y, z, 2, soundDistance);
					} else {
						ss.getMethod("newSource", boolean.class, String.class, URL.class, String.class, boolean.class, float.class, float.class, float.class, int.class, float.class)
							.invoke(soundSystem, volume > 1.0F, id, url, entry.name, loop, x, y, z, 2, soundDistance);
					}

					if (volume > 1.0F) {
						volume = 1.0F;
					}

					ss.getMethod("setPitch", String.class, float.class).invoke(soundSystem, id, pitch * entry.pitch);
					ss.getMethod("setVolume", String.class, float.class).invoke(soundSystem, id, volume * SoundCategoryHelper.getEffectiveVolume(category) * entry.volume);
					ss.getMethod("play", String.class).invoke(soundSystem, id);
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

		stopSoundId(id);
	}

	static public void stopSoundId(String id) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		Object soundSystem = getSoundSystem();

		if (soundSystem == null) {
			return;
		}

		try {
			soundSystem.getClass().getMethod("stop", String.class).invoke(soundSystem, id);
		} catch (Exception e) {
			StargateMod.LOGGER.error("Unexpected exception while stopping sound '{}'!", id, e);
		}
	}

	private static Object getSoundSystem() {
		try {
			return SoundEngine.class.getMethod("getSoundSystem").invoke(null);
		} catch (Exception e) {
			return null;
		}
	}
}
