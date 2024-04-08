package earth.terrarium.cadmus.mixins.common.neoforge.protections;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @WrapWithCondition(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/FireBlock;checkBurnOut(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;ILnet/minecraft/core/Direction;)V"
        )
    )
    private boolean cadmus$checkBurnOut(FireBlock ignored1, Level level, BlockPos toPos, int ignored2, RandomSource ignored3, int ignored4, Direction direction, BlockState fromState, ServerLevel ignored5, BlockPos fromPos, RandomSource ignored6) {
        return !Protections.BLOCK_PLACING.canPlaceBlock(level, fromPos, fromState) ||
            Protections.BLOCK_PLACING.canPlaceBlock(level, toPos, level.getBlockState(toPos));
    }
}
