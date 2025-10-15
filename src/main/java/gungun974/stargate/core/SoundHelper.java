package gungun974.stargate.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.SoundRepository;
import net.minecraft.core.sound.SoundCategory;
import turniplabs.halplibe.helper.EnvironmentHelper;

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

		Minecraft.getMinecraft().sndManager.playSoundWithIdAtPos(SoundRepository.SOUNDS.getSoundEntry(name), category, x, y, z, volume, pitch, String.format("stargate_%f_%f_%f", x, y, z));
	}

	static public void stopSingleSoundAt(String name, SoundCategory category, float x, float y, float z) {
		if (EnvironmentHelper.isServerEnvironment()) {
			return;
		}

		Minecraft.getMinecraft().sndManager.playSoundWithIdAtPos(SoundRepository.SOUNDS.getSoundEntry(name), category, x, y, z, 0f, 1.0f, String.format("stargate_%f_%f_%f", x, y, z));
	}
}
