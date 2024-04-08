package earth.terrarium.cadmus.common.protections.types.fabric;

import earth.terrarium.cadmus.common.protections.Protections;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

public final class BlockInteractProtectionImpl {

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!Protections.BLOCK_INTERACTIONS.canInteractWithBlock(player, hitResult.getBlockPos(), level.getBlockState(hitResult.getBlockPos()))) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }
}
