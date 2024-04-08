package earth.terrarium.cadmus.common.utils;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.common.network.NetworkHandler;
import earth.terrarium.cadmus.common.network.packets.ClientboundSyncClaimsPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Contract;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ModUtils {

    private static final int MAX_CHUNKS_PER_PACKET = 500;

    private static final ChatFormatting[] COLORS = new ChatFormatting[]{
        ChatFormatting.DARK_BLUE,
        ChatFormatting.DARK_GREEN,
        ChatFormatting.DARK_AQUA,
        ChatFormatting.DARK_RED,
        ChatFormatting.DARK_PURPLE,
        ChatFormatting.GOLD,
        ChatFormatting.BLUE,
        ChatFormatting.GREEN,
        ChatFormatting.AQUA,
        ChatFormatting.RED,
        ChatFormatting.LIGHT_PURPLE,
        ChatFormatting.YELLOW,
    };

    @Contract(pure = true)
    @ExpectPlatform
    public static boolean isMixinModLoaded(String modId) {
        throw new NotImplementedException();
    }

    public static UUID stringToUUID(String string) {
        return UUID.nameUUIDFromBytes(string.getBytes(StandardCharsets.UTF_8));
    }

    public static ChatFormatting uuidToColor(UUID id) {
        return COLORS[Math.abs(id.hashCode()) % COLORS.length];
    }

    /**
     * Sends all claims, packet splitting in batches of {@link #MAX_CHUNKS_PER_PACKET} to the player joining the server.
     */
    public static void sendJoinPackets(ServerPlayer player) {
        if (!NetworkHandler.CHANNEL.canSendToPlayer(player, ClientboundSyncClaimsPacket.TYPE)) return;
        for (var level : player.server.getAllLevels()) {
            Object2ObjectMap<UUID, Object2BooleanMap<ChunkPos>> allClaims = ClaimApi.API.getAllClaimsByOwner(player.serverLevel());
            if (allClaims.isEmpty()) continue;

            Object2ObjectMap<UUID, Object2BooleanMap<ChunkPos>> batch = new Object2ObjectOpenHashMap<>();
            int count = 0;

            for (var entry : allClaims.entrySet()) {
                batch.put(entry.getKey(), entry.getValue());
                count++;

                if (count == MAX_CHUNKS_PER_PACKET || count == allClaims.size()) {
                    NetworkHandler.CHANNEL.sendToPlayer(new ClientboundSyncClaimsPacket(level.dimension(), batch), player);
                    batch = new Object2ObjectOpenHashMap<>();
                    count = 0;
                }
            }
        }
    }

    public static Component translatableWithStyle(String key, Object... args) {
        return Component.literal(CommonUtils.serverTranslatable(key, args).getString());
    }
}
