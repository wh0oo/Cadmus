package earth.terrarium.cadmus.common.protections.types.neoforge;

import earth.terrarium.cadmus.Cadmus;
import earth.terrarium.cadmus.common.protections.Protections;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;

@Mod.EventBusSubscriber(modid = Cadmus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
final class ItemPickupProtectionImpl {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onItemPickup(EntityItemPickupEvent event) {
        if (!Protections.ITEM_PICKUP.canPickupItem(event.getEntity(), event.getItem())) {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }
}
