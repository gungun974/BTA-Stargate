package gungun974.stargate.gate.blocks;

import gungun974.stargate.StargateBlocks;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import turniplabs.halplibe.helper.EnvironmentHelper;

public class BlockLogicStargate extends BlockLogic {
	public BlockLogicStargate(Block<?> block, Material material) {
		super(block, material);
	}

	private int getIdForStargateBuildPartBlock() {
		int id = id();

		if (id == StargateBlocks.STARGATE_PEGASUS.id()) {
			return StargateBlocks.STARGATE_BUILD_PART_PEGASUS.id();
		}
		if (id == StargateBlocks.STARGATE_UNIVERSE.id()) {
			return StargateBlocks.STARGATE_BUILD_PART_UNIVERSE.id();
		}

		return StargateBlocks.STARGATE_BUILD_PART_MILKYWAY.id();
	}

	private int originalBlockMetadata(World world, int x, int y, int z, int rawMetadata) {
		int ringMetadata = rawMetadata & 0b1111;

		int ringMetadataOffset = rawMetadata & 0b11000000;

		if (ringMetadataOffset == 0b11000000) {
			ringMetadata = Math.floorMod(ringMetadata - 4, 16);
		} else if (ringMetadataOffset == 0b01000000) {
			ringMetadata = Math.floorMod(ringMetadata - 8, 16);
		} else if (ringMetadataOffset == 0b10000000) {
			ringMetadata = Math.floorMod(ringMetadata + 4, 16);
		}

		int metadata = BlockLogicStargateBuildPart.RING_META;

		switch (ringMetadata) {
			case 0:
				metadata = BlockLogicStargateBuildPart.CORE_META;

				TileEntity tileEntity = world.getTileEntity(x, y, z);
				if (tileEntity instanceof TileEntityStargate) {

					StargateComponent gate = ((TileEntityStargate) tileEntity).getStargateComponent();

					if (gate != null) {
						metadata = BlockLogicStargateBuildPart.setDirection(metadata, gate.getDirection().getOpposite());
					}
				}
				break;
			case 1:
			case 2:
			case 4:
			case 6:
			case 8:
			case 10:
			case 12:
			case 14:
			case 15:
				metadata = BlockLogicStargateBuildPart.CHEVRON_META;
		}

		return metadata;
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity entity) {
		switch (dropCause) {
			case PICK_BLOCK:
			case EXPLOSION:
			case PROPER_TOOL:
			case SILK_TOUCH:
			case PISTON_CRUSH:
				return new ItemStack[]{new ItemStack(getIdForStargateBuildPartBlock(), 1, originalBlockMetadata(world, entity.x, entity.y, entity.z, meta))};
			default:
				return null;
		}
	}

	public void restoreOriginalBlock(World world, int x, int y, int z) {
		if (world.isAirBlock(x, y, z)) {
			return;
		}

		if (world.getBlockId(x, y, z) != id()) {
			return;
		}

		world.setBlockAndMetadataWithNotify(x, y, z, getIdForStargateBuildPartBlock(), originalBlockMetadata(world, x, y, z, world.getBlockMetadata(x, y, z)));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		checkIfStillValid(world, x, y, z);
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		checkIfStillValid(world, x, y, z);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		if (EnvironmentHelper.isClientWorld()) {
			return false;
		}

		checkIfStillValid(world, x, y, z);

		TileEntityStargate stargate = findMainTileEntityStargate(world, x, y, z);
		if (stargate == null) {
			return false;
		}

		StargateComponent component = stargate.getStargateComponent();
		if (component == null) {
			return false;
		}

		component.autoDial();

		return true;
	}

	private TileEntityStargate extractMainTileEntityStargate(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null) {
			return null;
		}

		if (!(tileEntity instanceof TileEntityStargate)) {
			return null;
		}

		TileEntityStargate stargate = (TileEntityStargate) tileEntity;

		if (stargate.getRole() != TileEntityStargate.Role.CORE) {
			return null;
		}

		return stargate;
	}

	private TileEntityStargate findMainTileEntityStargate(World world, int x, int y, int z) {
		int rawMetadata = world.getBlockMetadata(x, y, z);

		int ringMetadata = rawMetadata & 0b1111;
		int directionMetadata = rawMetadata & 0b110000;

		int cx = x;
		int cy = y;
		int cz = z;

		if (directionMetadata == 0b100000) {
			switch (ringMetadata) {
				case 1:
				case 15:
					cx += ringMetadata < 8 ? 1 : -1;
					break;
				case 2:
				case 14:
					cx += ringMetadata < 8 ? 2 : -2;
					cz += 1;
					break;
				case 3:
				case 13:
					cx += ringMetadata < 8 ? 3 : -3;
					cz += 2;
					break;
				case 4:
				case 12:
					cx += ringMetadata < 8 ? 3 : -3;
					cz += 3;
					break;
				case 5:
				case 11:
					cx += ringMetadata < 8 ? 3 : -3;
					cz += 4;
					break;
				case 6:
				case 10:
					cx += ringMetadata < 8 ? 2 : -2;
					cz += 5;
					break;
				case 7:
				case 9:
					cx += ringMetadata < 8 ? 1 : -1;
					cz += 6;
					break;
				case 8:
					cz += 6;
					break;
			}

			TileEntityStargate tileEntityStargate;

			tileEntityStargate = extractMainTileEntityStargate(world, cx, cy, cz);

			if (tileEntityStargate == null) {
				tileEntityStargate = extractMainTileEntityStargate(world, cx, cy, cz - 6);
			}

			if (tileEntityStargate == null) {
				tileEntityStargate = extractMainTileEntityStargate(world, cx - 3, cy, cz - 3);
			}

			if (tileEntityStargate == null) {
				tileEntityStargate = extractMainTileEntityStargate(world, cx + 3, cy, cz - 3);
			}

			return tileEntityStargate;
		} else if (directionMetadata == 0b010000) {
			switch (ringMetadata) {
				case 1:
				case 15:
					cz += ringMetadata < 8 ? 1 : -1;
					break;
				case 2:
				case 14:
					cz += ringMetadata < 8 ? 2 : -2;
					cy -= 1;
					break;
				case 3:
				case 13:
					cz += ringMetadata < 8 ? 3 : -3;
					cy -= 2;
					break;
				case 4:
				case 12:
					cz += ringMetadata < 8 ? 3 : -3;
					cy -= 3;
					break;
				case 5:
				case 11:
					cz += ringMetadata < 8 ? 3 : -3;
					cy -= 4;
					break;
				case 6:
				case 10:
					cz += ringMetadata < 8 ? 2 : -2;
					cy -= 5;
					break;
				case 7:
				case 9:
					cz += ringMetadata < 8 ? 1 : -1;
					cy -= 6;
					break;
				case 8:
					cy -= 6;
					break;
			}
		} else {
			switch (ringMetadata) {
				case 1:
				case 15:
					cx += ringMetadata < 8 ? 1 : -1;
					break;
				case 2:
				case 14:
					cx += ringMetadata < 8 ? 2 : -2;
					cy -= 1;
					break;
				case 3:
				case 13:
					cx += ringMetadata < 8 ? 3 : -3;
					cy -= 2;
					break;
				case 4:
				case 12:
					cx += ringMetadata < 8 ? 3 : -3;
					cy -= 3;
					break;
				case 5:
				case 11:
					cx += ringMetadata < 8 ? 3 : -3;
					cy -= 4;
					break;
				case 6:
				case 10:
					cx += ringMetadata < 8 ? 2 : -2;
					cy -= 5;
					break;
				case 7:
				case 9:
					cx += ringMetadata < 8 ? 1 : -1;
					cy -= 6;
					break;
				case 8:
					cy -= 6;
					break;
			}
		}

		return extractMainTileEntityStargate(world, cx, cy, cz);
	}

	void checkIfStillValid(World world, int x, int y, int z) {
		TileEntityStargate stargate = findMainTileEntityStargate(world, x, y, z);

		if (stargate == null) {
			restoreOriginalBlock(world, x, y, z);
			return;
		}

		StargateComponent gate = stargate.getStargateComponent();

		if (gate == null) {
			restoreOriginalBlock(world, x, y, z);
			return;
		}

		gate.checkIfStillValid();
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}
}
