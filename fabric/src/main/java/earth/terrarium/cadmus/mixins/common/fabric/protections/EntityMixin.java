package earth.terrarium.cadmus.mixins.common.fabric.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    // Prevent entities from being damaged by players in protected chunks
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void cadmus$isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player player) {
            if (!Protections.ENTITY_DAMAGE.canDamageEntity(player, (Entity) (Object) this)) {
                cir.setReturnValue(false);
            }
        }
    }
}
