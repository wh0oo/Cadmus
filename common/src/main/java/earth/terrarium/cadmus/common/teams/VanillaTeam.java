package earth.terrarium.cadmus.common.teams;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.teams.Team;
import earth.terrarium.cadmus.common.utils.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VanillaTeam implements Team {

    private static final BiMap<String, UUID> TEAM_CACHE = HashBiMap.create();

    @Override
    public Optional<Component> getName(Level level, UUID id) {
        String name = TEAM_CACHE.inverse().get(id);
        if (name == null) return Optional.empty();
        PlayerTeam playerTeam = level.getScoreboard().getPlayerTeam(name);
        if (playerTeam == null) return Optional.empty();
        return Optional.of(playerTeam.getDisplayName());
    }

    @Override
    public Optional<ChatFormatting> getColor(Level level, UUID id) {
        String name = TEAM_CACHE.inverse().get(id);
        if (name == null) return Optional.empty();
        PlayerTeam playerTeam = level.getScoreboard().getPlayerTeam(name);
        ChatFormatting color = Optionull.map(playerTeam, PlayerTeam::getColor);
        return Optional.ofNullable(color == ChatFormatting.RESET ? ChatFormatting.AQUA : color);
    }

    @Override
    public List<GameProfile> getMembers(Level level, UUID id) {
        if (!(level instanceof ServerLevel serverLevel)) return List.of();
        String name = TEAM_CACHE.inverse().get(id);
        if (name == null) return List.of();

        PlayerTeam playerTeam = level.getScoreboard().getPlayerTeam(name);
        GameProfileCache profileCache = serverLevel.getServer().getProfileCache();
        if (playerTeam == null || profileCache == null) return List.of();

        List<GameProfile> profiles = new ArrayList<>();
        playerTeam.getPlayers().forEach(member -> profileCache.get(member).ifPresent(profiles::add));
        return profiles;
    }

    @Override
    public boolean isMember(Level level, UUID id, Player player) {
        String name = TEAM_CACHE.inverse().get(id);
        if (name == null) return false;
        PlayerTeam playerTeam = level.getScoreboard().getPlayerTeam(name);
        if (playerTeam == null) return false;
        return playerTeam.getPlayers().contains(player.getGameProfile().getName());
    }

    @Override
    public Optional<UUID> getId(Player player) {
        PlayerTeam team = player.getScoreboard().getPlayersTeam(player.getGameProfile().getName());
        if (team == null) return Optional.empty();
        return Optional.of(gerOrCreateId(team));
    }

    @Override
    public boolean canModifySettings(Player player) {
        return true;
    }

    public UUID gerOrCreateId(PlayerTeam team) {
        return TEAM_CACHE.computeIfAbsent(team.getName(), ModUtils::stringToUUID);
    }

    public UUID remove(PlayerTeam team) {
        return TEAM_CACHE.remove(team.getName());
    }

    public void transferClaims(MinecraftServer server, PlayerTeam team, String playerName) {
        UUID id = TEAM_CACHE.get(team.getName());
        ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
        if (player == null) return;

        server.getAllLevels().forEach(level ->
            ClaimApi.API.getOwnedClaims(level, player.getUUID()).ifPresent(claims ->
                ClaimApi.API.claim(level, id, claims)));

        server.getAllLevels().forEach(level -> ClaimApi.API.clear(level, player.getUUID()));
    }
}
