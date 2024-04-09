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
        at = @At(
            value = "HEAD",
            target = "Lxaero/map/settings/ModSettings;displayClaims:Z",
            shift = At.Shift.AFTER
        )
    )
    // Update claimed chunks when the user toggles chunk visibility.
    private void cadmus$setOptionValue(ModOptions option, Object value, CallbackInfo ci) {
        if (option == ModOptions.PAC_CLAIMS) {
            XaerosCompat.updateAll(CadmusClient.level().dimension());
        }
    }

    @Inject(
        method = "setOptionDoubleValue",
        at = @At(
            value = "HEAD",
            target = "Lxaero/map/settings/ModSettings;displayClaims:Z",
            shift = At.Shift.AFTER
        )
    )
    // Update claimed chunks when the user changes transparency options.
    private void cadmus$setOptionDoubleValue(ModOptions option, double value, CallbackInfo ci) {
        if (option == ModOptions.PAC_CLAIMS_BORDER_OPACITY || option == ModOptions.PAC_CLAIMS_FILL_OPACITY) {
            XaerosCompat.updateAll(CadmusClient.level().dimension());
        }
    }
}