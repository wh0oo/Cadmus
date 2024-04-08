package earth.terrarium.cadmus.api.flags;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface Flag<T> {

    /**
     * Gets the value of the flag.
     *
     * @return The value of the flag.
     */
    T get();

    RequiredArgumentBuilder<CommandSourceStack, T> createArgument(String name);

    Flag<T> getFromArgument(String name, CommandContext<CommandSourceStack> context);

    void serialize(String name, CompoundTag tag);

    Flag<T> deserialize(String name, CompoundTag tag);

    default String id() {
        return FlagApi.API.getId(this);
    }

    /**
     * Gets the value of the flag for a specific admin chunk.
     *
     * @param level The level.
     * @param pos   The position of the chunk.
     * @return The value of the flag.
     */
    default T get(Level level, ChunkPos pos) {
        return level instanceof ServerLevel serverLevel ?
            FlagApi.API.<T>getFlag(serverLevel, pos, id())
                .map(Flag::get)
                .orElse(this.get()) :
            this.get();
    }

    /**
     * Gets the value of the flag for a specific admin team.
     *
     * @param server The server.
     * @param id     The id of the admin team.
     * @return The value of the flag.
     */
    default T get(MinecraftServer server, UUID id) {
        return FlagApi.API.<T>getFlag(server, id, id()).get();
    }
}