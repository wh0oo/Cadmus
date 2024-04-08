package earth.terrarium.cadmus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.flags.FlagApi;
import earth.terrarium.cadmus.api.flags.types.ChatFormattingFlag;
import earth.terrarium.cadmus.api.flags.types.StringFlag;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.common.commands.claims.ClaimCommand;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import earth.terrarium.cadmus.common.flags.Flags;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.Collection;
import java.util.UUID;

public class AdminClaimCommands {

    private static final SimpleCommandExceptionType ADMIN_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType(ConstantComponents.ADMIN_TEAM_ALREADY_EXISTS);
    public static final SimpleCommandExceptionType ADMIN_TEAM_DOES_NOT_EXIST = new SimpleCommandExceptionType(ConstantComponents.ADMIN_TEAM_DOES_NOT_EXIST);

    public static final SuggestionProvider<CommandSourceStack> ADMIN_TEAM_SUGGESTION_PROVIDER = (context, builder) -> {
        Collection<String> names = FlagApi.API.getAllAdminTeamNames(context.getSource().getServer());
        return SharedSuggestionProvider.suggest(names, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cadmus")
            .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
            .then(Commands.literal("adminclaims")
                .then(Commands.literal("create")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String team = StringArgumentType.getString(context, "id");
                            create(player, team);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests(ADMIN_TEAM_SUGGESTION_PROVIDER)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String team = StringArgumentType.getString(context, "id");
                            remove(player, team);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("claim")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests(ADMIN_TEAM_SUGGESTION_PROVIDER)
                        .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                String team = StringArgumentType.getString(context, "id");
                                claim(player, pos, team);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ChunkPos pos = player.chunkPosition();
                            String team = StringArgumentType.getString(context, "id");
                            claim(player, pos, team);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("unclaim")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests(ADMIN_TEAM_SUGGESTION_PROVIDER)
                        .then(Commands.argument("pos", ColumnPosArgument.columnPos())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                ChunkPos pos = ColumnPosArgument.getColumnPos(context, "pos").toChunkPos();
                                String team = StringArgumentType.getString(context, "id");
                                unclaim(player, pos, team);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ChunkPos pos = player.chunkPosition();
                            String team = StringArgumentType.getString(context, "id");
                            unclaim(player, pos, team);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("clear")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests(ADMIN_TEAM_SUGGESTION_PROVIDER)
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String team = StringArgumentType.getString(context, "id");
                            unclaimAll(player, team);
                            return 1;
                        })
                    )
                )
            )
        );
    }

    private static void create(ServerPlayer player, String team) throws CommandSyntaxException {
        if (FlagApi.API.isAdminTeam(player.server, team)) throw ADMIN_TEAM_ALREADY_EXISTS.create();
        UUID id = FlagApi.API.createAdminTeam(player.server, team);
        FlagApi.API.setFlag(player.server, id, Flags.DISPLAY_NAME.id(), new StringFlag(team));
        FlagApi.API.setFlag(player.server, id, Flags.COLOR.id(), new ChatFormattingFlag(ChatFormatting.LIGHT_PURPLE));
        player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.admin.create", team), false);
        TeamApi.API.syncTeamInfo(player.server, id, true);
    }

    private static void remove(ServerPlayer player, String team) throws CommandSyntaxException {
        if (!FlagApi.API.isAdminTeam(player.server, team)) throw ADMIN_TEAM_DOES_NOT_EXIST.create();
        UUID id = FlagApi.API.getIdFromName(player.server, team).orElse(null);
        if (id == null) throw ADMIN_TEAM_DOES_NOT_EXIST.create();

        FlagApi.API.removeAdminTeam(player.server, id);
        player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.admin.remove", team), false);
    }

    private static void claim(ServerPlayer player, ChunkPos pos, String team) throws CommandSyntaxException {
        UUID id = FlagApi.API.getIdFromName(player.server, team).orElse(null);
        if (id == null) throw AdminClaimCommands.ADMIN_TEAM_DOES_NOT_EXIST.create();

        ClaimCommand.checkClaimed(player.serverLevel(), pos);

        ClaimApi.API.claim(player.serverLevel(), id, pos, false);

        player.displayClientMessage(ModUtils.translatableWithStyle(
            "command.cadmus.info.claimed_admin_chunk_at",
            pos.x, pos.z
        ), false);
    }

    private static void unclaim(ServerPlayer player, ChunkPos pos, String team) throws CommandSyntaxException {
        UUID id = FlagApi.API.getIdFromName(player.server, team).orElse(null);
        if (id == null) throw AdminClaimCommands.ADMIN_TEAM_DOES_NOT_EXIST.create();

        ClaimApi.API.unclaim(player.serverLevel(), id, pos);

        player.displayClientMessage(ModUtils.translatableWithStyle(
            "command.cadmus.info.unclaimed_admin_chunk_at",
            pos.x, pos.z
        ), false);
    }

    private static void unclaimAll(ServerPlayer player, String team) throws CommandSyntaxException {
        UUID id = FlagApi.API.getIdFromName(player.server, team).orElse(null);
        if (id == null) throw AdminClaimCommands.ADMIN_TEAM_DOES_NOT_EXIST.create();

        int oldClaimsCount = ClaimCommand.getClaimsCount(player.serverLevel(), id, false);
        ClaimApi.API.clear(player.level(), id);
        int diff = oldClaimsCount - ClaimCommand.getClaimsCount(player, false);
        player.displayClientMessage(ModUtils.translatableWithStyle(
            "command.cadmus.info.unclaimed_all_admin",
            diff
        ), false);
    }
}
