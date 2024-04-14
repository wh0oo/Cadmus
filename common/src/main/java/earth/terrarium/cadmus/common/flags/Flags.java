package earth.terrarium.cadmus.common.flags;

import earth.terrarium.cadmus.api.flags.Flag;
import earth.terrarium.cadmus.api.flags.FlagApi;
import earth.terrarium.cadmus.api.flags.types.BooleanFlag;
import earth.terrarium.cadmus.api.flags.types.ChatFormattingFlag;
import earth.terrarium.cadmus.api.flags.types.FloatFlag;
import earth.terrarium.cadmus.api.flags.types.StringFlag;
import net.minecraft.ChatFormatting;

public class Flags {

    public static final StringFlag DISPLAY_NAME = register(new StringFlag("display-name", ""));
    public static final ChatFormattingFlag COLOR = register(new ChatFormattingFlag("color", ChatFormatting.LIGHT_PURPLE));

    public static final BooleanFlag BLOCK_BREAK = register(new BooleanFlag("block-break", true));
    public static final BooleanFlag BLOCK_PLACE = register(new BooleanFlag("block-place", true));
    public static final BooleanFlag BLOCK_INTERACTIONS = register(new BooleanFlag("block-interactions", true));
    public static final BooleanFlag BLOCK_EXPLOSIONS = register(new BooleanFlag("block-explosions", true));
    public static final BooleanFlag ENTITY_EXPLOSIONS = register(new BooleanFlag("entity-explosions", true));
    public static final BooleanFlag ENTITY_INTERACTIONS = register(new BooleanFlag("entity-interactions", true));
    public static final BooleanFlag ENTITY_DAMAGE = register(new BooleanFlag("entity-damage", true));
    public static final BooleanFlag MOB_GRIEFING = register(new BooleanFlag("mob-griefing", true));
    public static final BooleanFlag ITEM_PICKUP = register(new BooleanFlag("item-pickup", true));

    public static final BooleanFlag FIRE_SPREAD = register(new BooleanFlag("fire-spread", true));
    public static final BooleanFlag SNOW_FALL = register(new BooleanFlag("snow-fall", true));
    public static final BooleanFlag SNOW_MELT = register(new BooleanFlag("snow-melt", true));
    public static final BooleanFlag ICE_FORM = register(new BooleanFlag("ice-form", true));
    public static final BooleanFlag ICE_MELT = register(new BooleanFlag("ice-melt", true));
    public static final BooleanFlag LEAF_DECAY = register(new BooleanFlag("leaf-decay", true));
    public static final BooleanFlag LIGHTNING = register(new BooleanFlag("lightning", true));

    public static final BooleanFlag MONSTER_DAMAGE = register(new BooleanFlag("monster-damage", true));
    public static final BooleanFlag CREATURE_DAMAGE = register(new BooleanFlag("creature-damage", true));
    public static final BooleanFlag PVP = register(new BooleanFlag("pvp", true));

    public static final BooleanFlag MONSTER_SPAWNING = register(new BooleanFlag("monster-spawning", true));
    public static final BooleanFlag CREATURE_SPAWNING = register(new BooleanFlag("creature-spawning", true));

    public static final BooleanFlag KEEP_INVENTORY = register(new BooleanFlag("keep-inventory", false));

    public static final BooleanFlag ALLOW_ENTRY = register(new BooleanFlag("allow-entry", true));
    public static final BooleanFlag ALLOW_EXIT = register(new BooleanFlag("allow-exit", true));

    public static final BooleanFlag USE = register(new BooleanFlag("use", true));
    public static final BooleanFlag USE_CHESTS = register(new BooleanFlag("use-chests", true));
    public static final BooleanFlag USE_DOORS = register(new BooleanFlag("use-doors", true));
    public static final BooleanFlag USE_REDSTONE = register(new BooleanFlag("use-redstone", true));
    public static final BooleanFlag USE_VEHICLES = register(new BooleanFlag("use-vehicles", true));

    public static final FloatFlag FEED_RATE = register(new FloatFlag("feed-rate", 0.0f));
    public static final FloatFlag HEAL_RATE = register(new FloatFlag("heal-rate", 0.0f));

    public static final StringFlag ENTRY_DENY_MESSAGE = register(new StringFlag("entry-deny-message", ""));
    public static final StringFlag EXIT_DENY_MESSAGE = register(new StringFlag("exit-deny-message", ""));
    public static final StringFlag FAREWELL = register(new StringFlag("farewell", ""));
    public static final StringFlag GREETING = register(new StringFlag("greeting", ""));

    public static <T extends Flag<?>> T register(T flag) {
        FlagApi.API.register(flag.id(), flag);
        return flag;
    }

    public static void init() {} // NO-OP
}
