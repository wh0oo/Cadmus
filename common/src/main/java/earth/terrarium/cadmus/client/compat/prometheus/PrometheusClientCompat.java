package earth.terrarium.cadmus.client.compat.prometheus;

import earth.terrarium.cadmus.api.protections.ProtectionApi;
import earth.terrarium.cadmus.common.compat.prometheus.CadmusOptions;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.api.roles.client.PageApi;

public class PrometheusClientCompat {

    public static void init() {
        ProtectionApi.API.getProtections().values().forEach(protection ->
            PermissionApi.API.addAutoComplete(protection.permission()));

        PageApi.API.register(CadmusOptions.SERIALIZER.id(), CadmusOptionsPage::new);
    }
}
