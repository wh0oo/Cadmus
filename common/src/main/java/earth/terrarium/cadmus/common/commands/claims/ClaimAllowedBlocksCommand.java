package earth.terrarium.cadmus.common.commands.claims;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import earth.terrarium.cadmus.common.utils.CadmusSaveData;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class ClaimAllowedBlocksCommand {

    private static final SimpleCommandExceptionType BLOCK_NOT_ADDED = new SimpleCommandExceptionType(ConstantComponents.BLOCK_NOT_ADDED);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("claim")
            .then(Commands.literal("settings")
                .then(Commands.literal("allowedBlocks")
                    .then(Commands.literal("add")
                        .then(Commands.argument("value", BlockStateArgument.block(buildContext))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                BlockState block = BlockStateArgument.getBlock(context, "value").getState();
                                addBlock(player, block);
                                return 1;
                            })
                        )
                    )
                    .then(Commands.literal("remove")
                        .then(Commands.argument("value", BlockStateArgument.block(buildContext))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                BlockState block = BlockStateArgument.getBlock(context, "value").getState();
                                removeBlock(player, block);
                                return 1;
                            })
                        )
                    )
                    .then(Commands.literal("list")
                        .executes(context -> {
                            listBlocks(context.getSource().getPlayerOrException());
                            return 1;
                        })
                    )
                    .executes(context -> {
                        listBlocks(context.getSource().getPlayerOrException());
                        return 1;
                    })
                )
            )
        );
    }

    private static void addBlock(ServerPlayer player, BlockState block) {
        CadmusSaveData.addAllowedBlock(player.server, TeamApi.API.getId(player), block.getBlock());
        player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.setting.add_allowed_block", block.getBlock().getName()), false);
    }

    private static void removeBlock(ServerPlayer player, BlockState block) throws CommandSyntaxException {
        UUID id = TeamApi.API.getId(player);
        if (!CadmusSaveData.isBlockAllowed(player.server, id, block.getBlock())) throw BLOCK_NOT_ADDED.create();
        CadmusSaveData.removeAllowedBlock(player.server, id, block.getBlock());
        player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.setting.remove_allowed_block", block.getBlock().getName()), false);
    }

    private static void listBlocks(ServerPlayer player) {
        CadmusSaveData.getAllowedBlocks(player.server, TeamApi.API.getId(player)).forEach(key -> {
            Block block = BuiltInRegistries.BLOCK.get(key);
            player.displayClientMessage(ModUtils.translatableWithStyle("command.cadmus.setting.list_allowed_blocks", block == Blocks.AIR ? key : block.getName()), false);
        });
    }
}
