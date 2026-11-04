package de.chillbyyte.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;

/**
 * Minimal top-down overview of LOADED blocks only; no scans of unloaded chunks.
 * Independently rendered from Xaero. At most 21x21 samples each half-second.
 */
public final class MiniMapHud {
    private static final int TILES = 21;
    private static final int HALF = TILES / 2;
    private static final int[][] COLORS = new int[TILES][TILES];
    private static int lastCenterX, lastCenterZ;
    private static int lastZoom;
    private static String lastWorld = "";

    private MiniMapHud() {}

    static void clearCache() {
        for (int[] row : COLORS) java.util.Arrays.fill(row, 0xFF172329);
        lastWorld = "";
    }

    static void refresh(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;
        final var level = mc.level;
        final var settings = MiniMapClient.SETTINGS;
        final int zoom = settings.zoom;
        final int cx = mc.player.getBlockX();
        final int cz = mc.player.getBlockZ();
        lastCenterX = cx;
        lastCenterZ = cz;
        lastZoom = zoom;
        lastWorld = DeathStore.worldKey(mc);
        for (int dz = 0; dz < TILES; dz++) {
            for (int dx = 0; dx < TILES; dx++) {
                int wx = cx + (dx - HALF) * zoom;
                int wz = cz + (dz - HALF) * zoom;
                if (!level.hasChunk(wx >> 4, wz >> 4)) {
                    COLORS[dz][dx] = 0xFF131D24;
                    continue;
                }
                BlockPos column = new BlockPos(wx, mc.player.getBlockY(), wz);
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, column) - 1;
                BlockPos pos = new BlockPos(wx, surfaceY, wz);
                BlockState state = level.getBlockState(pos);
                COLORS[dz][dx] = colorFor(state, surfaceY, level);
            }
        }
    }

    private static int colorFor(BlockState state, int y, Level world) {
        if (state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN)) return 0xFF397AB5;
        if (state.is(Blocks.LAVA)) return 0xFFE7792C;
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.SHORT_GRASS) || state.is(Blocks.TALL_GRASS)) return 0xFF5F9F54;
        if (state.is(Blocks.OAK_LEAVES) || state.is(Blocks.SPRUCE_LEAVES) || state.is(Blocks.BIRCH_LEAVES)) return 0xFF317E45;
        if (state.is(Blocks.SAND) || state.is(Blocks.SANDSTONE)) return 0xFFD7C48B;
        if (state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.SNOW)) return 0xFFF2F4F5;
        if (state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.MUD)) return 0xFF87674B;
        if (state.is(Blocks.OAK_PLANKS) || state.is(Blocks.SPRUCE_PLANKS) || state.is(Blocks.DARK_OAK_PLANKS)) return 0xFFA0764D;
        if (state.is(Blocks.NETHERRACK)) return 0xFF8F4A4A;
        if (state.is(Blocks.BEDROCK)) return 0xFF48494E;
        if (state.isAir()) return 0xFF172329;
        return y > 105 ? 0xFF9B9B9D : 0xFF808A82;
    }

    public static void render(GuiGraphicsExtractor g, net.minecraft.client.DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        MiniMapSettings s = MiniMapClient.SETTINGS;
        if (!s.visible || mc.player == null || mc.level == null || mc.gui.hud.isHidden()) return;
        int cell = s.cellSize;
        int size = TILES * cell;
        int x = g.guiWidth() - size - 12;
        int y = s.top ? 12 : g.guiHeight() - size - 28;
        g.fill(x - 3, y - 15, x + size + 3, y + size + 15, 0xDA0D161C);
        g.outline(x - 2, y - 14, size + 4, size + 29, 0xFF7798A8);
        g.text(mc.font, "MINIKARTE", x, y - 12, 0xFFFFFFFF, true);
        for (int z = 0; z < TILES; z++) {
            for (int xx = 0; xx < TILES; xx++) {
                g.fill(x + xx * cell, y + z * cell,
                    x + (xx + 1) * cell, y + (z + 1) * cell, COLORS[z][xx]);
            }
        }
        int center = x + HALF * cell + cell / 2;
        int centerY = y + HALF * cell + cell / 2;
        g.fill(center - 2, centerY - 2, center + 3, centerY + 3, 0xFFFFFFFF);
        g.fill(center - 1, centerY - 1, center + 2, centerY + 2, 0xFFEB4D4D);

        if (s.deathMarkers && lastZoom != 0 && lastWorld.equals(DeathStore.worldKey(mc))) {
            for (DeathStore.Death death : MiniMapClient.DEATHS.points(lastWorld)) {
                int dx = Math.round((death.x() - lastCenterX) / (float) lastZoom) + HALF;
                int dz = Math.round((death.z() - lastCenterZ) / (float) lastZoom) + HALF;
                // Distant deaths are marked at the map edge in their correct direction.
                dx = Math.max(0, Math.min(TILES - 1, dx));
                dz = Math.max(0, Math.min(TILES - 1, dz));
                int px = x + dx * cell + cell / 2;
                int py = y + dz * cell + cell / 2;
                g.fill(px - 2, py - 1, px + 3, py + 2, 0xFF000000);
                g.text(mc.font, "X", px - 3, py - 4, 0xFFFF4444, true);
            }
        }

        if (s.coords) {
            String text = mc.player.getBlockX() + " / " + mc.player.getBlockY() + " / " + mc.player.getBlockZ();
            g.text(mc.font, text, x, y + size + 3, 0xFFE0E0E0, true);
        }
    }
}
