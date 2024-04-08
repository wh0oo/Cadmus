package earth.terrarium.cadmus.common.flags;

import earth.terrarium.cadmus.api.flags.Flag;
import earth.terrarium.cadmus.api.flags.FlagApi;
import earth.terrarium.cadmus.api.flags.types.BooleanFlag;
import earth.terrarium.cadmus.api.flags.types.ChatFormattingFlag;
import earth.terrarium.cadmus.api.flags.types.FloatFlag;
import earth.terrarium.cadmus.api.flags.types.StringFlag;
import net.minecraft.ChatFormatting;

public class Flags {

    public static final StringFlag DISPLAY_NAME = register("display-name", new StringFlag(""));
    public static final ChatFormattingFlag COLOR = register("color", new ChatFormattingFlag(ChatFormatting.LIGHT_PURPLE));

    public static final BooleanFlag BLOCK_BREAK = register("block-break", new BooleanFlag(true));
    public static final BooleanFlag BLOCK_PLACE = register("block-place", new BooleanFlag(true));
    public static final BooleanFlag BLOCK_INTERACTIONS = register("block-interactions", new BooleanFlag(true));
    public static final BooleanFlag BLOCK_EXPLOSIONS = register("block-explosions", new BooleanFlag(true));
    public static final BooleanFlag ENTITY_EXPLOSIONS = register("entity-explosions", new BooleanFlag(true));
    public static final BooleanFlag ENTITY_INTERACTIONS = register("entity-interactions", new BooleanFlag(true));
    public static final BooleanFlag ENTITY_DAMAGE = register("entity-damage", new BooleanFlag(true));
    public static final BooleanFlag MOB_GRIEFING = register("mob-griefing", new BooleanFlag(true));
    public static final BooleanFlag ITEM_PICKUP = register("item-pickup", new BooleanFlag(true));

    public static final BooleanFlag FIRE_SPREAD = register("fire-spread", new BooleanFlag(true));
    public static final BooleanFlag SNOW_FALL = register("snow-fall", new BooleanFlag(true));
    public static final BooleanFlag SNOW_MELT = register("snow-melt", new BooleanFlag(true));
    public static final BooleanFlag ICE_FORM = register("ice-form", new BooleanFlag(true));
    public static final BooleanFlag ICE_MELT = register("ice-melt", new BooleanFlag(true));
    public static final BooleanFlag LEAF_DECAY = register("leaf-decay", new BooleanFlag(true));
    public static final BooleanFlag LIGHTNING = register("lightning", new BooleanFlag(true));

    public static final BooleanFlag MONSTER_DAMAGE = register("monster-damage", new BooleanFlag(true));
    public static final BooleanFlag CREATURE_DAMAGE = register("creature-damage", new BooleanFlag(true));
    public static final BooleanFlag PVP = register("pvp", new BooleanFlag(true));

    public static final BooleanFlag MONSTER_SPAWNING = register("monster-spawning", new BooleanFlag(true));
    public static final BooleanFlag CREATURE_SPAWNING = register("creature-spawning", new BooleanFlag(true));

    public static final BooleanFlag KEEP_INVENTORY = register("keep-inventory", new BooleanFlag(false));

    public static final BooleanFlag ALLOW_ENTRY = register("allow-entry", new BooleanFlag(true));
    public static final BooleanFlag ALLOW_EXIT = register("allow-exit", new BooleanFlag(true));

    public static final BooleanFlag USE = register("use", new BooleanFlag(true));
    public static final BooleanFlag USE_CHESTS = register("use-chests", new BooleanFlag(true));
    public static final BooleanFlag USE_DOORS = register("use-doors", new BooleanFlag(true));
    public static final BooleanFlag USE_REDSTONE = register("use-redstone", new BooleanFlag(true));
    public static final BooleanFlag USE_VEHICLES = register("use-vehicles", new BooleanFlag(true));

    public static final FloatFlag FEED_RATE = register("feed-rate", new FloatFlag(0.0f));
    public static final FloatFlag HEAL_RATE = register("heal-rate", new FloatFlag(0.0f));

    public static final StringFlag ENTRY_DENY_MESSAGE = register("entry-deny-message", new StringFlag(""));
    public static final StringFlag EXIT_DENY_MESSAGE = register("exit-deny-message", new StringFlag(""));
    public static final StringFlag FAREWELL = register("farewell", new StringFlag(""));
    public static final StringFlag GREETING = register("greeting", new StringFlag(""));

    public static <T extends Flag<?>> T register(String name, T flag) {
        FlagApi.API.register(name, flag);
        return flag;
    }

    public static void init() {} // NO-OP
}
