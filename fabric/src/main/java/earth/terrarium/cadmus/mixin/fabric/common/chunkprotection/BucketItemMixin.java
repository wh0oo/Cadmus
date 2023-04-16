package earth.terrarium.cadmus.mixin.fabric.common.chunkprotection;

import earth.terrarium.cadmus.api.claims.ClaimApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    // Prevent players from using buckets in protected chunks
    @Inject(method = "use", at = @At("HEAD"))
    private void cadmus$use(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
        if (!ClaimApi.API.canPlaceBlock(player.level, player.blockPosition(), player)) {
            ci.cancel();
        }
    }

    @Inject(method = "emptyContents", at = @At("HEAD"))
    private void cadmus$emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult result, CallbackInfoReturnable<Boolean> ci) {
        if (player != null) {
            if (!ClaimApi.API.canPlaceBlock(level, pos, player)) {
                ci.cancel();
            }
        } else if (!ClaimApi.API.isClaimed(level, pos)) {
            ci.cancel();
        }
    }
}