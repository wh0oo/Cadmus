package earth.terrarium.cadmus.common.protections.types.fabric;

import earth.terrarium.cadmus.common.protections.Protections;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;

public final class EntityInteractProtectionImpl {

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!Protections.ENTITY_INTERACTIONS.canInteractWithEntity(player, entity)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }
}
