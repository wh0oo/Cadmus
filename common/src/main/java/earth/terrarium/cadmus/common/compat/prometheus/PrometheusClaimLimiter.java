package earth.terrarium.cadmus.common.compat.prometheus;

import com.mojang.authlib.GameProfile;
import earth.terrarium.cadmus.api.claims.limit.ClaimLimiter;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.prometheus.api.roles.RoleApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PrometheusClaimLimiter implements ClaimLimiter {

    @Override
    public int getMaxClaims(MinecraftServer server, UUID id) {
        ServerLevel level = server.overworld();
        Optional<Player> player = TeamApi.API.getPlayer(server, id);
        if (player.isPresent()) {
            return RoleApi.API.getNonNullOption(player.get(), CadmusOptions.SERIALIZER).maxClaims();
        }

        int maxClaims = 0;
        for (var member : TeamApi.API.getSelected().getMembers(level, id)) {
            maxClaims = Math.max(maxClaims, RoleApi.API.forceGetNonNullOption(level, member.getId(), CadmusOptions.SERIALIZER).maxClaims());
        }
        return maxClaims;
    }

    @Override
    public int getMaxChunkLoadedClaims(MinecraftServer server, UUID id) {
        ServerLevel level = server.overworld();
        Optional<Player> player = TeamApi.API.getPlayer(server, id);
        if (player.isPresent()) {
            return RoleApi.API.getNonNullOption(player.get(), CadmusOptions.SERIALIZER).maxChunkLoaded();
        }

        int maxChunkLoaded = 0;
        List<GameProfile> members = TeamApi.API.getSelected().getMembers(level, id);
        for (var member : members) {
            maxChunkLoaded = Math.max(maxChunkLoaded, RoleApi.API.forceGetNonNullOption(level, member.getId(), CadmusOptions.SERIALIZER).maxChunkLoaded());
        }
        return maxChunkLoaded;
    }
}
