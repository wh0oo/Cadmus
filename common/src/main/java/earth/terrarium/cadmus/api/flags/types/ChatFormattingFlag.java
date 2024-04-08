package earth.terrarium.cadmus.api.flags.types;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.cadmus.api.flags.Flag;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.nbt.CompoundTag;

public record ChatFormattingFlag(ChatFormatting value) implements Flag<ChatFormatting> {

    @Override
    public ChatFormatting get() {
        return value;
    }

    @Override
    public RequiredArgumentBuilder<CommandSourceStack, ChatFormatting> createArgument(String name) {
        return Commands.argument(name, ColorArgument.color());
    }

    @Override
    public Flag<ChatFormatting> getFromArgument(String name, CommandContext<CommandSourceStack> context) {
        return new ChatFormattingFlag(ColorArgument.getColor(context, name));
    }

    @Override
    public void serialize(String name, CompoundTag tag) {
        tag.putInt(name, value.getId());
    }

    @Override
    public Flag<ChatFormatting> deserialize(String name, CompoundTag tag) {
        return new ChatFormattingFlag(ChatFormatting.getById(tag.getInt(name)));
    }

    @Override
    public String toString() {
        return value.getName();
    }
}
