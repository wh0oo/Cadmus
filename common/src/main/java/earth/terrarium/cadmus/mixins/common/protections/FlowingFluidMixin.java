package earth.terrarium.cadmus.mixins.common.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

    // Prevent outside fluids from flowing into the claimed chunk while still allowing fluids within the claim to flow.
    @Inject(method = "canSpreadTo", at = @At(value = "HEAD"), cancellable = true)
    private void cadmus$canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromState, Direction direction, BlockPos toPos, BlockState toState, FluidState toFluidState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof Level l &&
            Protections.BLOCK_PLACING.canPlaceBlock(l, fromPos, fromState) &&
            !Protections.BLOCK_PLACING.canPlaceBlock(l, toPos, toState)) {
            cir.cancel();
        }
    }
}
