package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record FloatFlag(String id, Float value) implements Flag<Float> {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> createArgument(String argument) {
        return Commands.argument(argument, FloatArgumentType.floatArg());
    }

    @Override
    public Flag<Float> getFromArgument(String argument, CommandContext<CommandSourceStack> context) {
        return new FloatFlag(id, FloatArgumentType.getFloat(context, argument));
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putFloat(id, value);
    }

    @Override
    public Flag<Float> deserialize(CompoundTag tag) {
        return new FloatFlag(id, tag.getFloat(id));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
