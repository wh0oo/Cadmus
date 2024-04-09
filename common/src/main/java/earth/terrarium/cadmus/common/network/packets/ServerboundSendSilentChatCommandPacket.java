package earth.terrarium.cadmus.common.network.packets;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.cadmus.Cadmus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Consumer;

public record ServerboundSendSilentChatCommandPacket(
    String command
) implements Packet<ServerboundSendSilentChatCommandPacket> {

    public static final ServerboundPacketType<ServerboundSendSilentChatCommandPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundSendSilentChatCommandPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundSendSilentChatCommandPacket> implements ServerboundPacketType<ServerboundSendSilentChatCommandPacket> {

        public Type() {
            super(
                ServerboundSendSilentChatCommandPacket.class,
                new ResourceLocation(Cadmus.MOD_ID, "send_silent_chat_command"),
                ObjectByteCodec.create(
                    ByteCodec.STRING.fieldOf(ServerboundSendSilentChatCommandPacket::command),
                    ServerboundSendSilentChatCommandPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundSendSilentChatCommandPacket packet) {
            return player -> Objects.requireNonNull(player.getServer()).getCommands().performPrefixedCommand(
                player.createCommandSourceStack().withSuppressedOutput(),
                packet.command
            );
        }
    }
}
