package gungun974.stargate.gate.blocks;

import gungun974.stargate.StargateBlocks;
import gungun974.stargate.core.VirtualWorld;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.components.StargateComponent;
import gungun974.stargate.gate.items.ItemAddressCard;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.List;

public class BlockLogicStargate extends BlockLogic {
	public BlockLogicStargate(Block<?> block, Material material) {
		super(block, material);
	}

	@Environment(EnvType.SERVER)
	private static void updateServerInventory(Player player) {
		if (player instanceof PlayerServer) {
			((PlayerServer) player).updateCraftingInventory(player.inventoryMenu, player.inventoryMenu.getSlotStackList());
		}
	}

	private Block getIdForStargateBuildPartBlock() {
		int id = id();

		if (id == StargateBlocks.STARGATE_PEGASUS.id()) {
			return StargateBlocks.STARGATE_BUILD_PART_PEGASUS;
		}
		if (id == StargateBlocks.STARGATE_UNIVERSE.id()) {
			return StargateBlocks.STARGATE_BUILD_PART_UNIVERSE;
		}

		return StargateBlocks.STARGATE_BUILD_PART_MILKYWAY;
	}

	private int originalBlockMetadata(TileEntity tileEntity, int rawMetadata, boolean includeDirection) {
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

				if (tileEntity instanceof TileEntityStargate) {

					StargateComponent gate = ((TileEntityStargate) tileEntity).getStargateComponent();

					if (gate != null) {
						metadata = BlockLogicStargateBuildPart.setDirection(metadata, gate.getDirection().opposite());
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

				return new ItemStack[]{new ItemStack(getIdForStargateBuildPartBlock(), 1, originalBlockMetadata(entity, meta, false))};
			default:
				return null;
		}
	}

	@Override
	public void onHarvest(@NotNull World world, @NotNull Player player, @NotNull TilePosc tilePos, int meta, @Nullable TileEntity entity) {
		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					camoufledBlock.onHarvest(world, player, tilePos, meta, null);
				}

				camouflageComponent.clearCamouflage();
				world.scheduleLightingUpdate(LightLayer.Block, new TilePos(tilePos), new TilePos(tilePos));
				return;
			}
		}
		super.onHarvest(world, player, tilePos, meta, entity);
	}

	@Override
	public void onDestroyedByPlayer(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, int meta, @NotNull Player player, @Nullable Item item) {
		if (player.getGamemode().hasItemDrops()) {
			return;
		}

		TileEntity entity = world.getTileEntity(tilePos);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				camouflageComponent.clearCamouflage();
				world.scheduleLightingUpdate(LightLayer.Block, new TilePos(tilePos), new TilePos(tilePos));
			}
		}

		super.onDestroyedByPlayer(world, tilePos, side, meta, player, item);
	}

	@Override
	public float getStrength(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, @NotNull Player player) {
		TileEntity entity = world.getTileEntity(tilePos);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					return camoufledBlock.getStrength(world, tilePos, side, player);
				}

			}
		}

		return super.getStrength(world, tilePos, side, player);
	}

	public void restoreOriginalBlock(World world, TilePosc tilePos) {
		if (world instanceof VirtualWorld) {
			return;
		}

		if (world.isAirBlock(tilePos)) {
			return;
		}

		if (world.getBlockType(tilePos).id() != id()) {
			return;
		}

		TileEntity entity = world.getTileEntity(tilePos);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					camoufledBlock.dropWithCause(world, EnumDropCause.SILK_TOUCH, tilePos, camouflageComponent.getBlockMeta(), null, null);
				}

				camouflageComponent.clearCamouflage();
			}
		}

		world.setBlockTypeDataNotify(tilePos, getIdForStargateBuildPartBlock(), originalBlockMetadata(world.getTileEntity(tilePos), world.getBlockData(tilePos), true));
	}

	@Override
	public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block block) {
		if (world instanceof VirtualWorld) {
			return;
		}
		checkIfStillValid(world, tilePos);
	}

	@Override
	public void onRemoved(@NotNull World world, @NotNull TilePosc tilePos, int data) {
		if (world instanceof VirtualWorld) {
			return;
		}
		checkIfStillValid(world, tilePos);
		world.notifyBlocksOfNeighborChange(tilePos.add(-1, -1, -1, new TilePos()), Blocks.AIR);
		world.notifyBlocksOfNeighborChange(tilePos.add(-1, -1, 1, new TilePos()), Blocks.AIR);
		world.notifyBlocksOfNeighborChange(tilePos.add(-1, 1, -1, new TilePos()), Blocks.AIR);
		world.notifyBlocksOfNeighborChange(tilePos.add(-1, 1, 1, new TilePos()), Blocks.AIR);
		world.notifyBlocksOfNeighborChange(tilePos.add(1, -1, -1, new TilePos()), Blocks.AIR);
		world.notifyBlocksOfNeighborChange(tilePos.add(1, -1, 1, new TilePos()), Blocks.AIR);
		world.notifyBlocksOfNeighborChange(tilePos.add(1, 1, -1, new TilePos()), Blocks.AIR);
		world.notifyBlocksOfNeighborChange(tilePos.add(1, 1, 1, new TilePos()), Blocks.AIR);

		TileEntity tileEntity = world.getTileEntity(tilePos);

		if (tileEntity instanceof TileEntityStargate) {
			((TileEntityStargate) tileEntity).destroyed();
		}

	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
		if (EnvironmentHelper.isClientWorld()) {
			return false;
		}

		checkIfStillValid(world, tilePos);

		{
			TileEntity tileEntity = world.getTileEntity(tilePos);

			if (tileEntity instanceof TileEntityStargate stargate) {

				ItemStack hand = player.getCurrentEquippedItem();

				StargateComponent gate = stargate.findMainStargateComponent();

				if (gate != null && hand != null && hand.itemID == Items.PAPER.id) {
					hand.stackSize -= 1;

					player.inventory.insertItem(ItemAddressCard.createFromGate(gate), true);

					if (EnvironmentHelper.isServerEnvironment()) {
						updateServerInventory(player);
					}

					return true;
				}

				if (!stargate.getCamouflageComponent().hasCamouflage()) {
					ItemStack heldItem = player.getHeldItem();
					if (heldItem != null) {

						Item item = heldItem.getItem();

						if (item instanceof ItemBlock<?> itemBlock) {

							if (!itemBlock.getBlock().isEntityTile) {

								VirtualWorld virtualWorld = new VirtualWorld(world);

								virtualWorld.setBlockTypeDataNotify(tilePos, Blocks.AIR, 0);

								itemBlock.onUseOnBlock(heldItem, virtualWorld, player, tilePos, side, xHit, yHit);

								int newId = virtualWorld.getBlockType(tilePos).id();
								int newMeta = virtualWorld.getBlockData(tilePos);

								stargate.getCamouflageComponent().setCamouflage(newId, newMeta);

								world.notifyBlockChange(tilePos, block);
								world.scheduleLightingUpdate(LightLayer.Block, new TilePos(tilePos), new TilePos(tilePos));

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
		TileEntity tileEntity = world.getTileEntity(new TilePos(x, y, z));
		if (tileEntity == null) {
			return null;
		}

		if (!(tileEntity instanceof TileEntityStargate stargate)) {
			return null;
		}

		if (stargate.getRole() != TileEntityStargate.Role.CORE) {
			return null;
		}

		return stargate;
	}

	public TileEntityStargate findMainTileEntityStargate(World world, TilePosc tilePos) {
		int rawMetadata = world.getBlockData(tilePos);

		int ringMetadata = rawMetadata & 0b1111;
		int directionMetadata = rawMetadata & 0b110000;

		int cx = tilePos.x();
		int cy = tilePos.y();
		int cz = tilePos.z();

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

	void checkIfStillValid(World world, TilePosc tilePos) {
		TileEntityStargate stargate = findMainTileEntityStargate(world, tilePos);

		if (stargate == null) {
			restoreOriginalBlock(world, tilePos);
			return;
		}

		StargateComponent gate = stargate.getStargateComponent();

		if (gate == null) {
			restoreOriginalBlock(world, tilePos);
			return;
		}

		gate.checkIfStillValid();
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public @NotNull AABBdc getBoundsFromState(@NotNull WorldSource world, @NotNull TilePosc tilePos) {
		TileEntity entity = world.getTileEntity(tilePos);

		AABBdc mainAABB;

		int rawMetadata = world.getBlockData(tilePos);

		int directionMetadata = rawMetadata & 0b110000;

		if (directionMetadata == 0b000000) {
			mainAABB = new AABBd(0, 0, 0.25, 1, 1, 0.75);
		} else if (directionMetadata == 0b010000) {
			mainAABB = new AABBd(0.25, 0, 0, 0.75, 1, 1);
		} else {
			mainAABB = new AABBd(0, 0.25, 0, 1, 0.75, 1);
		}

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					AABBdc otherAABB = camoufledBlock.getBoundsFromState(world, tilePos);
					return new AABBd(
						Math.min(mainAABB.minX(), otherAABB.minX()),
						Math.min(mainAABB.minY(), otherAABB.minY()),
						Math.min(mainAABB.minZ(), otherAABB.minZ()),
						Math.max(mainAABB.maxX(), otherAABB.maxX()),
						Math.max(mainAABB.maxY(), otherAABB.maxY()),
						Math.max(mainAABB.maxZ(), otherAABB.maxZ())
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
	public void getCollisionAABBs(@NotNull World world, @NotNull TilePosc tilePos, @NotNull AABBdc aabb, @NotNull List<AABBdc> aabbList) {
		TileEntity entity = world.getTileEntity(tilePos);

		if (entity instanceof TileEntityStargate) {
			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();

			if (camouflageComponent.hasCamouflage()) {
				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];

				if (camoufledBlock != null) {
					VirtualWorld virtualWorld = new VirtualWorld(world);

					virtualWorld.setBlockTypeDataRaw(tilePos, Blocks.getBlock(camouflageComponent.getBlockId()), camouflageComponent.getBlockMeta());

					camoufledBlock.getCollisionAABBs(virtualWorld, tilePos, aabb, aabbList);
				}
			}
		}

		int rawMetadata = world.getBlockData(tilePos);

		int ringMetadata = rawMetadata & 0b1111;

		int directionMetadata = rawMetadata & 0b110000;

		if (directionMetadata == 0b000000) {
			if (ringMetadata == 2) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0, 0.25, 1, 0.5, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0, 0.25, 0.5, 1, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 6) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.5, 0.25, 1, 1, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0, 0.25, 0.5, 1, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 10) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.5, 0.25, 1, 1, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0.5, 0, 0.25, 1, 1, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 14) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0, 0.25, 1, 0.5, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0.5, 0, 0.25, 1, 1, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0, 0.25, 1, 1, 0.75).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			}
		} else if (directionMetadata == 0b010000) {
			if (ringMetadata == 2) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0, 0, 0.75, 0.5, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0, 0, 0.75, 1, 0.5).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 6) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0.5, 0, 0.75, 1, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0, 0, 0.75, 1, 0.5).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 10) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0.5, 0, 0.75, 1, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0, 0.5, 0.75, 1, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 14) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0, 0, 0.75, 0.5, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0, 0.5, 0.75, 1, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else {
				this.addIntersectingBoundingBox(aabb, new AABBd(0.25, 0, 0, 0.75, 1, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			}
		} else {
			if (ringMetadata == 2) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.25, 0, 0.5, 0.75, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.25, 0.5, 1, 0.75, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 6) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.25, 0, 0.5, 0.75, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.25, 0, 1, 0.75, 0.5).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 10) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0.5, 0.25, 0, 1, 0.75, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.25, 0, 1, 0.75, 0.5).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else if (ringMetadata == 14) {
				this.addIntersectingBoundingBox(aabb, new AABBd(0.5, 0.25, 0, 1, 0.75, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.25, 0.5, 1, 0.75, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			} else {
				this.addIntersectingBoundingBox(aabb, new AABBd(0, 0.25, 0, 1, 0.75, 1).translate(tilePos.x(), tilePos.y(), tilePos.z()), aabbList);
			}
		}
	}

//TODO:	@Override
//	public boolean canPlaceOnSurfaceOnCondition(World world, int x, int y, int z) {
//		TileEntity entity = world.getTileEntity(x, y, z);
//
//		if (entity instanceof TileEntityStargate) {
//			CamouflageComponent camouflageComponent = ((TileEntityStargate) entity).getCamouflageComponent();
//
//			if (camouflageComponent.hasCamouflage()) {
//				Block<?> camoufledBlock = Blocks.blocksList[camouflageComponent.getBlockId()];
//
//				if (camoufledBlock != null) {
//					VirtualWorld virtualWorld = new VirtualWorld(world);
//
//					virtualWorld.setBlockAndMetadataRaw(x, y, z, camouflageComponent.getBlockId(), camouflageComponent.getBlockMeta());
//
//					return camoufledBlock.canPlaceOnSurfaceOnCondition(virtualWorld, x, y, z);
//				}
//			}
//		}
//
//		return this.canPlaceOnSurfaceOnCondition(world, x, y, z);
//	}
}
