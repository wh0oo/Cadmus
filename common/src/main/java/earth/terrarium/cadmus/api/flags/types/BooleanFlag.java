package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record BooleanFlag(boolean value) implements Flag<Boolean> {

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, Boolean> createArgument(String name) {
        return Commands.argument(name, BoolArgumentType.bool());
    }

    @Override
    public Flag<Boolean> getFromArgument(String name, CommandContext<CommandSourceStack> context) {
        return new BooleanFlag(BoolArgumentType.getBool(context, name));
    }

    @Override
    public void serialize(String name, CompoundTag tag) {
        tag.putBoolean(name, value);
    }

    @Override
    public Flag<Boolean> deserialize(String name, CompoundTag tag) {
        return new BooleanFlag(tag.getBoolean(name));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
