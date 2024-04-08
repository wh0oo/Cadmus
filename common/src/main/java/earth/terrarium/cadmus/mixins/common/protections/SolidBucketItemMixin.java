package earth.terrarium.cadmus.mixins.common.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SolidBucketItem.class)
public abstract class SolidBucketItemMixin {

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "emptyContents", at = @At("HEAD"), cancellable = true)
    public void cadmus$emptyContents(@Nullable Player player, Level level, BlockPos pos, BlockHitResult result, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = ((SolidBucketItem) (Object) this).getBlock().defaultBlockState();
        if (player != null) {
            if (!Protections.BLOCK_PLACING.canPlaceBlock(player, pos, state))
                cir.setReturnValue(false);
        } else {
            if (!Protections.BLOCK_PLACING.canPlaceBlock(level, pos, state))
                cir.setReturnValue(false);
        }
    }
}
