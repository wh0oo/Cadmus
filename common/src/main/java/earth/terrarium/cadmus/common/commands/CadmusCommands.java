package earth.terrarium.cadmus.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.cadmus.common.commands.admin.*;
import earth.terrarium.cadmus.common.commands.claims.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public class CadmusCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        ClaimCommand.register(dispatcher);
        ClaimInfoCommand.register(dispatcher);
        ClaimAreaCommand.register(dispatcher);
        ClaimShapeCommand.register(dispatcher);
        UnclaimCommand.register(dispatcher);
        UnclaimAreaCommand.register(dispatcher);
        ClaimSettingsCommand.register(dispatcher);
        ClaimAllowedBlocksCommand.register(dispatcher, context);

        AdminCommands.register(dispatcher);
        DefaultSettingsCommand.register(dispatcher);
        BypassCommand.register(dispatcher);
        FlagCommands.register(dispatcher);
        AdminClaimCommands.register(dispatcher);
    }
}
