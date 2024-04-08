package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record StringFlag(String value) implements Flag<String> {

    @Override
    public String get() {
        return value;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, String> createArgument(String name) {
        return Commands.argument(name, StringArgumentType.greedyString());
    }

    @Override
    public Flag<String> getFromArgument(String name, CommandContext<CommandSourceStack> context) {
        return new StringFlag(StringArgumentType.getString(context, name));
    }

    @Override
    public void serialize(String name, CompoundTag tag) {
        tag.putString(name, value);
    }

    @Override
    public Flag<String> deserialize(String name, CompoundTag tag) {
        return new StringFlag(tag.getString(name));
    }

    @Override
    public String toString() {
        return value;
    }
}
