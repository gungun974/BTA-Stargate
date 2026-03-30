package gungun974.stargate.core;

import gungun974.stargate.IPlayer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class DangerousItemFood extends ItemFood {
	public DangerousItemFood(String name, String namespaceId, int id, int healAmount, int ticksPerHeal, boolean favouriteWolfMeat, int maxStackSize) {
		super(name, namespaceId, id, healAmount, ticksPerHeal, favouriteWolfMeat, maxStackSize);
	}


	@Override
	public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
		if (itemstack.consumeItem(entityplayer)) {
			entityplayer.heal(1);
			((IPlayer) entityplayer).bta_Stargate$poissonHurt();
			world.playSoundAtEntity(entityplayer, entityplayer, this.getTicksPerHeal() >= 10 ? "random.bite_extended" : "random.bite", 0.5F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F, 1.1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F);
		}

		return itemstack;
	}
}
