package gungun974.stargate.gate.blocks;

import gungun974.stargate.StargateBlocks;
import gungun974.stargate.core.StargateFamily;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicStargateBuildPart extends BlockLogic {
	public static final int MASK_DIRECTION = 0b00011000;

	public static final int RING_META = 0;
	public static final int CHEVRON_META = 1;
	public static final int CORE_META = 2;

	public BlockLogicStargateBuildPart(Block<?> block, Material material) {
		super(block, material);
	}

	public static int getRawMeta(int meta) {
		return meta & ~MASK_DIRECTION;
	}

	public static Direction getDirectionFromMeta(int meta) {
		switch (meta & MASK_DIRECTION) {
			case 0b00000000:
				return Direction.NORTH;
			case 0b00001000:
				return Direction.EAST;
			case 0b00010000:
				return Direction.SOUTH;
			case 0b00011000:
				return Direction.WEST;
		}
		return Direction.NORTH;
	}

	public static int setDirection(int meta, Direction direction) {
		meta = meta & ~MASK_DIRECTION;

		switch (direction) {
			case EAST:
				return meta | 0b00001000;
			case SOUTH:
				return meta | 0b00010000;
			case WEST:
				return meta | 0b00011000;
			default:
				return meta;
		}
	}

	static public boolean canBuildVerticalStargate(World world, int x, int y, int z, Direction direction) {
		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = x + direction.getOffsetZ() * i;
			int py = y + j;
			int pz = z + direction.getOffsetX() * i;

			if (!world.canPlaceInsideBlock(px, py, pz)) {
				return false;
			}
		}

		return true;
	}

	static public void buildVerticalStargate(World world, int x, int y, int z, Direction direction, StargateFamily family) {
		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		boolean invertOrder = (direction == Direction.WEST || direction == Direction.NORTH);

		int stargateBlockId = 0;

		switch (family) {
			case MilkyWay:
				stargateBlockId = StargateBlocks.STARGATE_MILKYWAY.id();
				break;
			case Pegasus:
				stargateBlockId = StargateBlocks.STARGATE_PEGASUS.id();
				break;
			case Universe:
				stargateBlockId = StargateBlocks.STARGATE_UNIVERSE.id();
				break;
		}

		for (int idx = 0; idx < ringOrder.length; idx++) {
			int i = ringOrder[idx][0];
			int j = ringOrder[idx][1];

			int meta;
			if (invertOrder && idx > 0) {
				meta = ringOrder.length - idx;
			} else {
				meta = idx;
			}

			int blockMeta = meta;

			if (direction == Direction.EAST || direction == Direction.WEST) {
				blockMeta += 0b010000;
			}

			int px = x + direction.getOffsetZ() * i;
			int py = y + j;
			int pz = z + direction.getOffsetX() * i;

			world.setBlockAndMetadataRaw(px, py, pz, stargateBlockId, blockMeta);
			TileEntity tileEntity = world.getTileEntity(px, py, pz);
			if (tileEntity == null) {
				continue;
			}

			if (!(tileEntity instanceof TileEntityStargate)) {
				continue;
			}

			if (idx == 0) {
				((TileEntityStargate) tileEntity).setRole(TileEntityStargate.Role.CORE);
				((TileEntityStargate) tileEntity).getStargateComponent().setDirection(direction.getOpposite());
				((TileEntityStargate) tileEntity).getStargateComponent().setOrientation(Direction.NORTH);
			} else {
				((TileEntityStargate) tileEntity).setRole(TileEntityStargate.Role.RING);
			}
		}

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = x + direction.getOffsetZ() * i;
			int py = y + j;
			int pz = z + direction.getOffsetX() * i;

			world.notifyBlockChange(px, py, pz, stargateBlockId);
		}
	}

	static public void buildHorizontalStargate(World world, int x, int y, int z, Direction direction, Direction orientation, StargateFamily family) {
		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		int stargateBlockId = 0;

		switch (family) {
			case MilkyWay:
				stargateBlockId = StargateBlocks.STARGATE_MILKYWAY.id();
				break;
			case Pegasus:
				stargateBlockId = StargateBlocks.STARGATE_PEGASUS.id();
				break;
			case Universe:
				stargateBlockId = StargateBlocks.STARGATE_UNIVERSE.id();
				break;
		}

		for (int idx = 0; idx < ringOrder.length; idx++) {
			int i = ringOrder[idx][0];
			int j = ringOrder[idx][1];

			int meta;
			if (direction == Direction.WEST) {
				meta = ringOrder.length - idx - 12;
			} else if (direction == Direction.NORTH) {
				meta = idx - 8;
			} else if (direction == Direction.EAST) {
				meta = ringOrder.length - idx - 4;
			} else {
				meta = idx;
			}

			meta = Math.floorMod(meta, 16);

			int blockMeta = meta;

			blockMeta += 0b100000;

			if (direction == Direction.WEST) {
				blockMeta += 0b11000000;
			} else if (direction == Direction.NORTH) {
				blockMeta += 0b01000000;
			} else if (direction == Direction.EAST) {
				blockMeta += 0b10000000;
			}

			int px = x + direction.getOffsetZ() * i - direction.getOffsetX() * j;
			int pz = z + direction.getOffsetX() * i - direction.getOffsetZ() * j;

			world.setBlockAndMetadataRaw(px, y, pz, stargateBlockId, blockMeta);

			TileEntity tileEntity = world.getTileEntity(px, y, pz);
			if (tileEntity == null) {
				continue;
			}

			if (!(tileEntity instanceof TileEntityStargate)) {
				continue;
			}

			if (idx == 0) {
				((TileEntityStargate) tileEntity).setRole(TileEntityStargate.Role.CORE);
				((TileEntityStargate) tileEntity).getStargateComponent().setDirection(direction.getOpposite());
				((TileEntityStargate) tileEntity).getStargateComponent().setOrientation(orientation);
			} else {
				((TileEntityStargate) tileEntity).setRole(TileEntityStargate.Role.RING);
			}
		}

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = x + direction.getOffsetZ() * i - direction.getOffsetX() * j;
			int pz = z + direction.getOffsetX() * i - direction.getOffsetZ() * j;

			world.notifyBlockChange(px, y, pz, stargateBlockId);
		}
	}

	static public boolean canBuildHorizontalStargate(World world, int x, int y, int z, Direction direction) {
		int[][] ringOrder = {
			{0, 0},
			{-1, 0},
			{-2, 1},
			{-3, 2},
			{-3, 3},
			{-3, 4},
			{-2, 5},
			{-1, 6},
			{0, 6},
			{1, 6},
			{2, 5},
			{3, 4},
			{3, 3},
			{3, 2},
			{2, 1},
			{1, 0},
		};

		for (int[] ints : ringOrder) {
			int i = ints[0];
			int j = ints[1];

			int px = x + direction.getOffsetZ() * i - direction.getOffsetX() * j;
			int pz = z + direction.getOffsetX() * i - direction.getOffsetZ() * j;

			if (!world.canPlaceInsideBlock(px, y, pz)) {
				return false;
			}
		}

		return true;
	}

	public String getLanguageKey(int meta) {
		if (meta == RING_META) {
			return this.block.getKey().replace(".build.part", ".ring");
		}
		if (meta == CHEVRON_META) {
			return this.block.getKey().replace(".build.part", ".chevron");
		}
		if (meta == CORE_META) {
			return this.block.getKey().replace(".build.part", ".core");
		}
		return this.block.getKey();
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity entity) {
		switch (dropCause) {
			case PICK_BLOCK:
			case EXPLOSION:
			case PROPER_TOOL:
			case SILK_TOUCH:
			case PISTON_CRUSH:
				return new ItemStack[]{new ItemStack(block.id(), 1, 0)};
			default:
				return null;
		}
	}

	@Override
	public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
		if (stack.getMetadata() == CHEVRON_META) {
			return CHEVRON_META;
		}
		if (stack.getMetadata() == CORE_META) {
			return CORE_META;
		}
		return RING_META;
	}

	@Override
	public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
		world.setBlockMetadataWithNotify(x, y, z, setDirection(world.getBlockMetadata(x, y, z), mob.getHorizontalPlacementDirection(side).getOpposite()));
		findStargateCoreForCheckAndAssemble(world, x, y, z);
	}

	private boolean isSameBlockWithMeta(World worldSource, int x, int y, int z, int metaId) {
		if (worldSource.getBlockId(x, y, z) != this.id()) {
			return false;
		}

		return getRawMeta(worldSource.getBlockMetadata(x, y, z)) == metaId;
	}

	private void findStargateCoreForCheckAndAssemble(World worldSource, int x, int y, int z) {
		if (isSameBlockWithMeta(worldSource, x, y, z, CORE_META)) {
			checkAndAssemble(worldSource, x, y, z);
			return;
		}

		for (Direction direction : Direction.directions) {
			if (direction == Direction.DOWN) {
				continue;
			}

			for (int j = 0; j < 7; j++) {
				int i;
				switch (j) {
					case 0:
						i = 1;
						break;
					case 1:
					case 5:
						i = 2;
						break;
					case 2:
					case 3:
					case 4:
						i = 3;
						break;
					case 6:
						i = 1;
						if (isSameBlockWithMeta(worldSource, x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j, CORE_META)) {
							checkAndAssemble(worldSource, x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j);
							return;
						}
						break;
					default:
						continue;
				}

				if (direction == Direction.UP) {
					if (isSameBlockWithMeta(worldSource, x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j + i, CORE_META)) {
						checkAndAssemble(worldSource, x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j + i);
						return;
					}

					if (isSameBlockWithMeta(worldSource, x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j - i, CORE_META)) {
						checkAndAssemble(worldSource, x - direction.getOffsetX() * j, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j - i);
						return;
					}

					if (isSameBlockWithMeta(worldSource, x - direction.getOffsetX() * j + i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j, CORE_META)) {
						checkAndAssemble(worldSource, x - direction.getOffsetX() * j + i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j);
						return;
					}

					if (isSameBlockWithMeta(worldSource, x - direction.getOffsetX() * j - i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j, CORE_META)) {
						checkAndAssemble(worldSource, x - direction.getOffsetX() * j - i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j);
						return;
					}
				} else {
					if (isSameBlockWithMeta(worldSource, x - direction.getOffsetX() * j + direction.getOffsetZ() * i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j + direction.getOffsetX() * i, CORE_META)) {
						checkAndAssemble(worldSource, x - direction.getOffsetX() * j + direction.getOffsetZ() * i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j + direction.getOffsetX() * i);
						return;
					}
					if (isSameBlockWithMeta(worldSource, x - direction.getOffsetX() * j - direction.getOffsetZ() * i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j - direction.getOffsetX() * i, CORE_META)) {
						checkAndAssemble(worldSource, x - direction.getOffsetX() * j - direction.getOffsetZ() * i, y - direction.getOffsetY() * j, z - direction.getOffsetZ() * j - direction.getOffsetX() * i);
						return;
					}
				}
			}
		}
	}

	private void checkAndAssemble(World world, int x, int y, int z) {
		Direction direction;

		if (world == null) {
			direction = Direction.NORTH;
		} else {
			direction = getDirectionFromMeta(world.getBlockMetadata(x, y, z));
		}

		if (isValidStructureVertical(world, x, y, z, direction)) {
			buildVerticalStargate(world, x, y, z, direction, getFamilyForStargateBlock());
			return;
		}
		if (isValidStructureHorizontal(world, x, y, z, direction)) {
			buildHorizontalStargate(world, x, y, z, direction, Direction.UP, getFamilyForStargateBlock());
		}
	}

	private boolean isValidStructureVertical(World world, int x, int y, int z, Direction direction) {
		if (world == null) {
			return false;
		}

		for (int j = 0; j < 7; j++) {
			for (int i = -4; i < 5; i++) {
				switch (j) {
					case 0:
						if (Math.abs(i) != 1) continue;
						break;
					case 1:
					case 5:
						if (Math.abs(i) != 2) continue;
						break;
					case 2:
					case 3:
					case 4:
						if (Math.abs(i) != 3) continue;
						break;
					case 6:
						if (Math.abs(i) > 1) continue;
						break;
					default:
						continue;
				}

				int px = x + direction.getOffsetZ() * i;
				int py = y + j;
				int pz = z + direction.getOffsetX() * i;

				if (j == 0 || j == 1 || j == 3 || j == 5 || (j == 6 && i == 0)) {
					if (!isSameBlockWithMeta(world, px, py, pz, CHEVRON_META)) {
						return false;
					}
				} else if (!isSameBlockWithMeta(world, px, py, pz, RING_META)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidStructureHorizontal(World world, int x, int y, int z, Direction direction) {
		if (world == null) {
			return false;
		}

		for (int j = 0; j < 7; j++) {
			for (int i = -4; i < 5; i++) {
				switch (j) {
					case 0:
						if (Math.abs(i) != 1) continue;
						break;
					case 1:
					case 5:
						if (Math.abs(i) != 2) continue;
						break;
					case 2:
					case 3:
					case 4:
						if (Math.abs(i) != 3) continue;
						break;
					case 6:
						if (Math.abs(i) > 1) continue;
						break;
					default:
						continue;
				}

				int px = x + direction.getOffsetZ() * i - direction.getOffsetX() * j;
				int pz = z + direction.getOffsetX() * i - direction.getOffsetZ() * j;

				if (j == 0 || j == 1 || j == 3 || j == 5 || (j == 6 && i == 0)) {
					if (!isSameBlockWithMeta(world, px, y, pz, CHEVRON_META)) {
						return false;
					}
				} else if (!isSameBlockWithMeta(world, px, y, pz, RING_META)) {
					return false;
				}
			}
		}
		return true;
	}

	private StargateFamily getFamilyForStargateBlock() {
		int id = id();

		if (id == StargateBlocks.STARGATE_BUILD_PART_PEGASUS.id()) {
			return StargateFamily.Pegasus;
		}
		if (id == StargateBlocks.STARGATE_BUILD_PART_UNIVERSE.id()) {
			return StargateFamily.Universe;
		}

		return StargateFamily.MilkyWay;
	}
}
