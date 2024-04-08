package earth.terrarium.cadmus.common.protections.types.fabric;

import earth.terrarium.cadmus.common.protections.Protections;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;

public final class EntityDamageProtectionImpl {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!Protections.ENTITY_DAMAGE.canDamageEntity(player, entity)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }
}
