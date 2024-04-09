package earth.terrarium.cadmus.client.claimmap;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.cadmus.Cadmus;
import earth.terrarium.cadmus.api.claims.ClaimApi;
import earth.terrarium.cadmus.api.claims.limit.ClaimLimitApi;
import earth.terrarium.cadmus.api.client.events.CadmusClientEvents;
import earth.terrarium.cadmus.api.events.CadmusEvents;
import earth.terrarium.cadmus.api.teams.TeamApi;
import earth.terrarium.cadmus.client.CadmusClient;
import earth.terrarium.cadmus.common.commands.claims.ClaimCommand;
import earth.terrarium.cadmus.common.constants.ConstantComponents;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ClaimScreen extends BaseCursorScreen {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Cadmus.MOD_ID, "textures/gui/map.png");
    public static final int TEXTURE_WIDTH = 216;
    public static final int TEXTURE_HEIGHT = 237;
    public static final int MAP_SIZE = 200;

    private static final WidgetSprites TRASH_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Cadmus.MOD_ID, "claimmap/trash_button"),
        new ResourceLocation(Cadmus.MOD_ID, "claimmap/trash_button_highlighted")
    );
    private static final WidgetSprites X_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Cadmus.MOD_ID, "claimmap/x_button"),
        new ResourceLocation(Cadmus.MOD_ID, "claimmap/x_button_highlighted")
    );

    private final Map<ChunkPos, ClaimTile> claims = new HashMap<>();

    private final LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
    private final ClientLevel level = player.clientLevel;

    private UUID id;
    private ChatFormatting teamColor;
    private int claimedCount;
    private int maxClaims;
    private int chunkLoadedCount;
    private int maxChunkLoaded;
    private float chunkScale;
    private float left;
    private float top;
    private float pixelScale;
    private int playerChunkX;
    private int playerChunkZ;

    private int selectionStartX;
    private int selectionStartZ;
    private int selectionEndX;
    private int selectionEndZ;

    public ClaimScreen() {
        super(CommonComponents.EMPTY);
    }

    public void refresh() {
        this.id = TeamApi.API.getId(this.player);
        this.teamColor = ChatFormatting.getByCode(CadmusClient.TEAM_INFO.get(id).rightChar());
        this.claimedCount = ClaimCommand.getClaimsCount(player, false);
        this.maxClaims = ClaimLimitApi.API.getMaxClaims(player);
        this.chunkLoadedCount = ClaimCommand.getClaimsCount(player, true);
        this.maxChunkLoaded = ClaimLimitApi.API.getMaxChunkLoadedClaims(player);

        float renderDistanceScale = getScaledRenderDistance();
        this.chunkScale = renderDistanceScale / 16f;
        this.left = (this.width - MAP_SIZE) / 2f;
        this.top = (this.height - MAP_SIZE) / 2f;
        this.pixelScale = MAP_SIZE / renderDistanceScale * 16;
        this.playerChunkX = Math.round(player.chunkPosition().x - chunkScale / 2);
        this.playerChunkZ = Math.round(player.chunkPosition().z - chunkScale / 2);

        this.calculateClaims();
    }

    @Override
    protected void init() {
        this.refresh();

        int x = (this.width - TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2;

        this.addRenderableWidget(new ImageButton(x + 7, y + 6, 11, 11, TRASH_BUTTON_SPRITES, button -> unclaimAll()))
            .setTooltip(Tooltip.create(ConstantComponents.CLEAR_CLAIMED_CHUNKS));

        this.addRenderableWidget(new ImageButton(x + TEXTURE_WIDTH - 11 - 7, y + 6, 11, 11, X_BUTTON_SPRITES, button -> onClose()))
            .setTooltip(Tooltip.create(ConstantComponents.CLOSE));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int top = (this.height - TEXTURE_HEIGHT) / 2;
        graphics.drawString(font, ConstantComponents.MAP_TITLE, (width - font.width(ConstantComponents.MAP_TITLE)) / 2, top + 7, 0x2a262b, false);
        drawClaimLabels(graphics, mouseX, mouseY);
    }

    private void drawClaimLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int left = (this.width - TEXTURE_WIDTH) / 2;
        int top = (this.height - TEXTURE_HEIGHT) / 2 + TEXTURE_HEIGHT - 13;

        String claimedCount = String.format("%d/%d", this.claimedCount, this.maxClaims);
        String chunkLoadedCount = String.format("%d/%d", this.chunkLoadedCount, this.maxChunkLoaded);

        graphics.drawString(font, claimedCount, left + 18, top, 0x404040, false);
        graphics.drawString(font, chunkLoadedCount, left + 95, top, 0x404040, false);

        if (mouseX >= left + 8 && mouseX <= left + 18 + font.width(claimedCount) && mouseY >= top - 5 && mouseY <= top + 10) {
            ScreenUtils.setTooltip(ConstantComponents.MAX_CLAIMS);
        } else if (mouseX >= left + 85 && mouseX <= left + 95 + font.width(chunkLoadedCount) && mouseY >= top - 5 && mouseY <= top + 10) {
            ScreenUtils.setTooltip(ConstantComponents.MAX_CHUNK_LOADED_CLAIMS);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        int left = (this.width - TEXTURE_WIDTH) / 2;
        int top = (this.height - TEXTURE_HEIGHT) / 2 + 1;
        graphics.blit(TEXTURE, left, top, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        if (this.selectionStartX == 0 && this.selectionStartZ == 0) {
            drawHover(graphics, mouseX, mouseY);
        } else {
            drawSelection(graphics);
        }
        drawClaims(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < chunkScale; i++) {
            for (int j = 0; j < chunkScale; j++) {
                float x = left + (i * pixelScale);
                float y = top + (j * pixelScale);

                if (isHovering(mouseX, mouseY, x, y)) {
                    this.selectionStartX = playerChunkX + i;
                    this.selectionStartZ = playerChunkZ + j;
                    this.selectionEndX = playerChunkX + i;
                    this.selectionEndZ = playerChunkZ + j;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (int i = 0; i < chunkScale; i++) {
            for (int j = 0; j < chunkScale; j++) {
                float x = left + (i * pixelScale);
                float y = top + (j * pixelScale);

                if (isHovering(mouseX, mouseY, x, y)) {
                    this.selectionEndX = playerChunkX + i;
                    this.selectionEndZ = playerChunkZ + j;
                }
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        setFocused(null);
        doAction(this.selectionStartX, this.selectionEndX, this.selectionStartZ, this.selectionEndZ, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void drawClaims(GuiGraphics graphics, int mouseX, int mouseY) {
        this.claims.forEach((pos, claim) -> {
            float x = left + (claim.x * pixelScale);
            float y = top + (claim.y * pixelScale);

            drawClaimSquare(graphics, x, y, pixelScale, pixelScale, modifyAlpha(claim.color, 127),
                claim.north, claim.east,
                claim.south, claim.west,
                claim.northEast, claim.southEast,
                claim.southWest, claim.northWest);

            if (isHovering(mouseX, mouseY, x, y)) {
                ScreenUtils.setTooltip(claim.name);
            }
        });
    }

    private void drawHover(GuiGraphics graphics, int mouseX, int mouseY) {
        for (int i = 0; i < chunkScale; i++) {
            for (int j = 0; j < chunkScale; j++) {
                float x = left + (i * pixelScale);
                float y = top + (j * pixelScale);

                if (isHovering(mouseX, mouseY, x, y)) {
                    drawClaimSquare(graphics, x, y, pixelScale, pixelScale, color(this.teamColor, 127),
                        true, true, true, true, true, true, true, true);
                    return;
                }
            }
        }
    }

    private void drawSelection(GuiGraphics graphics) {
        int selectionStartX = Math.min(this.selectionStartX, this.selectionEndX);
        int selectionStartZ = Math.min(this.selectionStartZ, this.selectionEndZ);
        int selectionEndX = Math.max(this.selectionStartX, this.selectionEndX);
        int selectionEndZ = Math.max(this.selectionStartZ, this.selectionEndZ);

        float x = left + ((selectionStartX - playerChunkX) * pixelScale);
        float y = top + ((selectionStartZ - playerChunkZ) * pixelScale);
        float width = Math.max(pixelScale, ((selectionEndX + 1 - selectionStartX)) * pixelScale);
        float height = Math.max(pixelScale, ((selectionEndZ + 1 - selectionStartZ)) * pixelScale);

        drawClaimSquare(graphics, x, y, width, height, color(this.teamColor, 127),
            true, true, true, true, true, true, true, true);
    }

    private void calculateClaims() {
        this.claims.clear();
        for (int i = 0; i < chunkScale; i++) {
            for (int j = 0; j < chunkScale; j++) {
                ChunkPos pos = new ChunkPos(playerChunkX + i, playerChunkZ + j);

                var claim = ClaimApi.API.getClaim(level, pos);
                if (claim.isEmpty()) continue;
                UUID id = claim.get().left();

                Component name = getName(id, claim.get().rightBoolean());
                int color = color(ChatFormatting.getByCode(CadmusClient.TEAM_INFO.get(id).rightChar()), 127);

                boolean north = checkSide(i, j, 0, -1);
                boolean east = checkSide(i, j, 1, 0);
                boolean south = checkSide(i, j, 0, 1);
                boolean west = checkSide(i, j, -1, 0);

                boolean northEast = checkSide(i, j, 1, -1);
                boolean southEast = checkSide(i, j, 1, 1);
                boolean southWest = checkSide(i, j, -1, 1);
                boolean northWest = checkSide(i, j, -1, -1);

                this.claims.put(pos, new ClaimTile(id, name, color, pos, i, j, north, east, south, west, northEast, southEast, southWest, northWest));
            }
        }
    }

    private boolean checkSide(int x, int z, int offsetX, int offsetZ) {
        ChunkPos pos = new ChunkPos(playerChunkX + x + offsetX, playerChunkZ + z + offsetZ);

        return ClaimApi.API.getClaim(level, pos).map(pair -> {
            ChunkPos currentPos = new ChunkPos(playerChunkX + x, playerChunkZ + z);
            return !pair.left().equals(ClaimApi.API.getClaim(level, currentPos).map(Pair::left).orElse(null));
        }).orElse(true);
    }

    private void drawClaimSquare(
        GuiGraphics graphics,
        float x, float y,
        float width, float height,
        int color,
        boolean north, boolean east,
        boolean south, boolean west,
        boolean northEast, boolean southEast,
        boolean southWest, boolean northWest
    ) {
        int roundedX = Math.round(x);
        int roundedY = Math.round(y);
        int roundedWidth = Math.round(x + width);
        int roundedHeight = Math.round(y + height);

        int borderColor = (color & 0x00FFFFFF) | 0xFF000000;

        if (north) graphics.fill(roundedX, roundedY, roundedWidth, roundedY + 1, borderColor);
        else if (northEast) graphics.fill(roundedWidth - 1, roundedY, roundedWidth, roundedY + 1, borderColor);

        if (east) graphics.fill(roundedWidth - 1, roundedY, roundedWidth, roundedHeight, borderColor);
        else if (southEast) graphics.fill(roundedWidth - 1, roundedHeight - 1, roundedWidth, roundedHeight, borderColor);

        if (south) graphics.fill(roundedX, roundedHeight - 1, roundedWidth, roundedHeight, borderColor);
        else if (southWest) graphics.fill(roundedX, roundedHeight - 1, roundedX + 1, roundedHeight, borderColor);

        if (west) graphics.fill(roundedX, roundedY, roundedX + 1, roundedHeight, borderColor);
        else if (northWest) graphics.fill(roundedX, roundedY, roundedX + 1, roundedY + 1, borderColor);

        graphics.fill(roundedX, roundedY, roundedWidth, roundedHeight, color & 0x33ffffff);
    }

    private void doAction(int startX, int endX, int startZ, int endZ, int button) {
        if (startX == 0 && startZ == 0) return;
        ChunkPos startPos = new ChunkPos(startX, startZ);
        ChunkPos endPos = new ChunkPos(endX, endZ);
        if (startPos.equals(endPos)) {
            if (button == 0 && !this.claims.containsKey(startPos)) {
                claim(startPos, hasShiftDown());
            } else if (button == 1 && this.claims.containsKey(startPos) && this.claims.get(startPos).id().equals(this.id)) {
                unclaim(startPos);
            }
        } else {
            if (button == 0) {
                claimArea(startPos, endPos, hasShiftDown());
            } else if (button == 1) {
                unclaimArea(startPos, endPos);
            }
        }
        this.selectionStartX = 0;
        this.selectionStartZ = 0;
        this.selectionEndX = 0;
        this.selectionEndZ = 0;
    }

    private boolean isHovering(double mouseX, double mouseY, float x, float y) {
        return mouseX >= x && mouseX < x + pixelScale && mouseY >= y && mouseY < y + pixelScale;
    }

    @SuppressWarnings("DataFlowIssue")
    private int color(ChatFormatting color, int alpha) {
        return modifyAlpha((color == ChatFormatting.RESET ? ChatFormatting.AQUA : color).getColor(), alpha);
    }

    private int modifyAlpha(int color, int alpha) {
        alpha = Math.min(255, Math.max(0, alpha));
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private Component getName(UUID id, boolean chunkLoad) {
        return Component.translatable("text.cadmus.claimed_by", TeamApi.API.getName(level, id)).withStyle(ChatFormatting.GRAY)
            .append(CommonComponents.SPACE)
            .append(chunkLoad ?
                Component.translatable("text.cadmus.chunk_loaded").withStyle(ChatFormatting.GOLD) :
                CommonComponents.EMPTY
            );
    }

    private int getScaledRenderDistance() {
        int scale = Minecraft.getInstance().options.renderDistance().get() * 16;
        return scale - (scale % 16) + 16;
    }


    private void claim(ChunkPos pos, boolean chunkLoad) {
        CadmusClient.sendSilentCommand("claim %s %s %s".formatted(pos.getMaxBlockX(), pos.getMaxBlockZ(), chunkLoad));
    }

    private void unclaim(ChunkPos pos) {
        CadmusClient.sendSilentCommand("unclaim %s %s".formatted(pos.getMaxBlockX(), pos.getMaxBlockZ()));
    }

    private void claimArea(ChunkPos startPos, ChunkPos endPos, boolean chunkLoad) {
        CadmusClient.sendSilentCommand("claim area %s %s %s %s %s".formatted(startPos.getMaxBlockX(), startPos.getMaxBlockZ(), endPos.getMaxBlockX(), endPos.getMaxBlockZ(), chunkLoad));
    }

    private void unclaimArea(ChunkPos startPos, ChunkPos endPos) {
        CadmusClient.sendSilentCommand("unclaim area %s %s %s %s".formatted(startPos.getMaxBlockX(), startPos.getMaxBlockZ(), endPos.getMaxBlockX(), endPos.getMaxBlockZ()));
    }

    private void unclaimAll() {
        CadmusClient.sendSilentCommand("unclaim all");
    }

    private static void update() {
        if (!Minecraft.getInstance().isSameThread()) return;
        if (Minecraft.getInstance().screen instanceof ClaimScreen screen) {
            screen.refresh();
        }
    }

    private record ClaimTile(
        UUID id,
        Component name,
        int color,
        ChunkPos pos,
        int x, int y,
        boolean north, boolean east,
        boolean south, boolean west,
        boolean northEast, boolean southEast,
        boolean southWest, boolean northWest
    ) {
    }

    static {
        CadmusEvents.AddClaimsEvent.register((level, id, positions) -> update());
        CadmusEvents.RemoveClaimsEvent.register((level, id, positions) -> update());
        CadmusEvents.ClearClaimsEvent.register((level, id) -> update());
        CadmusClientEvents.UpdateTeamInfo.register((id, name, color, updateMaps) -> {
            if (updateMaps) update();
        });
    }
}
