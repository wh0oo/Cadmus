package earth.terrarium.cadmus.mixins.common.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ButtonBlock.class)
public abstract class ButtonBlockMixin {

    // Prevent projectiles from pressing wooden buttons in protected chunks
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void cadmus$entityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof Projectile projectile) {
            if (projectile.getOwner() instanceof Player player) {
                if (!Protections.BLOCK_INTERACTIONS.canInteractWithBlock(player, pos, state)) {
                    ci.cancel();
                }
            } else if (!Protections.MOB_GRIEFING.canMobGrief(entity, pos)) {
                ci.cancel();
            }
        }
    }
}
