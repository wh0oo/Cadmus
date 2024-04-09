package earth.terrarium.cadmus.api.claims.limit;

import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public interface ClaimLimiter {

    int getMaxClaims(MinecraftServer server, UUID id);

    int getMaxChunkLoadedClaims(MinecraftServer server, UUID id);
}
