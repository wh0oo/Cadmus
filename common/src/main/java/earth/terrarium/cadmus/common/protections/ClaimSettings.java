package earth.terrarium.cadmus.common.protections;

import earth.terrarium.cadmus.api.protections.ProtectionApi;

public class ClaimSettings {

    public static final String CAN_BREAK_BLOCKS = "canBreakBlocks";
    public static final String CAN_PLACE_BLOCKS = "canPlaceBlocks";
    public static final String CAN_INTERACT_WITH_BLOCKS = "canInteractWithBlocks";
    public static final String CAN_EXPLODE_BLOCKS = "canExplodeBlocks";
    public static final String CAN_EXPLODE_ENTITIES = "canExplodeEntities";
    public static final String CAN_INTERACT_WITH_ENTITIES = "canInteractWithEntities";
    public static final String CAN_DAMAGE_ENTITIES = "canDamageEntities";
    public static final String CAN_MOBS_GRIEF = "canMobsGrief";
    public static final String CAN_PICKUP_ITEMS = "canPickupItems";

    public static final String CAN_NON_PLAYERS_PLACE_BLOCKS = "canNonPlayersPlaceBlocks";

    public static void init() {
        ProtectionApi.API.registerSetting(CAN_NON_PLAYERS_PLACE_BLOCKS);
        // Other settings are registered by default in Protections
    }
}
