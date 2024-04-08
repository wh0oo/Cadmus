package earth.terrarium.cadmus.common.protections.types.fabric;

import earth.terrarium.cadmus.common.protections.Protections;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.InteractionResult;

public final class BlockBreakProtectionImpl {

    public static void register() {
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!Protections.BLOCK_BREAKING.canBreakBlock(player, pos)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) ->
            Protections.BLOCK_BREAKING.canBreakBlock(player, pos));
    }
}
