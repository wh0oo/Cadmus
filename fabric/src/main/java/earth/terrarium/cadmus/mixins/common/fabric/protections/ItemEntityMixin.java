package earth.terrarium.cadmus.mixins.common.fabric.protections;

import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // Prevent players from picking up items in protected chunks unless they dropped them
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void cadmus$playerTouch(Player player, CallbackInfo ci) {
        if (!Protections.ITEM_PICKUP.canPickupItem(player, (ItemEntity) (Object) this)) {
            ci.cancel();
        }
    }
}
