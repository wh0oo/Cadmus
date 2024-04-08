package earth.terrarium.cadmus.mixins.common.neoforge.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @Shadow
    @Final
    private Fluid content;

    @Inject(method = "emptyContents*", at = @At("HEAD"), cancellable = true)
    public void cadmus$emptyContents(@Nullable Player player, Level level, BlockPos pos, BlockHitResult result, @Nullable ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        if (player != null) {
            if (!Protections.BLOCK_PLACING.canPlaceBlock(player, pos, content.defaultFluidState().createLegacyBlock()))
                cir.setReturnValue(false);
        } else {
            if (!Protections.BLOCK_PLACING.canPlaceBlock(level, pos, content.defaultFluidState().createLegacyBlock()))
                cir.setReturnValue(false);
        }
    }
}
