package earth.terrarium.cadmus.mixins.client.compat.xaero;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xaero.map.settings.ModOptions;

@Mixin(value = ModOptions.class, remap = false)
public abstract class ModOptionsMixin {

    @ModifyReturnValue(
        method = "isDisabledBecausePac",
        at = @At(value = "RETURN")
    )
    // Enable transparency options for cadmus claims
    private boolean cadmus$isDisabledBecausePac(boolean original) {
        return false;
    }
}