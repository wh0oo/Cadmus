package earth.terrarium.cadmus.mixins.common.fabric.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemoveBlockGoal.class)
public abstract class RemoveBlockGoalMixin {

    @Final
    @Shadow
    private Mob removerMob;

    // Prevent zombies from breaking down doors in protected chunks
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void cadmus$canUse(CallbackInfoReturnable<Boolean> cir) {
        if (!Protections.MOB_GRIEFING.canMobGrief(removerMob)) {
            cir.setReturnValue(false);
        }
    }
}
