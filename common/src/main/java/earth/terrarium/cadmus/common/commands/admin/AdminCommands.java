package earth.terrarium.cadmus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.common.commands.claims.ClaimCommand;
import earth.terrarium.cadmus.common.commands.claims.UnclaimCommand;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

public class AdminCommands {

    public static final SimpleCommandExceptionType TEAM_DOES_NOT_EXIST = new SimpleCommandExceptionType(ConstantComponents.TEAM_DOES_NOT_EXIST);

    public static final SuggestionProvider<CommandSourceStack> TEAM_SUGGESTION_PROVIDER = (context, builder) -> {
        ServerPlayer player = context.getSource().getPlayerOrException();
        return SharedSuggestionProvider.suggest(
            ClaimApi.API.getAllClaimsByOwner(player.serverLevel()).keySet(),
            builder,
            UUID::toString,
            id -> TeamApi.API.getName(player.getServer(), id)
        );
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cadmus")
            .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
            .then(Commands.literal("admin")
                .then(Commands.literal("claim")
                    .then(Commands.argument("id", UuidArgument.uuid())
                        .suggests(TEAM_SUGGESTION_PROVIDER)
                        .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                            .then(Commands.argument("chunkload", BoolArgumentType.bool())
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                    boolean chunkload = BoolArgumentType.getBool(context, "chunkload");
                                    UUID id = UuidArgument.getUuid(context, "id");
                                    claim(player, pos, chunkload, id);
                                    return 1;
                                }))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                UUID id = UuidArgument.getUuid(context, "id");
                                claim(player, pos, false, id);
                                return 1;
                            }))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            UUID id = UuidArgument.getUuid(context, "id");
                            claim(player, player.chunkPosition(), false, id);
                            return 1;
                        })
                    )
                )

                .then(Commands.literal("unclaim")
                    .then(Commands.argument("id", UuidArgument.uuid())
                        .suggests(TEAM_SUGGESTION_PROVIDER)
                        .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                UUID id = UuidArgument.getUuid(context, "id");
                                unclaim(player, pos, id);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ChunkPos pos = player.chunkPosition();
                            UUID id = UuidArgument.getUuid(context, "id");
                            unclaim(player, pos, id);
                            return 1;
                        })
                    )
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        unclaim(player);
                        return 1;
                    })
                )
                .then(Commands.literal("clear")
                    .then(Commands.argument("id", UuidArgument.uuid())
                        .suggests(TEAM_SUGGESTION_PROVIDER)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            UUID id = UuidArgument.getUuid(context, "id");
                            unclaimAll(player, id);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("clearall")
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        clearAll(player);
                        return 1;
                    })
                )
            )
        );
    }

    private static void claim(ServerPlayer player, ChunkPos pos, boolean chunkload, UUID id) throws CommandSyntaxException {
        if (!ClaimApi.API.teamExists(player.serverLevel(), id)) throw TEAM_DOES_NOT_EXIST.create();
        ClaimCommand.checkClaimed(player.serverLevel(), pos);

        ClaimApi.API.claim(player.level(), id, pos, chunkload);

        int claimsCount = ClaimCommand.getClaimsCount(player, chunkload);
        int maxClaims = chunkload ? ClaimApi.API.getMaxChunkLoadedClaims(player) : ClaimApi.API.getMaxClaims(player);

        player.displayClientMessage(ModUtils.translatableWithStyle(
            chunkload ?
                "command.cadmus.info.chunk_loaded_chunk_at" :
                "command.cadmus.info.claimed_chunk_at",
            pos.x, pos.z,
            claimsCount, maxClaims
        ), false);
    }

    private static void unclaim(ServerPlayer player, ChunkPos pos, UUID id) throws CommandSyntaxException {
        if (!ClaimApi.API.teamExists(player.serverLevel(), id)) throw TEAM_DOES_NOT_EXIST.create();

        var claim = ClaimApi.API.getClaim(player.serverLevel(), pos);
        if (claim.isEmpty()) throw UnclaimCommand.NOT_CLAIMED.create();

        ClaimApi.API.unclaim(player.level(), id, pos);

        int claimsCount = ClaimCommand.getClaimsCount(player, false);
        int maxClaims = ClaimApi.API.getMaxClaims(player);
        player.displayClientMessage(ModUtils.translatableWithStyle(
            "command.cadmus.info.unclaimed_chunk_at",
            pos.x, pos.z,
            claimsCount, maxClaims
        ), false);
    }

    private static void unclaim(ServerPlayer player) throws CommandSyntaxException {
        var claim = ClaimApi.API.getClaim(player.serverLevel(), player.chunkPosition());
        if (claim.isEmpty()) throw UnclaimCommand.NOT_CLAIMED.create();
        unclaim(player, player.chunkPosition(), claim.get().left());
    }

    private static void unclaimAll(ServerPlayer player, UUID id) throws CommandSyntaxException {
        if (!ClaimApi.API.teamExists(player.serverLevel(), id)) throw TEAM_DOES_NOT_EXIST.create();

        int oldClaimsCount = ClaimCommand.getClaimsCount(player, false);
        ClaimApi.API.clear(player.level(), id);
        int diff = oldClaimsCount - ClaimCommand.getClaimsCount(player, false);
        player.displayClientMessage(ModUtils.translatableWithStyle(
            "command.cadmus.info.unclaimed_all",
            diff
        ), false);
    }

    private static void clearAll(ServerPlayer player) {
        ClaimApi.API.clearAll(player.server);
        player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.info.admin_clear"), false);
    }
}
