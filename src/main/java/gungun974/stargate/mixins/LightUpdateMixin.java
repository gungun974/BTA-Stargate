package gungun974.stargate.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import gungun974.stargate.gate.blocks.BlockLogicStargate;
import gungun974.stargate.gate.components.CamouflageComponent;
import gungun974.stargate.gate.tiles.TileEntityStargate;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.LightUpdate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LightUpdate.class, remap = false)
public abstract class LightUpdateMixin {

	@Final
	@Shadow
	public LightLayer layer;

	@ModifyVariable(
		method = "performLightUpdate",
		at = @At(
			value = "STORE",
			ordinal = 2
		),
		name = "blockLightValue"
	)
	private int modifyBlockLightValue(
		int blockLightValue,
		@Local(argsOnly = true) World world,
		@Local(name = "x") int x,
		@Local(name = "y") int y,
		@Local(name = "z") int z
	) {
		if (this.layer != LightLayer.Block) {
			return blockLightValue;
		}

		BlockLogicStargate blockLogicStargate = world.getBlockLogic(x, y, z, BlockLogicStargate.class);
		if (blockLogicStargate == null) {
			return blockLightValue;
		}

		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityStargate)) {
			return 0;
		}

		TileEntityStargate stargateTile = (TileEntityStargate) tileEntity;
		CamouflageComponent camouflageComponent = stargateTile.getCamouflageComponent();
		if (camouflageComponent.hasCamouflage()) {
			return Blocks.lightEmission[camouflageComponent.getBlockId()];
		}
		return 0;
	}

	@ModifyVariable(
		method = "performLightUpdate",
		at = @At(
			value = "STORE",
			ordinal = 1
		),
		name = "blockLightOpacity"
	)
	private int modifyBlockLightOpacity(
		int blockLightOpacity,
		@Local(argsOnly = true) World world,
		@Local(name = "x") int x,
		@Local(name = "y") int y,
		@Local(name = "z") int z
	) {
		if (this.layer != LightLayer.Block) {
			return blockLightOpacity;
		}

		BlockLogicStargate blockLogicStargate = world.getBlockLogic(x, y, z, BlockLogicStargate.class);
		if (blockLogicStargate == null) {
			return blockLightOpacity;
		}

		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityStargate) {
			TileEntityStargate stargateTile = (TileEntityStargate) tileEntity;
			CamouflageComponent camouflageComponent = stargateTile.getCamouflageComponent();
			if (camouflageComponent.hasCamouflage()) {
				int opacity = Blocks.lightBlock[camouflageComponent.getBlockId()];
				return opacity == 0 ? 1 : opacity;
			}
		}
		return blockLightOpacity;
	}
}
