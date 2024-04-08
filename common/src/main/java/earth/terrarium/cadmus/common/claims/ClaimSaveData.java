package earth.terrarium.cadmus.common.claims;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimSaveData extends SaveHandler {

    private static final Map<ResourceKey<Level>, ClaimSaveData> CLIENT_HANDLERS = new HashMap<>();

    private final Object2ObjectMap<ChunkPos, ObjectBooleanPair<UUID>> claims = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<UUID, Object2BooleanMap<ChunkPos>> claimsById = new Object2ObjectOpenHashMap<>();

    @Override
    public void loadData(CompoundTag tag) {
        tag.getAllKeys().forEach(stringId -> {
            UUID id = UUID.fromString(stringId);
            long[] values = tag.getLongArray(stringId);
            for (long value : values) {
                int x = BlockPos.getX(value);
                int z = BlockPos.getZ(value);
                boolean chunkLoaded = BlockPos.getY(value) == 1;
                ChunkPos pos = new ChunkPos(x, z);
                this.claims.put(pos, new ObjectBooleanImmutablePair<>(id, chunkLoaded));
                this.claimsById.computeIfAbsent(id, uuid -> new Object2BooleanOpenHashMap<>()).put(pos, chunkLoaded);
            }
        });
    }

    @Override
    public void saveData(CompoundTag tag) {
        this.claimsById.forEach((id, claim) -> {
            long[] values = new long[claim.size()];
            int i = 0;
            for (var entry : claim.object2BooleanEntrySet()) {
                ChunkPos pos = entry.getKey();
                boolean chunkLoaded = entry.getBooleanValue();
                values[i] = BlockPos.asLong(pos.x, chunkLoaded ? 1 : 0, pos.z);
                i++;
            }
            tag.put(id.toString(), new LongArrayTag(values));
        });
    }

    public Object2ObjectMap<ChunkPos, ObjectBooleanPair<UUID>> claims() {
        return this.claims;
    }

    public Object2ObjectMap<UUID, Object2BooleanMap<ChunkPos>> claimsById() {
        return this.claimsById;
    }

    public static ClaimSaveData read(Level level) {
        return read(level, HandlerType.create(readClient(level.dimension()), ClaimSaveData::new), "cadmus_claims");
    }

    public static ClaimSaveData readClient(ResourceKey<Level> dimension) {
        return CLIENT_HANDLERS.computeIfAbsent(dimension, dim -> new ClaimSaveData());
    }

    public static void clearClientClaims() {
        CLIENT_HANDLERS.clear();
    }
}
