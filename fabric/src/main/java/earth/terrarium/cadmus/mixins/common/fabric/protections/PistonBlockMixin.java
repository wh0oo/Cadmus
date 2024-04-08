package earth.terrarium.cadmus.mixins.common.fabric.protections;

import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBlockMixin {

    @Inject(
        method = "moveBlocks",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;",
            shift = At.Shift.BEFORE
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        cancellable = true
    )
    private void cadmus$moveBlocks(Level level, BlockPos pos, Direction facing, boolean extending, CallbackInfoReturnable<Boolean> cir, @Local PistonStructureResolver resolver, @Local(ordinal = 0, argsOnly = true) BlockPos offsetPos) {
        if (!extending) return;
        if (!Protections.BLOCK_PLACING.canPlaceBlock(level, pos, level.getBlockState(pos))) {
            return;
        }

        BlockPos target = offsetPos.relative(facing, resolver.getToPush().size());
        if (!Protections.BLOCK_PLACING.canPlaceBlock(level, target, level.getBlockState(offsetPos))) {
            cir.setReturnValue(false);
        }
    }
}
