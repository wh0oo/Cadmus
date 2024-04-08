package earth.terrarium.cadmus.mixins.common.protections;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BasePressurePlateBlock.class)
public abstract class BasePressurePlateBlockMixin {

    // Prevent players from activating pressure plates in protected chunks
    @WrapWithCondition(
        method = "entityInside",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/BasePressurePlateBlock;checkPressed(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)V"
        )
    )
    private boolean cadmus$entityInside(BasePressurePlateBlock block, Entity entity, Level level, BlockPos pos, BlockState state, int signalStrength) {
        if (entity instanceof Player player) {
            return Protections.BLOCK_INTERACTIONS.canInteractWithBlock(player, pos, state);
        }
        return Protections.MOB_GRIEFING.canMobGrief(entity, pos);
    }
}
