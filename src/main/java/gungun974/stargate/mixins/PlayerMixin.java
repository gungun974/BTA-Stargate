package gungun974.stargate.mixins;

import gungun974.stargate.IPlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(remap = false, value = Player.class)
public abstract class PlayerMixin extends Mob implements IPlayer {
	@Unique
	private int poissonDamage = 0;
	@Unique
	private int poissonTick = 0;

	public PlayerMixin(@Nullable World world) {
		super(world);
	}

	@Shadow
	public abstract boolean hurt(Entity attacker, int damage, DamageType type);

	public void bta_Stargate$poissonHurt() {
		if (this.poissonTick == 0 && this.poissonDamage == 0) {
			this.poissonTick = 100;
		}
		this.poissonDamage += 25;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	void tick(CallbackInfo ci) {
		if (this.world == null) {
			return;
		}

		if (this.world.isClientSide) {
			this.poissonDamage = 0;
		} else if (this.poissonDamage > 0) {
			if (this.poissonTick <= 0) {
				Player player = (Player) (Object) this;
				int damage = 1;

				this.lastDamage = 0;
				this.hurt(null, damage, DamageType.GENERIC);

				if (player.getHealth() <= 0) {
					this.poissonDamage = 0;
					this.poissonTick = 0;
				} else {
					this.poissonDamage -= damage;
					this.poissonTick = 5;
				}
			}
			this.poissonTick--;
		}
	}
}
