package earth.terrarium.cadmus.common.utils.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class ModUtilsImpl {

    public static boolean isMixinModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
