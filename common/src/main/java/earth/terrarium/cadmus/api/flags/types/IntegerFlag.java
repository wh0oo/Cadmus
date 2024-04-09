package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record IntegerFlag(int value) implements Flag<Integer> {

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Integer> createArgument(String name) {
        return Commands.argument(name, IntegerArgumentType.integer());
    }

    @Override
    public Flag<Integer> getFromArgument(String name, CommandContext<CommandSourceStack> context) {
        return new IntegerFlag(IntegerArgumentType.getInteger(context, name));
    }

    @Override
    public void serialize(String name, CompoundTag tag) {
        tag.putInt(name, value);
    }

    @Override
    public Flag<Integer> deserialize(String name, CompoundTag tag) {
        return new IntegerFlag(tag.getInt(name));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}