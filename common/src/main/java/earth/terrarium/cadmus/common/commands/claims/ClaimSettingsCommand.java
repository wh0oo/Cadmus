package earth.terrarium.cadmus.common.commands.claims;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.cadmus.Cadmus;
import earth.terrarium.cadmus.api.protections.Protection;
import earth.terrarium.cadmus.api.protections.ProtectionApi;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.common.compat.prometheus.PrometheusCompat;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import earth.terrarium.cadmus.common.utils.CadmusSaveData;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Locale;

public class ClaimSettingsCommand {

    private static final SimpleCommandExceptionType NO_PERMISSION_TEAM = new SimpleCommandExceptionType(ConstantComponents.NO_PERMISSION_TEAM);
    private static final SimpleCommandExceptionType NO_PERMISSION_ROLE = new SimpleCommandExceptionType(ConstantComponents.NO_PERMISSION_ROLE);
    private static final SimpleCommandExceptionType INVALID_STATE = new SimpleCommandExceptionType(ConstantComponents.INVALID_STATE);

    // I'm gonna claim the entire TRI STATE AREA
    public static final SuggestionProvider<CommandSourceStack> TRI_STATE_SUGGESTION_PROVIDER = (context, builder) ->
        SharedSuggestionProvider.suggest((List.of("true", "false", "default")), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        ProtectionApi.API.getSettings().forEach(setting ->
            dispatcher.register(Commands.literal("claim")
                .then(Commands.literal("settings")
                    .then(Commands.literal(setting)
                        .then(Commands.argument("value", StringArgumentType.string())
                            .suggests(TRI_STATE_SUGGESTION_PROVIDER)
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                checkPermissions(player, ProtectionApi.API.getProtection(setting));
                                String value = StringArgumentType.getString(context, "value");
                                TriState state = stringToState(value);

                                CadmusSaveData.setClaimSetting(player.server, TeamApi.API.getId(player), setting, state);
                                player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.setting.set", setting, value), false);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            TriState state = CadmusSaveData.getClaimSetting(player.server, TeamApi.API.getId(player), setting);
                            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.setting.get", setting, stateToString(state)), false);
                            return 1;
                        })
                    )
                )
            )
        );
    }

    private static TriState stringToState(String input) throws CommandSyntaxException {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "true" -> TriState.TRUE;
            case "false" -> TriState.FALSE;
            case "default" -> TriState.UNDEFINED;
            default -> throw INVALID_STATE.create();
        };
    }

    private static String stateToString(TriState state) {
        return switch (state) {
            case TRUE -> "true";
            case FALSE -> "false";
            case UNDEFINED -> "default";
        };
    }

    private static void checkPermissions(ServerPlayer player, Protection protection) throws CommandSyntaxException {
        if (player.hasPermissions(2)) return;

        if (!TeamApi.API.canModifySettings(player)) throw NO_PERMISSION_TEAM.create();

        if (Cadmus.IS_PROMETHEUS_LOADED && !PrometheusCompat.hasPermission(player, protection.permission())) {
            throw NO_PERMISSION_ROLE.create();
        }
    }
}
