package earth.terrarium.cadmus.common.utils.neoforge;

import net.neoforged.fml.loading.LoadingModList;

public class ModUtilsImpl {

    public static boolean isMixinModLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}
