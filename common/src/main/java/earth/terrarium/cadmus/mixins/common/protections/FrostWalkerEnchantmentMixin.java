package earth.terrarium.cadmus.mixins.common.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FrostWalkerEnchantment.class)
public abstract class FrostWalkerEnchantmentMixin {

    // Prevent frost walker from creating frosted ice in protected chunks
    @Inject(method = "onEntityMoved", at = @At("HEAD"), cancellable = true)
    private static void cadmus$onEntityMoved(LivingEntity entity, Level level, BlockPos pos, int levelConflicting, CallbackInfo ci) {
        if (entity instanceof Player player && !Protections.BLOCK_PLACING.canPlaceBlock(player, player.blockPosition(), Blocks.FROSTED_ICE.defaultBlockState())) {
            ci.cancel();
        } else if (!Protections.MOB_GRIEFING.canMobGrief(entity)) {
            ci.cancel();
        }
    }
}
