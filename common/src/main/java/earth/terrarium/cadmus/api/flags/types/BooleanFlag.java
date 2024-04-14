package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;

public record BooleanFlag(String id, Boolean value) implements Flag<Boolean> {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> createArgument(String name) {
        return Commands.argument(name, BoolArgumentType.bool());
    }

    @Override
    public Flag<Boolean> getFromArgument(String argument, CommandContext<CommandSourceStack> context) {
        return new BooleanFlag(id, BoolArgumentType.getBool(context, argument));
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putBoolean(id, value);
    }

    @Override
    public Flag<Boolean> deserialize(CompoundTag tag) {
        return new BooleanFlag(id, tag.getBoolean(id));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
