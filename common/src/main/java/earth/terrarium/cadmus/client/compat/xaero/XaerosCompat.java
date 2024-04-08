package earth.terrarium.cadmus.client.compat.xaero;

import earth.terrarium.cadmus.api.client.events.CadmusClientEvents;
import earth.terrarium.cadmus.api.events.CadmusEvents;
import earth.terrarium.cadmus.client.CadmusClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.highlight.HighlighterRegistry;
import xaero.map.region.MapRegion;
import xaero.map.world.MapDimension;

import java.util.Set;

public class XaerosCompat {

    static {
        CadmusEvents.AddClaimsEvent.register((level, id, positions) -> update(level.dimension(), positions.keySet()));
        CadmusEvents.RemoveClaimsEvent.register((level, id, positions) -> update(level.dimension(), positions));
        CadmusEvents.ClearClaimsEvent.register((level, id) -> updateAll(level.dimension()));
        CadmusClientEvents.UpdateTeamInfo.register((id, name, color, updateMaps) -> {
            if (updateMaps) updateAll(CadmusClient.level().dimension());
        });
    }

    public static void update(ResourceKey<Level> dimension, Set<ChunkPos> positions) {
        if (!Minecraft.getInstance().isSameThread()) return;
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapProcessor processor = session.getMapProcessor();
        MapDimension mapDim = processor.getMapWorld().getDimension(dimension);
        if (mapDim == null) return;

        int caveLayer = processor.getCurrentCaveLayer();
        for (var pos : positions) {
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    if (i == 0 && j == 0 || i * i != j * j) {
                        int regionX = pos.x + i >> 5;
                        int regionZ = pos.z + j >> 5;

                        mapDim.getHighlightHandler().clearCachedHash(regionX, regionZ);
                        MapRegion region = mapDim.getLayeredMapRegions().getLeaf(caveLayer, regionX, regionZ);
                        if (region != null) region.requestRefresh(processor);
                    }
                }
            }
        }
    }

    public static void updateAll(ResourceKey<Level> dimension) {
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapProcessor processor = session.getMapProcessor();
        MapDimension mapDim = processor.getMapWorld().getDimension(dimension);
        if (mapDim == null) return;

        mapDim.getHighlightHandler().clearCachedHashes();
        try {
            for (var leveledRegion : mapDim.getLayeredMapRegions().getLoadedListUnsynced()) {
                if (leveledRegion instanceof MapRegion mapRegion) {
                    mapRegion.requestRefresh(processor);
                }
            }
        } catch (Exception ignored) {}
    }

    public static void registerHighlighters(HighlighterRegistry registry) {
        registry.register(new CadmusChunkHighlighter());
    }
}