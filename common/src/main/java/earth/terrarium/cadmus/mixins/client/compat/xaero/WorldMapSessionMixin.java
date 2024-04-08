package earth.terrarium.cadmus.mixins.client.compat.xaero;

import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.cadmus.client.compat.xaero.XaerosCompat;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xaero.map.WorldMapSession;
import xaero.map.highlight.HighlighterRegistry;

@Mixin(value = WorldMapSession.class, remap = false)
public abstract class WorldMapSessionMixin {

    @Inject(
        method = "init",
        at = @At(value = "INVOKE", target = "Lxaero/map/highlight/HighlighterRegistry;end()V"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void cadmus$registerHighlighters(ClientPacketListener connection, long biomeZoomSeed, CallbackInfo ci, @Local HighlighterRegistry highlightRegistry) {
        XaerosCompat.registerHighlighters(highlightRegistry);
    }
}