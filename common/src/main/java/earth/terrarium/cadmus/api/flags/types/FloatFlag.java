package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record FloatFlag(float value) implements Flag<Float> {

    @Override
    public Float get() {
        return value;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Float> createArgument(String name) {
        return Commands.argument(name, FloatArgumentType.floatArg());
    }

    @Override
    public Flag<Float> getFromArgument(String name, CommandContext<CommandSourceStack> context) {
        return new FloatFlag(FloatArgumentType.getFloat(context, name));
    }

    @Override
    public void serialize(String name, CompoundTag tag) {
        tag.putFloat(name, value);
    }

    @Override
    public Flag<Float> deserialize(String name, CompoundTag tag) {
        return new FloatFlag(tag.getFloat(name));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
