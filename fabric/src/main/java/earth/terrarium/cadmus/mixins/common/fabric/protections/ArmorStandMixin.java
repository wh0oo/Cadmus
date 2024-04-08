package earth.terrarium.cadmus.mixins.common.fabric.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends Entity {

    public ArmorStandMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // Prevent armor stands from being interacted with in protected chunks
    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void cadmus$interactAt(Player player, Vec3 vec, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Protections.ENTITY_INTERACTIONS.canInteractWithEntity(player, this)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    // Prevent armor stands from being affected by explosions in protected chunks
    @Inject(method = "ignoreExplosion", at = @At("HEAD"), cancellable = true)
    private void cadmus$ignoreExplosion(CallbackInfoReturnable<Boolean> cir) {
        if (!Protections.MOB_GRIEFING.canMobGrief(this)) {
            cir.setReturnValue(true);
        }
    }
}
