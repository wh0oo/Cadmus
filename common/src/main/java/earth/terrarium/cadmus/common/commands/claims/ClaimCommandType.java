package earth.terrarium.cadmus.common.commands.claims;

public enum ClaimCommandType {
    CLAIM("claim"),
    CLAIM_AREA("claim area"),
    UNCLAIM("unclaim"),
    UNCLAIM_AREA("unclaim area"),
    UNCLAIM_ALL("unclaim all"),
    ;

    private final String command;

    ClaimCommandType(String command) {
        this.command = command;
    }

    public String command() {
        return command;
    }
}
