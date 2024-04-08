package earth.terrarium.cadmus.mixins.common.fabric.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Entity {

    public EnderDragonMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // Prevent the ender dragon from destroying blocks in protected chunks
    @Inject(method = "checkWalls", at = @At("HEAD"), cancellable = true)
    private void cadmus$checkWalls(AABB area, CallbackInfoReturnable<Boolean> cir) {
        if (!Protections.MOB_GRIEFING.canMobGrief(this)) {
            cir.setReturnValue(false);
        }
    }
}
