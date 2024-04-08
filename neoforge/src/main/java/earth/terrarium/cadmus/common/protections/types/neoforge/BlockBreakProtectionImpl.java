package earth.terrarium.cadmus.common.protections.types.neoforge;

import earth.terrarium.cadmus.Cadmus;
import earth.terrarium.cadmus.common.protections.Protections;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.FillBucketEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod.EventBusSubscriber(modid = Cadmus.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
final class BlockBreakProtectionImpl {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!Protections.BLOCK_BREAKING.canBreakBlock(event.getEntity(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!Protections.BLOCK_BREAKING.canBreakBlock(event.getPlayer(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onFarmLandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof Player player && !Protections.BLOCK_BREAKING.canBreakBlock(player, event.getPos())) {
            event.setCanceled(true);
        } else if (!Protections.MOB_GRIEFING.canMobGrief(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onFillBucket(FillBucketEvent event) {
        if (event.getTarget() != null && !Protections.BLOCK_BREAKING.canBreakBlock(event.getEntity(), BlockPos.containing(event.getTarget().getLocation()))) {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }
}
