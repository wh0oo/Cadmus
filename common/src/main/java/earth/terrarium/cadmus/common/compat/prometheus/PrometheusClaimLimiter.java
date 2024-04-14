package earth.terrarium.cadmus.common.compat.prometheus;

import earth.terrarium.cadmus.api.claims.limit.ClaimLimiter;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.prometheus.api.roles.RoleApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;

public class PrometheusClaimLimiter implements ClaimLimiter {

    @Override
    public int getMaxClaims(MinecraftServer server, UUID id) {
        ServerLevel level = server.overworld();
        Player player = server.getPlayerList().getPlayer(id);
        if (player != null) {
            return RoleApi.API.getNonNullOption(player, CadmusOptions.SERIALIZER).maxClaims();
        }

        int maxClaims = 0;
        for (var member : TeamApi.API.getSelected().getMembers(level, id)) {
            maxClaims = Math.max(maxClaims, RoleApi.API.forceGetNonNullOption(level, member, CadmusOptions.SERIALIZER).maxClaims());
        }
        return maxClaims;
    }

    @Override
    public int getMaxChunkLoadedClaims(MinecraftServer server, UUID id) {
        ServerLevel level = server.overworld();
        Player player = server.getPlayerList().getPlayer(id);
        if (player != null) {
            return RoleApi.API.getNonNullOption(player, CadmusOptions.SERIALIZER).maxChunkLoaded();
        }

        int maxChunkLoaded = 0;
        Set<UUID> members = TeamApi.API.getSelected().getMembers(level, id);
        for (var member : members) {
            maxChunkLoaded = Math.max(maxChunkLoaded, RoleApi.API.forceGetNonNullOption(level, member, CadmusOptions.SERIALIZER).maxChunkLoaded());
        }
        return maxChunkLoaded;
    }
}
