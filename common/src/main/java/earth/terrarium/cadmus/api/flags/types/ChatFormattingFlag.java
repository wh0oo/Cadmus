package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.nbt.CompoundTag;

import java.util.Locale;

public record ChatFormattingFlag(String id, ChatFormatting value) implements Flag<ChatFormatting> {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> createArgument(String argument) {
        return Commands.argument(argument, ColorArgument.color());
    }

    @Override
    public Flag<ChatFormatting> getFromArgument(String argument, CommandContext<CommandSourceStack> context) {
        return new ChatFormattingFlag(id, ColorArgument.getColor(context, argument));
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString(id, value.getName().toLowerCase(Locale.ROOT));
    }

    @Override
    public Flag<ChatFormatting> deserialize(CompoundTag tag) {
        return new ChatFormattingFlag(id, ChatFormatting.getByName(tag.getString(id)));
    }

    @Override
    public String toString() {
        return value.getName();
    }
}
