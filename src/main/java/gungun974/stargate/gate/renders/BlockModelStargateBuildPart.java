package gungun974.stargate.gate.renders;

import gungun974.stargate.gate.blocks.BlockLogicStargateBuildPart;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

public class BlockModelStargateBuildPart<T extends BlockLogic> extends BlockModelGeneric<T> {
	public final @NotNull StaticBlockModel core;
	public final @NotNull StaticBlockModel chevron;

	public BlockModelStargateBuildPart(Block<T> block, String modelPrefix) {
		super(block, BlockModelDispatcher.loadDataModel(modelPrefix + "_ring").asModel());
		this.core = BlockModelDispatcher.loadDataModel(modelPrefix + "_core").asModel();
		this.chevron = BlockModelDispatcher.loadDataModel(modelPrefix + "_chevron").asModel();
	}

	public boolean renderAttached(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos, boolean cullFaces, @Nullable IconCoordinate overrideTexture) {
		Direction direction = BlockLogicStargateBuildPart.getDirectionFromMeta(worldSource.getBlockData(tilePos));
		switch (direction) {
			case UP -> {
				return this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, -1, 0, 0, 0.0F, 0.0F, 0.0F, false, cullFaces, overrideTexture);
			}
			case DOWN -> {
				return this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, 1, 0, 0, 0.0F, 0.0F, 0.0F, false, cullFaces, overrideTexture);
			}
			case WEST -> {
				return this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, 0, 1, 0, 0.0F, 0.0F, 0.0F, false, cullFaces, overrideTexture);
			}
			case EAST -> {
				return this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, 0, 3, 0, 0.0F, 0.0F, 0.0F, false, cullFaces, overrideTexture);
			}
			case SOUTH -> {
				return this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, 0, 2, 0, 0.0F, 0.0F, 0.0F, false, cullFaces, overrideTexture);
			}
			default -> {
				return this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, 0, 0, 0, 0.0F, 0.0F, 0.0F, false, cullFaces, overrideTexture);
			}
		}
	}

	@Override
	public @NotNull StaticBlockModel getModel(@NotNull WorldSource source, @NotNull TilePosc tilePosc) {
		return getModelFromData(BlockLogicStargateBuildPart.getRawMeta(source.getBlockData(tilePosc)));
	}

	public @NotNull StaticBlockModel getModelFromData(int data) {
		if (data == BlockLogicStargateBuildPart.CORE_META) {
			return core;
		}
		if (data == BlockLogicStargateBuildPart.CHEVRON_META) {
			return chevron;
		}

		return staticModel;
	}
}
