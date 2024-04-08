package earth.terrarium.cadmus.common.protections.types;

import earth.terrarium.cadmus.api.flags.types.BooleanFlag;
import earth.terrarium.cadmus.api.protections.Protection;
import earth.terrarium.cadmus.common.flags.Flags;
import earth.terrarium.cadmus.common.protections.ClaimSettings;
import earth.terrarium.cadmus.common.utils.CadmusGameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;

public final class EntityExplosionProtection implements Protection {

    @Override
    public String setting() {
        return ClaimSettings.CAN_EXPLODE_ENTITIES;
    }

    @Override
    public String permission() {
        return "cadmus.entity_explosions";
    }

    @Override
    public String personalPermission() {
        return "cadmus.personal.entity_explosions";
    }

    @Override
    public BooleanFlag flag() {
        return Flags.ENTITY_EXPLOSIONS;
    }

    @Override
    public GameRules.Key<GameRules.BooleanValue> gameRule() {
        return CadmusGameRules.DO_CLAIMED_ENTITY_EXPLOSIONS;
    }

    public boolean canExplodeEntity(Entity entity, Explosion explosion) {
        return entity.level().isClientSide() || getId(entity.level(), entity.chunkPosition()).map(id ->
            explosion.getIndirectSourceEntity() instanceof Player player ?
                isPlayerAllowed(player, id) :
                isEntityAllowed(entity, id)
        ).orElse(true);
    }
}
