package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record IntegerFlag(String id, Integer value) implements Flag<Integer> {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> createArgument(String argument) {
        return Commands.argument(argument, IntegerArgumentType.integer());
    }

    @Override
    public Flag<Integer> getFromArgument(String argument, CommandContext<CommandSourceStack> context) {
        return new IntegerFlag(id, IntegerArgumentType.getInteger(context, argument));
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putInt(id, value);
    }

    @Override
    public Flag<Integer> deserialize(CompoundTag tag) {
        return new IntegerFlag(id, tag.getInt(id));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
