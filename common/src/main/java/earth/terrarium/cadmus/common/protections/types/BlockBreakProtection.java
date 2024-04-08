package earth.terrarium.cadmus.common.protections.types;

import earth.terrarium.cadmus.api.flags.types.BooleanFlag;
import earth.terrarium.cadmus.api.protections.Protection;
import earth.terrarium.cadmus.common.flags.Flags;
import earth.terrarium.cadmus.common.protections.ClaimSettings;
import earth.terrarium.cadmus.common.utils.CadmusGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.UUID;

public final class BlockBreakProtection implements Protection {

    @Override
    public String setting() {
        return ClaimSettings.CAN_BREAK_BLOCKS;
    }

    @Override
    public String permission() {
        return "cadmus.block_breaking";
    }

    @Override
    public String personalPermission() {
        return "cadmus.personal.block_breaking";
    }

    @Override
    public BooleanFlag flag() {
        return Flags.BLOCK_BREAK;
    }

    @Override
    public GameRules.Key<GameRules.BooleanValue> gameRule() {
        return CadmusGameRules.DO_CLAIMED_BLOCK_BREAKING;
    }

    public boolean canBreakBlock(Player player, BlockPos pos) {
        return player.level().isClientSide() || getId(player.level(), pos).map(id ->
            isPlayerAllowed(player, id) || isBlockAllowed(player.level(), id, pos)).orElse(true);
    }

    public boolean canBreakBlock(Level level, UUID player, BlockPos pos) {
        Player playerEntity = level.getPlayerByUUID(player);
        return playerEntity == null || canBreakBlock(playerEntity, pos);
    }
}
