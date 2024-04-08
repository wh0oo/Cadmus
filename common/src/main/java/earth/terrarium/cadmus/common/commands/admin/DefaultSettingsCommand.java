package earth.terrarium.cadmus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import earth.terrarium.cadmus.api.protections.ProtectionApi;
import earth.terrarium.cadmus.common.utils.CadmusSaveData;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class DefaultSettingsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        ProtectionApi.API.getSettings().forEach(setting ->
            dispatcher.register(Commands.literal("cadmus")
                .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("defaultsettings")
                    .then(Commands.literal(setting)
                        .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                boolean value = BoolArgumentType.getBool(context, "value");
                                CadmusSaveData.setDefaultClaimSetting(player.server, setting, value);
                                player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.setting.set", setting, value), false);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            boolean value = CadmusSaveData.getDefaultClaimSetting(player.server, setting);
                            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.setting.get", setting, value), false);
                            return 1;
                        })
                    )
                )
            )
        );
    }
}
