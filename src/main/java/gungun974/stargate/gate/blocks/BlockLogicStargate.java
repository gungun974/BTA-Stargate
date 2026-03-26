package gungun974.stargate.gate.blocks;

import gungun974.stargate.StargateBlocks;
import gungun974.stargate.core.VirtualWorld;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.items.ItemAddressCard;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.ArrayList;

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

	private int originalBlockMetadata(World world, int x, int y, int z, int rawMetadata, boolean includeDirection) {
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

				if (!includeDirection) {
					break;
				}

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
				if (entity instanceof TileEntityStargate) {
					CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

					if (camouflageComponent.hasCamouflage()) {
						Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

						if (camoufledBlock != null) {
							return camoufledBlock.getBreakResult(world, dropCause, camouflageComponent.getBlockMeta(), null);
						}
					}
				}


				return new ItemStack[]{new ItemStack(getIdForStargateBuildPartBlock(), 1, originalBlockMetadata(world, entity.x, entity.y, entity.z, meta, false))};
			default:
				return null;
		}
	}

	@Override
	public void harvestBlock(World world, Player player, int x, int y, int z, int meta, TileEntity entity) {
		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					camoufledBlock.harvestBlock(world, player, x, y, z, meta, null);
				}

				camouflageComponent.clearCamouflage();
				world.scheduleLightingUpdate(LightLayer.Block, x, y, z, x, y, z);
				return;
			}
		}
		super.harvestBlock(world, player, x, y, z, meta, entity);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, Side side, int meta, Player player, Item item) {
		if (player.getGamemode().dropBlockOnBreak()) {
			return;
		}

		TileEntity entity = world.getTileEntity(x, y, z);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				camouflageComponent.clearCamouflage();
				world.scheduleLightingUpdate(LightLayer.Block, x, y, z, x, y, z);
			}
		}

		super.onBlockDestroyedByPlayer(world, x, y, z, side, meta, player, item);
	}

	@Override
	public float blockStrength(World world, int x, int y, int z, Side side, Player player) {
		TileEntity entity = world.getTileEntity(x, y, z);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					return camoufledBlock.blockStrength(world, x, y, z, side, player);
				}

			}
		}

		return super.blockStrength(world, x, y, z, side, player);
	}

	public void restoreOriginalBlock(World world, int x, int y, int z) {
		if (world.isAirBlock(x, y, z)) {
			return;
		}

		if (world.getBlockId(x, y, z) != id()) {
			return;
		}

		TileEntity entity = world.getTileEntity(x, y, z);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					camoufledBlock.dropBlockWithCause(world, EnumDropCause.SILK_TOUCH, x, y, z, world.getBlockMetadata(x, y, z), null, null);
				}

			}
		}

		world.setBlockAndMetadataWithNotify(x, y, z, getIdForStargateBuildPartBlock(), originalBlockMetadata(world, x, y, z, world.getBlockMetadata(x, y, z), true));
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

		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);

			if (tileEntity instanceof TileEntityStargate) {
				TileEntityStargate stargate = (TileEntityStargate) tileEntity;

				ItemStack hand = player.getCurrentEquippedItem();

				StargateComponent gate = stargate.findMainStargateComponent();

				if (gate != null && hand != null && hand.itemID == Items.PAPER.id) {
					hand.stackSize -= 1;

					player.inventory.insertItem(ItemAddressCard.createFromGate(gate), false);

					return true;
				}

				if (!stargate.getCamouflageComponent().hasCamouflage()) {
					ItemStack heldItem = player.getHeldItem();
					if (heldItem != null) {

						Item item = heldItem.getItem();

						if (item instanceof ItemBlock) {
							ItemBlock<?> itemBlock = (ItemBlock<?>) item;

							if (!itemBlock.getBlock().isEntityTile) {

								VirtualWorld virtualWorld = new VirtualWorld(world);

								virtualWorld.setBlockWithNotify(x, y, z, 0);

								itemBlock.onUseItemOnBlock(heldItem, player, virtualWorld, x, y, z, side, xHit, yHit);

								int newId = virtualWorld.getBlockId(x, y, z);
								int newMeta = virtualWorld.getBlockMetadata(x, y, z);

								stargate.getCamouflageComponent().setCamouflage(newId, newMeta);

								world.notifyBlockChange(x, y, z, id());
								world.scheduleLightingUpdate(LightLayer.Block, x, y, z, x, y, z);

								return true;
							}
						}
					}
				}

			}
		}

		return false;
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

	public TileEntityStargate findMainTileEntityStargate(World world, int x, int y, int z) {
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

	@Override
	public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(x, y, z);

		AABB mainAABB;

		int rawMetadata = world.getBlockMetadata(x, y, z);

		int directionMetadata = rawMetadata & 0b110000;

		if (directionMetadata == 0b000000) {
			mainAABB = AABB.getPermanentBB(0, 0, 0.25, 1, 1, 0.75);
		} else if (directionMetadata == 0b010000) {
			mainAABB = AABB.getPermanentBB(0.25, 0, 0, 0.75, 1, 1);
		} else {
			mainAABB = AABB.getPermanentBB(0, 0.25, 0, 1, 0.75, 1);
		}

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					AABB otherAABB = camoufledBlock.getBlockBoundsFromState(world, x, y, z);
					return AABB.getPermanentBB(
						Math.min(mainAABB.minX, otherAABB.minX),
						Math.min(mainAABB.minY, otherAABB.minY),
						Math.min(mainAABB.minZ, otherAABB.minZ),
						Math.max(mainAABB.maxX, otherAABB.maxX),
						Math.max(mainAABB.maxY, otherAABB.maxY),
						Math.max(mainAABB.maxZ, otherAABB.maxZ)
					);
				}
			}
		}

		return mainAABB;
	}

	@Override
	public boolean isCubeShaped() {
		return false;
	}

	@Override
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList<AABB> aabbList) {
		TileEntity entity = world.getTileEntity(x, y, z);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					VirtualWorld virtualWorld = new VirtualWorld(world);

					virtualWorld.setBlockAndMetadataRaw(x, y, z, camouflageComponent.getBlockId(), camouflageComponent.getBlockMeta());

					camoufledBlock.getCollidingBoundingBoxes(virtualWorld, x, y, z, aabb, aabbList);
				}
			}
		}

		int rawMetadata = world.getBlockMetadata(x, y, z);

		int ringMetadata = rawMetadata & 0b1111;

		int directionMetadata = rawMetadata & 0b110000;

		if (directionMetadata == 0b000000) {
			if (ringMetadata == 2) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0, 0.25, 1, 0.5, 0.75).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0, 0.25, 0.5, 1, 0.75).move(x, y, z), aabbList);
			} else if (ringMetadata == 6) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.5, 0.25, 1, 1, 0.75).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0, 0.25, 0.5, 1, 0.75).move(x, y, z), aabbList);
			} else if (ringMetadata == 10) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.5, 0.25, 1, 1, 0.75).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0, 0.25, 1, 1, 0.75).move(x, y, z), aabbList);
			} else if (ringMetadata == 14) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0, 0.25, 1, 0.5, 0.75).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0, 0.25, 1, 1, 0.75).move(x, y, z), aabbList);
			} else {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0, 0.25, 1, 1, 0.75).move(x, y, z), aabbList);
			}
		} else if (directionMetadata == 0b010000) {
			if (ringMetadata == 2) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0, 0, 0.75, 0.5, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0, 0, 0.75, 1, 0.5).move(x, y, z), aabbList);
			} else if (ringMetadata == 6) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0.5, 0, 0.75, 1, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0, 0, 0.75, 1, 0.5).move(x, y, z), aabbList);
			} else if (ringMetadata == 10) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0.5, 0, 0.75, 1, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0, 0.5, 0.75, 1, 1).move(x, y, z), aabbList);
			} else if (ringMetadata == 14) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0, 0, 0.75, 0.5, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0, 0.5, 0.75, 1, 1).move(x, y, z), aabbList);
			} else {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.25, 0, 0, 0.75, 1, 1).move(x, y, z), aabbList);
			}
		} else {
			if (ringMetadata == 2) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.25, 0, 0.5, 0.75, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.25, 0.5, 1, 0.75, 1).move(x, y, z), aabbList);
			} else if (ringMetadata == 6) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.25, 0, 0.5, 0.75, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.25, 0, 1, 0.75, 0.5).move(x, y, z), aabbList);
			} else if (ringMetadata == 10) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0.25, 0, 1, 0.75, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.25, 0, 1, 0.75, 0.5).move(x, y, z), aabbList);
			} else if (ringMetadata == 14) {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0.25, 0, 1, 0.75, 1).move(x, y, z), aabbList);
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.25, 0.5, 1, 0.75, 1).move(x, y, z), aabbList);
			} else {
				this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0, 0.25, 0, 1, 0.75, 1).move(x, y, z), aabbList);
			}
		}
	}
}
