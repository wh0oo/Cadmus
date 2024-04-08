package earth.terrarium.cadmus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.common.utils.CadmusSaveData;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BypassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cadmus")
            .requires((commandSourceStack) -> commandSourceStack.hasPermission(2))
            .then(Commands.literal("bypass")
                .executes(context -> {
                    bypass(context.getSource().getPlayerOrException());
                    return 1;
                })
            )
        );
    }

    private static void bypass(ServerPlayer player) {
        CadmusSaveData.toggleBypass(player.server, player.getUUID());
        Component name = Component.literal(player.getGameProfile().getName()).withStyle(TeamApi.API.getColor(player.level(), player.getUUID()));
        if (CadmusSaveData.canBypass(player.server, player.getUUID())) {
            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.bypass.enable", name), false);
        } else {
            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.bypass.disable", name), false);
        }
    }
}
