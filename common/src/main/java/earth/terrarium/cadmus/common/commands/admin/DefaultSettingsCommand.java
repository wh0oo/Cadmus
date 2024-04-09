package earth.terrarium.cadmus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import earth.terrarium.cadmus.api.protections.ProtectionApi;
import earth.terrarium.cadmus.common.utils.CadmusSaveData;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DefaultSettingsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        ProtectionApi.API.getSettings().forEach(setting ->
            dispatcher.register(Commands.literal("cadmus")
                .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("defaultsettings")
                    .then(Commands.literal(setting)
                        .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(context -> {
                                set(context.getSource(), setting, BoolArgumentType.getBool(context, "value"));
                                return 1;
                            })
                        )
                        .executes(context -> {
                            get(context.getSource(), setting);
                            return 1;
                        })
                    )
                )
            )
        );
    }

    private static void set(CommandSourceStack source, String setting, boolean value) {
        CadmusSaveData.setDefaultClaimSetting(source.getServer(), setting, value);
        source.sendSuccess(() -> ModUtils.translatableWithStyle("command.cadmus.setting.set", setting, value), false);
    }

    private static void get(CommandSourceStack source, String setting) {
        boolean value = CadmusSaveData.getDefaultClaimSetting(source.getServer(), setting);
        source.sendSuccess(() -> ModUtils.translatableWithStyle("command.cadmus.setting.get", setting, value), false);
    }
}
