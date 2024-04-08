package earth.terrarium.cadmus.common.commands.claims;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class ClaimInfoCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("claim")
            .then(Commands.literal("info")
                .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                        getInfo(player, pos);
                        return 1;
                    }))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    getInfo(player, player.chunkPosition());
                    return 1;
                })
            )
        );
    }

    private static void getInfo(ServerPlayer player, ChunkPos pos) {
        ClaimApi.API.getClaim(player.serverLevel(), pos).ifPresentOrElse(claim -> {
            boolean chunkLoaded = claim.rightBoolean();
            Component name = TeamApi.API.getName(player.level(), claim.left());

            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.info.claimed_by", name.getString()).copy().withStyle(name.getStyle()), false);
            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.info.id", claim.left()), false);
            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.info.position", pos.x, pos.z), false);
            player.displayClientMessage(chunkLoaded ? ConstantComponents.CHUNK_LOADED_TRUE : ConstantComponents.CHUNK_LOADED_FALSE, false);
        }, () -> player.displayClientMessage(ConstantComponents.NOT_CLAIMED, false));
    }
}
