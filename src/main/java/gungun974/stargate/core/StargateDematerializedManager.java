package gungun974.stargate.core;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import gungun974.stargate.gate.blocks.core.TileEntityStargateCore;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StargateDematerializedManager {
	private static StargateDematerializedManager instance;

	private final List<StargateDematerializedEntity> dematerializedEntities = new ArrayList<>();

	private StargateDematerializedManager() {
	}

	public static synchronized StargateDematerializedManager getInstance() {
		if (instance == null) {
			instance = new StargateDematerializedManager();
		}

		return instance;
	}

	public @NotNull CompoundTag createNBTData() {
		CompoundTag rootTag = new CompoundTag();

		CompoundTag dematerializedEntitiesTag = new CompoundTag();

		for (int i = 0; i < dematerializedEntities.size(); i++) {
			StargateDematerializedEntity dematerializedEntity = dematerializedEntities.get(i);

			CompoundTag dematerializedEntityTag = new CompoundTag();

			dematerializedEntityTag.putInt("DestinationX", dematerializedEntity.destinationX);
			dematerializedEntityTag.putInt("DestinationY", dematerializedEntity.destinationY);
			dematerializedEntityTag.putInt("DestinationZ", dematerializedEntity.destinationZ);
			dematerializedEntityTag.putInt("DestinationDim", dematerializedEntity.destinationDim);

			dematerializedEntityTag.putCompound("Entity", dematerializedEntity.dematerializedData);

			dematerializedEntitiesTag.put(String.valueOf(i), dematerializedEntityTag);
		}

		rootTag.putCompound("Entities", dematerializedEntitiesTag);

		return rootTag;
	}


	public void loadNBTData(CompoundTag rootTag) {
		dematerializedEntities.clear();

		try {
			CompoundTag dematerializedEntitiesTag = rootTag.getCompound("Entities");

			for (Map.Entry<String, Tag<?>> entry : dematerializedEntitiesTag.getValue().entrySet()) {
				try {
					final Tag<?> tag = entry.getValue();
					if (!(tag instanceof CompoundTag)) {
						continue;
					}

					final CompoundTag dematerializedEntityTag = (CompoundTag) tag;

					dematerializedEntities.add(new StargateDematerializedEntity(
						dematerializedEntityTag.getInteger("DestinationX"),
						dematerializedEntityTag.getInteger("DestinationY"),
						dematerializedEntityTag.getInteger("DestinationZ"),
						dematerializedEntityTag.getInteger("DestinationDim"),
						dematerializedEntityTag.getCompound("Entity")
					));
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
	}

	public void dematerializeEntity(
		int destinationX,
		int destinationY,
		int destinationZ,
		int destinationDim,
		Entity entity
	) {
		CompoundTag dematerializedData = new CompoundTag();
		entity.save(dematerializedData);
		entity.remove();

		dematerializedEntities.add(new StargateDematerializedEntity(
			destinationX,
			destinationY,
			destinationZ,
			destinationDim,
			dematerializedData
		));
	}

	public void materializeEntities(TileEntityStargateCore gate) {
		if (gate == null) {
			return;
		}

		if (gate.worldObj == null) {
			return;
		}

		int dim = gate.worldObj.dimension.id;

		Iterator<StargateDematerializedEntity> iterator = dematerializedEntities.iterator();

		while (iterator.hasNext()) {
			StargateDematerializedEntity dematerializedEntity = iterator.next();

			if (gate.x != dematerializedEntity.destinationX || gate.y != dematerializedEntity.destinationY || gate.z != dematerializedEntity.destinationZ || dim != dematerializedEntity.destinationDim) {
				continue;
			}

			Entity materializedEntity = EntityDispatcher.createEntityFromNBT(dematerializedEntity.dematerializedData, gate.worldObj);
			if (materializedEntity == null) {
				iterator.remove();
				continue;
			}
			gate.worldObj.entityJoinedWorld(materializedEntity);

			iterator.remove();
		}

	}
}
