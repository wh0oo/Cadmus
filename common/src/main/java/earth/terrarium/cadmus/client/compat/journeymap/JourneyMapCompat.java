package earth.terrarium.cadmus.client.compat.journeymap;

import earth.terrarium.cadmus.Cadmus;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.client.events.CadmusClientEvents;
import earth.terrarium.cadmus.api.events.CadmusEvents;
import earth.terrarium.cadmus.client.CadmusClient;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.RegistryEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

@ClientPlugin
public class JourneyMapCompat implements IClientPlugin {

    private IClientAPI api;
    private ClaimedChunkOptions options;

    @Override
    public void initialize(IClientAPI api) {
        this.api = api;
        this.api.subscribe(getModId(),
            EnumSet.of(
                ClientEvent.Type.DISPLAY_UPDATE,
                ClientEvent.Type.MAPPING_STOPPED,
                ClientEvent.Type.REGISTRY
            )
        );

        CadmusEvents.AddClaimsEvent.register((level, id, positions) -> update(level.dimension()));
        CadmusEvents.RemoveClaimsEvent.register((level, id, positions) -> update(level.dimension()));
        CadmusEvents.ClearClaimsEvent.register((level, id) -> update(level.dimension()));
        CadmusClientEvents.UpdateTeamInfo.register((id, name, color, updateMaps) -> {
            if (updateMaps) update(CadmusClient.level().dimension());
        });
    }

    @Override
    public String getModId() {
        return Cadmus.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        switch (event.type) {
            case DISPLAY_UPDATE, MAPPING_STARTED -> {
                if (options != null && Boolean.TRUE.equals(options.showClaimedChunks.get())) {
                    update(event.dimension);
                } else {
                    clear();
                }
            }
            case MAPPING_STOPPED -> clear();
            case REGISTRY -> {
                if (event instanceof RegistryEvent.OptionsRegistryEvent) {
                    this.options = new ClaimedChunkOptions();
                }
            }
        }
    }

    private void clear() {
        api.removeAll(getModId());
    }

    private void update(ResourceKey<Level> dimension) {
        if (options != null && options.showClaimedChunks.get()) {
            show(dimension);
        } else {
            clear();
        }
    }

    private void show(ResourceKey<Level> dimension) {
        clear();
        ClaimApi.API.getAllClientClaims(dimension).forEach((pos, entry) -> {
            try {
                api.show(ClaimedChunkDisplay.create(pos, entry.left(), entry.rightBoolean(), dimension));
            } catch (Exception ignored) {}
        });
    }
}
