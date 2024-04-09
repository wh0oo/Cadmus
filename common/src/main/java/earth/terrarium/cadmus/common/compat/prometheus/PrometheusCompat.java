package earth.terrarium.cadmus.common.compat.prometheus;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.cadmus.api.claims.limit.ClaimLimitApi;
import earth.terrarium.cadmus.api.protections.ProtectionApi;
import earth.terrarium.prometheus.api.events.ServerRolesUpdatedEvent;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.api.roles.options.RoleOptionsApi;
import net.minecraft.world.entity.player.Player;

public class PrometheusCompat {

    public static void init() {
        RoleOptionsApi.API.register(CadmusOptions.SERIALIZER);

        ProtectionApi.API.getProtections().values().forEach(protection ->
            PermissionApi.API.addDefaultPermission(protection.personalPermission(), TriState.TRUE));

        ClaimLimitApi.API.register(new PrometheusClaimLimiter());

        ServerRolesUpdatedEvent.register(event -> ClaimLimitApi.API.calculate(event.server()));
    }

    public static boolean hasPermission(Player player, String permission) {
        return PermissionApi.API.getPermission(player, permission).map(false);
    }
}
