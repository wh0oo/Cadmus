package earth.terrarium.cadmus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import earth.terrarium.cadmus.api.flags.Flag;
import earth.terrarium.cadmus.api.flags.FlagApi;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class FlagCommands {

    private static final SimpleCommandExceptionType TEAM_HAS_NO_FLAGS = new SimpleCommandExceptionType(ConstantComponents.ADMIN_TEAM_HAS_NO_FLAGS);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("cadmus")
            .requires((commandSourceStack) -> commandSourceStack.hasPermission(2));

        FlagApi.API.getAllDefaults().forEach((name, flag) ->
            dispatcher.register(command
                .then(Commands.literal("adminclaims")
                    .then(Commands.literal("flags")
                        .then(Commands.literal("set")
                            .then(Commands.argument("team", StringArgumentType.string())
                                .suggests(AdminClaimCommands.ADMIN_TEAM_SUGGESTION_PROVIDER)
                                .then(Commands.literal(name)
                                    .then(flag.createArgument("value")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            String team = StringArgumentType.getString(context, "team");
                                            Flag<?> value = flag.getFromArgument("value", context);
                                            set(player, team, name, value);
                                            return 1;
                                        })
                                    )
                                )
                            )
                        )
                        .then(Commands.literal("remove")
                            .then(Commands.argument("team", StringArgumentType.string())
                                .suggests(AdminClaimCommands.ADMIN_TEAM_SUGGESTION_PROVIDER)
                                .then(Commands.literal(name)
                                    .executes(context -> {
                                        ServerPlayer player = context.getSource().getPlayerOrException();
                                        String team = StringArgumentType.getString(context, "team");
                                        remove(player, name, team);
                                        return 1;
                                    })
                                )
                            )
                        )
                        .then(Commands.literal("list")
                            .then(Commands.argument("team", StringArgumentType.string())
                                .suggests(AdminClaimCommands.ADMIN_TEAM_SUGGESTION_PROVIDER)
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String team = StringArgumentType.getString(context, "team");
                                    list(player, team);
                                    return 1;
                                })
                            )
                        )
                    )
                )
            )
        );
    }

    private static void set(ServerPlayer player, String team, String flagName, Flag<?> flag) throws CommandSyntaxException {
        if (!FlagApi.API.isAdminTeam(player.server, team)) throw AdminClaimCommands.ADMIN_TEAM_DOES_NOT_EXIST.create();

        FlagApi.API.getIdFromName(player.server, team).ifPresent(id -> {
            Flag<?> oldValue = FlagApi.API.getFlag(player.server, id, flagName);
            FlagApi.API.setFlag(player.server, id, flagName, flag);
            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.admin.set_flag", flagName, oldValue, flag), false);
            TeamApi.API.syncTeamInfo(player.server, id, true);
        });
    }

    private static void remove(ServerPlayer player, String flagName, String team) throws CommandSyntaxException {
        if (!FlagApi.API.isAdminTeam(player.server, team)) throw AdminClaimCommands.ADMIN_TEAM_DOES_NOT_EXIST.create();

        FlagApi.API.getIdFromName(player.server, team).ifPresent(id -> {
            FlagApi.API.removeFlag(player.server, id, flagName);
            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.admin.remove_flag", flagName), false);
            TeamApi.API.syncTeamInfo(player.server, id, true);
        });
    }

    private static void list(ServerPlayer player, String team) throws CommandSyntaxException {
        if (!FlagApi.API.isAdminTeam(player.server, team)) throw AdminClaimCommands.ADMIN_TEAM_DOES_NOT_EXIST.create();

        UUID id = FlagApi.API.getIdFromName(player.server, team).orElse(null);
        if (id == null) throw AdminClaimCommands.ADMIN_TEAM_DOES_NOT_EXIST.create();
        var flags = FlagApi.API.getAllAdminTeams(player.server).get(id);
        if (flags.isEmpty()) throw TEAM_HAS_NO_FLAGS.create();
        flags.forEach((name, flag) -> player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.admin.list_flags", name, flag), false));
    }
}
