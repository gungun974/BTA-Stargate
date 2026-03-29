package gungun974.stargate;

import gungun974.stargate.cc.CCPlugin;
import gungun974.stargate.network.server.PlayerEnterStargateMessage;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.sound.SoundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;


public class StargateMod implements ModInitializer, GameStartEntrypoint {
	public static final String MOD_ID = "stargate";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static int startBlockID = 2800;
	public static int startItemID = 19700;


	@Override
	public void onInitialize() {
		LOGGER.info("Stargate initialized.");
		StargateBlocks.RegisterBlocks();
		StargateItems.RegisterItems();

		NetworkHandler.registerNetworkMessage(PlayerEnterStargateMessage::new);

		try {
			Class.forName("dan200.computercraft.api.ComputerCraftAPI");
			registerCCPlugin();
		} catch (ClassNotFoundException ignored) {
		}
	}

	@Override
	public void beforeGameStart() {
		if (!EnvironmentHelper.isServerEnvironment()) {
			beforeGameStartClient();
		}
	}

	public void beforeGameStartClient() {
		SoundRepository.registerNamespace(MOD_ID);
	}

	@Override
	public void afterGameStart() {

	}

	private void registerCCPlugin() {
		CCPlugin.register();
	}

}
