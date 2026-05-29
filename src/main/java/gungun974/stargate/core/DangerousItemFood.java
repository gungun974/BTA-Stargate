package gungun974.stargate.core;

import gungun974.stargate.IPlayer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DangerousItemFood extends ItemFood {
	public DangerousItemFood(String name, String namespaceId, int id, int healAmount, int ticksPerHeal, boolean favouriteWolfMeat, int maxStackSize) {
		super(name, namespaceId, id, healAmount, ticksPerHeal, favouriteWolfMeat, maxStackSize);
	}


	public @Nullable ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (selfStack.consumeItem(player)) {
			player.heal(1);
			((IPlayer) player).bta_Stargate$poissonHurt();
			world.playSoundAtEntity(player, player, this.getTicksPerHeal(selfStack) >= 10 ? "random.bite_extended" : "random.bite", 0.5F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F, 1.1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F);
		}

		return selfStack;
	}
}
