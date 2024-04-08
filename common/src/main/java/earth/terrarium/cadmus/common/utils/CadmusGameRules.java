package earth.terrarium.cadmus.common.utils;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.cadmus.Cadmus;
import net.minecraft.world.level.GameRules;

public class CadmusGameRules {

    public static final GameRules.Key<GameRules.IntegerValue> MAX_CLAIMS = register(
        "maxClaims",
        GameRules.Category.MISC,
        createIntRule(Cadmus.DEFAULT_MAX_CLAIMS));

    public static final GameRules.Key<GameRules.IntegerValue> MAX_CHUNK_LOADED_CLAIMS = register(
        "maxChunkLoadedClaims",
        GameRules.Category.MISC,
        createIntRule(Cadmus.DEFAULT_MAX_CHUNK_LOADED_CLAIMS));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_BLOCK_BREAKING = register(
        "doClaimedBlockBreaking",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_BLOCK_PLACING = register(
        "doClaimedBlockPlacing",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_BLOCK_INTERACTIONS = register(
        "doClaimedBlockInteractions",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_BLOCK_EXPLOSIONS = register(
        "doClaimedBlockExplosions",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_ENTITY_EXPLOSIONS = register(
        "doClaimedEntityExplosions",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_ENTITY_INTERACTIONS = register(
        "doClaimedEntityInteractions",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_ENTITY_DAMAGE = register(
        "doClaimedEntityDamage",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_MOB_GRIEFING = register(
        "doClaimedMobGriefing",
        GameRules.Category.MISC,
        createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> DO_CLAIMED_ITEM_PICKUP = register(
        "doClaimedItemPickup",
        GameRules.Category.MISC,
        createBooleanRule(false));

    @ExpectPlatform
    private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    private static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    private static GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        throw new NotImplementedException();
    }

    public static void init() {} // NO-OP
}
