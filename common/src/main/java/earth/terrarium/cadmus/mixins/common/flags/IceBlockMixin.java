package earth.terrarium.cadmus.mixins.common.flags;

import earth.terrarium.cadmus.common.flags.Flags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin {

    @Inject(method = "melt", at = @At(value = "HEAD"), cancellable = true)
    private void cadmus$melt(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (!Flags.ICE_MELT.get(level, new ChunkPos(pos))) {
            ci.cancel();
        }
    }
}
