package earth.terrarium.cadmus.common.utils.fabric;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

public class CadmusGameRulesImpl {

    public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRuleRegistry.register(name, category, type);
    }

    public static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changeListener) {
        return GameRuleFactory.createIntRule(defaultValue, changeListener);
    }

    public static GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        return GameRuleFactory.createBooleanRule(defaultValue);
    }
}
