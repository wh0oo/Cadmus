package earth.terrarium.cadmus.mixins.common.fabric.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void cadmus$place(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player != null) {
            if (!Protections.BLOCK_PLACING.canPlaceBlock(player, context.getClickedPos(), level.getBlockState(context.getClickedPos()))) {
                cir.setReturnValue(InteractionResult.FAIL);
            }
        } else {
            if (!Protections.BLOCK_PLACING.canPlaceBlock(level, context.getClickedPos(), level.getBlockState(context.getClickedPos()))) {
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}
