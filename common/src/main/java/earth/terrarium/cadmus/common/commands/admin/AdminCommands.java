package earth.terrarium.cadmus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.claims.limit.ClaimLimitApi;
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

    public static final SuggestionProvider<CommandSourceStack> TEAM_SUGGESTION_PROVIDER = (context, builder) ->
        SharedSuggestionProvider.suggest(
            TeamApi.API.getAllTeams(context.getSource().getServer()),
            builder,
            UUID::toString,
            id -> TeamApi.API.getName(context.getSource().getServer(), id)
        );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cadmus")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("admin")
                .then(Commands.literal("claim")
                    .then(Commands.argument("id", UuidArgument.uuid())
                        .suggests(TEAM_SUGGESTION_PROVIDER)
                        .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                            .then(Commands.argument("chunkload", BoolArgumentType.bool())
                                .executes(context -> {
                                    ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                    boolean chunkload = BoolArgumentType.getBool(context, "chunkload");
                                    UUID id = UuidArgument.getUuid(context, "id");
                                    claim(context.getSource(), pos, chunkload, id);
                                    return 1;
                                }))
                            .executes(context -> {
                                ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                UUID id = UuidArgument.getUuid(context, "id");
                                claim(context.getSource(), pos, false, id);
                                return 1;
                            }))
                        .executes(context -> {
                            UUID id = UuidArgument.getUuid(context, "id");
                            claim(context.getSource(), context.getSource().getPlayerOrException().chunkPosition(), false, id);
                            return 1;
                        })
                    )
                )

                .then(Commands.literal("unclaim")
                    .then(Commands.argument("id", UuidArgument.uuid())
                        .suggests(TEAM_SUGGESTION_PROVIDER)
                        .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                            .executes(context -> {
                                ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                UUID id = UuidArgument.getUuid(context, "id");
                                unclaim(context.getSource(), pos, id);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ChunkPos pos = player.chunkPosition();
                            UUID id = UuidArgument.getUuid(context, "id");
                            unclaim(context.getSource(), pos, id);
                            return 1;
                        })
                    )
                    .executes(context -> {
                        unclaim(context.getSource());
                        return 1;
                    })
                )
                .then(Commands.literal("clear")
                    .then(Commands.argument("id", UuidArgument.uuid())
                        .suggests(TEAM_SUGGESTION_PROVIDER)
                        .executes(context -> {
                            UUID id = UuidArgument.getUuid(context, "id");
                            unclaimAll(context.getSource(), id);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("clearall")
                    .executes(context -> {
                        clearAll(context.getSource());
                        return 1;
                    })
                )
            )
        );
    }

    private static void claim(CommandSourceStack source, ChunkPos pos, boolean chunkload, UUID id) throws CommandSyntaxException {
        if (!TeamApi.API.teamExists(source.getServer(), id)) throw TEAM_DOES_NOT_EXIST.create();
        ClaimCommand.checkClaimed(source.getLevel(), pos);

        ClaimApi.API.claim(source.getLevel(), id, pos, chunkload);

        int claimsCount = ClaimCommand.getClaimsCount(source.getLevel(), id, chunkload);
        int maxClaims = chunkload ? ClaimLimitApi.API.getMaxChunkLoadedClaims(id) : ClaimLimitApi.API.getMaxClaims(id);

        source.sendSuccess(() -> ModUtils.translatableWithStyle(
            chunkload ?
                "command.cadmus.info.chunk_loaded_chunk_at" :
                "command.cadmus.info.claimed_chunk_at",
            pos.x, pos.z,
            claimsCount, maxClaims
        ), false);
    }

    private static void unclaim(CommandSourceStack source, ChunkPos pos, UUID id) throws CommandSyntaxException {
        if (!TeamApi.API.teamExists(source.getServer(), id)) throw TEAM_DOES_NOT_EXIST.create();

        var claim = ClaimApi.API.getClaim(source.getLevel(), pos);
        if (claim.isEmpty()) throw UnclaimCommand.NOT_CLAIMED.create();

        ClaimApi.API.unclaim(source.getLevel(), id, pos);

        int claimsCount = ClaimCommand.getClaimsCount(source.getLevel(), id, false);
        int maxClaims = ClaimLimitApi.API.getMaxClaims(id);
        source.sendSuccess(() -> ModUtils.translatableWithStyle(
            "command.cadmus.info.unclaimed_chunk_at",
            pos.x, pos.z,
            claimsCount, maxClaims
        ), false);
    }

    private static void unclaim(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        var claim = ClaimApi.API.getClaim(source.getLevel(), player.chunkPosition());
        if (claim.isEmpty()) throw UnclaimCommand.NOT_CLAIMED.create();
        unclaim(source, player.chunkPosition(), claim.get().left());
    }

    private static void unclaimAll(CommandSourceStack source, UUID id) throws CommandSyntaxException {
        if (!TeamApi.API.teamExists(source.getServer(), id)) throw TEAM_DOES_NOT_EXIST.create();

        int oldClaimsCount = ClaimCommand.getClaimsCount(source.getLevel(), id, false);
        ClaimApi.API.clear(source.getLevel(), id);
        int diff = oldClaimsCount - ClaimCommand.getClaimsCount(source.getLevel(), id, false);
        source.sendSuccess(() -> ModUtils.translatableWithStyle(
            "command.cadmus.info.unclaimed_all",
            diff
        ), false);
    }

    private static void clearAll(CommandSourceStack source) {
        ClaimApi.API.clearAll(source.getServer());
        source.sendSuccess(() -> ModUtils.translatableWithStyle("command.cadmus.info.admin_clear"), false);
    }
}
