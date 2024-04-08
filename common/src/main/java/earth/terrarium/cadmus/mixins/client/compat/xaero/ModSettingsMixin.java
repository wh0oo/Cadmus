package earth.terrarium.cadmus.mixins.client.compat.xaero;

import earth.terrarium.cadmus.client.CadmusClient;
import earth.terrarium.cadmus.client.compat.xaero.XaerosCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.settings.ModOptions;
import xaero.map.settings.ModSettings;

@Mixin(value = ModSettings.class, remap = false)
public abstract class ModSettingsMixin {

    @Inject(
        method = "setOptionValue",
        at = @At(value = "HEAD",
                 target = "Lxaero/map/settings/ModSettings;displayClaims:Z"
        )
    )
    // Update claimed chunks when the user toggles chunk visibility.
    private void cadmus$setOptionValue(ModOptions par1EnumOptions, Object value, CallbackInfo ci) {
        XaerosCompat.updateAll(CadmusClient.level().dimension());
    }
}