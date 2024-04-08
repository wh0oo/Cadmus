package earth.terrarium.cadmus.mixins.common.fabric.protections;

import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.cadmus.common.protections.Protections;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow
    @Final
    private ObjectArrayList<BlockPos> toBlow;

    @Shadow
    @Final
    private Level level;

    // Prevent explosions from destroying blocks in protected chunks
    @SuppressWarnings("UnreachableCode")
    @Inject(
        method = "explode",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V",
            ordinal = 1
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void cadmus$explode(CallbackInfo ci, @Local List<Entity> entities) {
        Explosion explosion = (Explosion) (Object) this;
        toBlow.removeIf(pos ->
            !Protections.BLOCK_EXPLOSIONS.canExplodeBlock(level, pos, explosion));
        entities.removeIf(entity ->
            !Protections.ENTITY_EXPLOSIONS.canExplodeEntity(entity, explosion));
    }
}
