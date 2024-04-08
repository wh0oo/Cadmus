package earth.terrarium.cadmus.common.teams;

import com.mojang.authlib.GameProfile;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.flags.FlagApi;
import earth.terrarium.cadmus.api.teams.Team;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.client.CadmusClient;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import earth.terrarium.cadmus.common.flags.Flags;
import earth.terrarium.cadmus.common.network.NetworkHandler;
import earth.terrarium.cadmus.common.network.packets.ClientboundSyncAllTeamInfoPacket;
import earth.terrarium.cadmus.common.network.packets.ClientboundSyncTeamInfo;
import earth.terrarium.cadmus.common.utils.CadmusSaveData;
import earth.terrarium.cadmus.common.utils.ModUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCharImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectCharPair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TeamApiImpl implements TeamApi {

    private static final Map<Player, Component> LAST_MESSAGE = new WeakHashMap<>();

    private final Object2IntMap<Team> teams = new Object2IntOpenHashMap<>();
    private Team selected;

    @Override
    public void register(Team team, int weight) {
        this.teams.put(team, weight);
    }

    @Override
    public Team getSelected() {
        if (this.selected == null) {
            int maxWeight = Integer.MIN_VALUE;
            for (var entry : teams.object2IntEntrySet()) {
                if (entry.getIntValue() > maxWeight) {
                    maxWeight = entry.getIntValue();
                    this.selected = entry.getKey();
                }
            }
        }
        return this.selected;
    }

    @Override
    public Component getName(Level level, UUID id) {
        return getSelected().getName(level, id).orElseGet(() -> {
            if (!level.isClientSide()) {
                MinecraftServer server = level.getServer();
                if (server == null) return ConstantComponents.UNKNOWN;

                if (FlagApi.API.isAdminTeam(server, id)) {
                    return Component.literal(Flags.DISPLAY_NAME.get(server, id));
                }

                GameProfileCache cache = server.getProfileCache();
                if (cache == null) return ConstantComponents.UNKNOWN;
                GameProfile profile = cache.get(id).orElse(null);
                if (profile == null) return ConstantComponents.UNKNOWN;
                return Component.literal(profile.getName());
            } else if (CadmusClient.TEAM_INFO.containsKey(id)) {
                return Component.literal(CadmusClient.TEAM_INFO.get(id).left());
            }

            return ConstantComponents.UNKNOWN;
        }).copy().withStyle(getColor(level, id));
    }

    @Override
    public Component getName(MinecraftServer server, UUID id) {
        return getName(server.overworld(), id);
    }

    @Override
    public ChatFormatting getColor(Level level, UUID id) {
        return getSelected().getColor(level, id).orElseGet(() -> {
            if (!level.isClientSide()) {
                MinecraftServer server = level.getServer();
                if (server == null) return ModUtils.uuidToColor(id);

                if (FlagApi.API.isAdminTeam(server, id)) {
                    return Flags.COLOR.get(server, id);
                }
            } else if (CadmusClient.TEAM_INFO.containsKey(id)) {
                return ChatFormatting.getByCode(CadmusClient.TEAM_INFO.get(id).rightChar());
            }

            return ModUtils.uuidToColor(id);
        });
    }

    @Override
    public ChatFormatting getColor(MinecraftServer server, UUID id) {
        return getColor(server.overworld(), id);
    }

    @Override
    public boolean isMember(Level level, UUID id, Player player) {
        return getSelected().isMember(level, id, player) || player.getUUID().equals(id);
    }

    @Override
    public UUID getId(@NotNull Player player) {
        return getSelected().getId(player).orElse(player.getUUID());
    }

    @Override
    public boolean isOnTeam(@NotNull Player player) {
        return getSelected().getId(player).isPresent();
    }

    @Override
    public Optional<Player> getPlayer(MinecraftServer server, UUID id) {
        return Optional.ofNullable(server.getPlayerList().getPlayer(id));
    }

    @Override
    public boolean canModifySettings(@NotNull Player player) {
        return !isOnTeam(player) || getSelected().canModifySettings(player);
    }

    @Override
    public void removeTeam(MinecraftServer server, UUID id) {
        server.getAllLevels().forEach(level -> ClaimApi.API.clear(level, id));
        CadmusSaveData.removeTeam(server, id);
    }

    @Override
    public void syncAllTeamInfo(MinecraftServer server) {
        Map<UUID, ObjectCharPair<String>> teamInfo = new HashMap<>();

        server.getAllLevels().forEach(level ->
            ClaimApi.API.getAllClaimsByOwner(level).keySet().forEach(id -> {
                String name = getName(server, id).getString();
                char color = getColor(server, id).getChar();
                teamInfo.put(id, new ObjectCharImmutablePair<>(name, color));
            })
        );

        FlagApi.API.getAllAdminTeams(server).keySet().forEach(id -> {
            String name = getName(server, id).getString();
            char color = getColor(server, id).getChar();
            teamInfo.put(id, new ObjectCharImmutablePair<>(name, color));
        });

        server.getPlayerList().getPlayers().forEach(player -> {
            UUID id = player.getUUID();
            if (!teamInfo.containsKey(id)) {
                String name = getName(server, id).getString();
                char color = getColor(server, id).getChar();
                teamInfo.put(id, new ObjectCharImmutablePair<>(name, color));
            }
        });

        NetworkHandler.sendToAllClientPlayers(new ClientboundSyncAllTeamInfoPacket(teamInfo), server);
    }

    @Override
    public void syncTeamInfo(MinecraftServer server, UUID id, boolean updateMaps) {
        String name = getName(server, id).getString();
        char color = getColor(server, id).getChar();
        NetworkHandler.sendToAllClientPlayers(new ClientboundSyncTeamInfo(id, name, color, updateMaps), server);
    }

    @Override
    public void displayTeamName(ServerPlayer player, ChunkPos pos) {
        if (player == null) return;
        Component message = ClaimApi.API.getClaim(player.level(), player.chunkPosition()).map(claim -> {
            String greeting = Flags.GREETING.get(player.serverLevel(), player.chunkPosition());
            return greeting.isBlank() ?
                getName(player.level(), claim.left()) :
                Component.literal(greeting).withStyle(ChatFormatting.GOLD);
        }).orElseGet(() -> {
            String farewell = Flags.FAREWELL.get(player.serverLevel(), pos);
            return farewell.isBlank() ?
                ConstantComponents.WILDERNESS :
                Component.literal(farewell).withStyle(ChatFormatting.GOLD);
        });

        if (message.equals(LAST_MESSAGE.get(player))) return;
        LAST_MESSAGE.put(player, message);
        player.displayClientMessage(message, true);
    }

    @Override
    public void displayTeamNameToAll(MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach(this::displayTeamName);
    }
}
