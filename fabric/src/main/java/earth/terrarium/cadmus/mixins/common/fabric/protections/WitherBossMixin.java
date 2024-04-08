package earth.terrarium.cadmus.mixins.common.fabric.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Entity {

    @Shadow
    private int destroyBlocksTick;

    public WitherBossMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // Prevent withers from destroying blocks in protected chunks
    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void cadmus$customServerAiStep(CallbackInfo ci) {
        if (!Protections.MOB_GRIEFING.canMobGrief(this)) {
            destroyBlocksTick = 20;
        }
    }
}
