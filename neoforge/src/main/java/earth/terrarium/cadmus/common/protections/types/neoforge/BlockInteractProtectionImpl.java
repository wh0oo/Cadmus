package earth.terrarium.cadmus.common.protections.types.neoforge;

import earth.terrarium.cadmus.Cadmus;
import earth.terrarium.cadmus.common.protections.Protections;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@Mod.EventBusSubscriber(modid = Cadmus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
final class BlockInteractProtectionImpl {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!Protections.BLOCK_INTERACTIONS.canInteractWithBlock(event.getEntity(), event.getPos(), event.getLevel().getBlockState(event.getPos()))) {
            event.setUseItem(Event.Result.ALLOW);
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
