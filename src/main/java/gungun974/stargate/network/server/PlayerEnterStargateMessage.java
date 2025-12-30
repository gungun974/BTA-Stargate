package gungun974.stargate.network.server;

import gungun974.stargate.core.StargateSession;
import gungun974.stargate.core.StargateSessionManager;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.packet.PacketEntityFling;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import java.util.List;

public class PlayerEnterStargateMessage implements NetworkMessage {
	int gateX;
	int gateY;
	int gateZ;
	int gateDim;

	double dx;
	double dy;
	double dz;

	boolean enterFront;

	public PlayerEnterStargateMessage(int gateX, int gateY, int gateZ, int gateDim, double dx, double dy, double dz, boolean enterFront) {
		this.gateX = gateX;
		this.gateY = gateY;
		this.gateZ = gateZ;
		this.gateDim = gateDim;

		this.dx = dx;
		this.dy = dy;
		this.dz = dz;

		this.enterFront = enterFront;
	}

	public PlayerEnterStargateMessage() {
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket universalPacket) {
		universalPacket.writeInt(gateX);
		universalPacket.writeInt(gateY);
		universalPacket.writeInt(gateZ);
		universalPacket.writeInt(gateDim);

		universalPacket.writeDouble(dx);
		universalPacket.writeDouble(dy);
		universalPacket.writeDouble(dz);

		universalPacket.writeBoolean(enterFront);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket universalPacket) {
		gateX = universalPacket.readInt();
		gateY = universalPacket.readInt();
		gateZ = universalPacket.readInt();
		gateDim = universalPacket.readInt();

		dx = universalPacket.readDouble();
		dy = universalPacket.readDouble();
		dz = universalPacket.readDouble();

		enterFront = universalPacket.readBoolean();
	}

	@Override
	@Environment(EnvType.SERVER)
	public void handleServerEnv(NetworkContext context) {
		World world = MinecraftServer.getInstance().getDimensionWorld(gateDim);

		if (world == null) {
			return;
		}

		TileEntity tileEntity = world.getTileEntity(gateX, gateY, gateZ);

		if (!(tileEntity instanceof TileEntityStargate)) {
			return;
		}

		StargateComponent gate = ((TileEntityStargate) tileEntity).getStargateComponent();

		if (gate == null) {
			return;
		}

		StargateSession session = StargateSessionManager.getInstance().getSession(gate);

		if (session == null) {
			return;
		}

		AABB detectionBox = gate.getDetectionBox();
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, detectionBox);

		boolean invalidPlayer = true;

		for (Entity entity : list) {
			if (entity == context.player) {
				invalidPlayer = false;
				break;
			}
		}

		if (invalidPlayer) {
			return;
		}

		if (
			(session.destinationX == gateX && session.destinationY == gateY && session.destinationZ == gateZ) ||
				!enterFront
		) {
			//SoundHelper.playShortSoundAt("stargate:stargate.eventHorizon.enter", SoundCategory.WORLD_SOUNDS, (float) entity.x, (float) entity.y, (float) entity.z, 1.0f, 1.0f);
			context.player.killPlayer();
		} else {
			context.player.xd = dx;
			context.player.yd = dy;
			context.player.zd = dz;

			gate.teleportEntity(context.player, session);

			((PlayerServer) context.player).playerNetServerHandler.sendPacket(new PacketEntityFling(context.player.id, context.player.xd, context.player.yd, context.player.zd, 0, 0));

			StargateSessionManager.getInstance().endSession(gate);
		}
	}
}
