package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record StringFlag(String id, String value) implements Flag<String> {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> createArgument(String argument) {
        return Commands.argument(argument, StringArgumentType.greedyString());
    }

    @Override
    public Flag<String> getFromArgument(String argument, CommandContext<CommandSourceStack> context) {
        return new StringFlag(id, StringArgumentType.getString(context, argument));
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString(id, value);
    }

    @Override
    public Flag<String> deserialize(CompoundTag tag) {
        return new StringFlag(id, tag.getString(id));
    }

    @Override
    public String toString() {
        return value.isEmpty() ? "\"\"" : value;
    }
}
