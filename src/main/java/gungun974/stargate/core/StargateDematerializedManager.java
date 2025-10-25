package gungun974.stargate.core;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import gungun974.stargate.gate.blocks.core.TileEntityStargateCore;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StargateDematerializedManager {
	private static StargateDematerializedManager instance;

	private final List<StargateDematerializedEntity> dematerializedEntities = new ArrayList<>();
	private final List<StargateDematerializedBlock> dematerializedBlocks = new ArrayList<>();

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

	public void dematerializeBlock(
		int destinationX,
		int destinationY,
		int destinationZ,
		int destinationDim,
		World world,
		int sourceX,
		int sourceY,
		int sourceZ,
		int targetX,
		int targetY,
		int targetZ
	) {
		int id = world.getBlockId(sourceX, sourceY, sourceZ);
		int meta = world.getBlockMetadata(sourceX, sourceY, sourceZ);

		TileEntity tileEntity = world.getTileEntity(sourceX, sourceY, sourceZ);

		world.removeBlockTileEntity(sourceX, sourceY, sourceZ);
		world.setBlockWithNotify(sourceX, sourceY, sourceZ, 0);

		if (tileEntity != null) {
			tileEntity.validate();
		}

		dematerializedBlocks.add(new StargateDematerializedBlock(
			destinationX,
			destinationY,
			destinationZ,
			destinationDim,
			id,
			meta,
			tileEntity,
			targetX,
			targetY,
			targetZ
		));
	}

	public void materializeBlocks(TileEntityStargateCore gate) {
		if (gate == null) {
			return;
		}

		if (gate.worldObj == null) {
			return;
		}

		int dim = gate.worldObj.dimension.id;

		Iterator<StargateDematerializedBlock> iterator = dematerializedBlocks.iterator();

		while (iterator.hasNext()) {
			StargateDematerializedBlock dematerializedBlock = iterator.next();

			if (gate.x != dematerializedBlock.destinationX || gate.y != dematerializedBlock.destinationY || gate.z != dematerializedBlock.destinationZ || dim != dematerializedBlock.destinationDim) {
				continue;
			}

			int id = gate.worldObj.getBlockId(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ
			);

			int meta = gate.worldObj.getBlockMetadata(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ
			);

			if (id == dematerializedBlock.dematerializedId && meta == dematerializedBlock.dematerializedId && dematerializedBlock.dematerializedTile == null) {
				iterator.remove();
				continue;
			}

			gate.worldObj.setBlockAndMetadataRaw(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ,
				dematerializedBlock.dematerializedId,
				dematerializedBlock.dematerializedMeta
			);

			if (dematerializedBlock.dematerializedTile != null) {
				gate.worldObj.replaceBlockTileEntity(
					dematerializedBlock.dematerializedX,
					dematerializedBlock.dematerializedY,
					dematerializedBlock.dematerializedZ,
					dematerializedBlock.dematerializedTile
				);
			}

			gate.worldObj.notifyBlockChange(
				dematerializedBlock.dematerializedX,
				dematerializedBlock.dematerializedY,
				dematerializedBlock.dematerializedZ,
				dematerializedBlock.dematerializedId
			);

			iterator.remove();
		}
	}
}
