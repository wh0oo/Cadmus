package earth.terrarium.cadmus.common.compat.prometheus;

import earth.terrarium.cadmus.api.claims.limit.ClaimLimiter;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class PrometheusClaimLimiter implements ClaimLimiter {

    @Override
    public int getMaxClaims(MinecraftServer server, UUID id) {
        return PrometheusCompat.getMaxClaims(server, id);
    }

    @Override
    public int getMaxChunkLoadedClaims(MinecraftServer server, UUID id) {
        return PrometheusCompat.getMaxChunkLoadedClaims(server, id);
    }

    // TODO sync when roles update
}
