package earth.terrarium.cadmus.mixins.common.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractArrow.class, ThrownTrident.class})
public abstract class OnHitMixin {

    // Prevent player-thrown projectiles from hurting entities in protected chunks
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void cadmus$onHitEntity(EntityHitResult result, CallbackInfo ci) {
        if (((AbstractArrow) (Object) this).getOwner() instanceof Player player) {
            if (!Protections.ENTITY_DAMAGE.canDamageEntity(player, result.getEntity())) {
                ci.cancel();
            }
        }
    }
}
