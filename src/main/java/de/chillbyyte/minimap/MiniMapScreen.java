package de.chillbyyte.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Small independent settings screen: O to open; V to toggle visibility. */
public final class MiniMapScreen extends Screen {
    private final Screen parent;

    public MiniMapScreen(Screen parent) {
        super(Minecraft.getInstance(), Minecraft.getInstance().font, Component.literal("Chillbyyte Minikarte"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        final MiniMapSettings s = MiniMapClient.SETTINGS;
        int left = width / 2 - 115;
        int y = height / 2 - 88;
        addRenderableWidget(Button.builder(Component.literal("Minikarte: " + (s.visible ? "AN" : "AUS")), b -> {
            s.visible = !s.visible; s.save(); rebuildWidgets();
        }).bounds(left, y, 230, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Todespunkte: " + (s.deathMarkers ? "AN" : "AUS")), b -> {
            s.deathMarkers = !s.deathMarkers; s.save(); rebuildWidgets();
        }).bounds(left, y + 24, 230, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Koordinaten: " + (s.coords ? "AN" : "AUS")), b -> {
            s.coords = !s.coords; s.save(); rebuildWidgets();
        }).bounds(left, y + 48, 230, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Position: " + (s.top ? "OBEN RECHTS" : "UNTEN RECHTS")), b -> {
            s.top = !s.top; s.save(); rebuildWidgets();
        }).bounds(left, y + 72, 230, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Zoom: " + s.zoom + " Blöcke/Pixel"), b -> {
            s.zoom = s.zoom >= 8 ? 2 : s.zoom + 2; s.save(); MiniMapHud.clearCache(); rebuildWidgets();
        }).bounds(left, y + 96, 230, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Todespunkte löschen"), b -> {
            MiniMapClient.DEATHS.clear(DeathStore.worldKey(Minecraft.getInstance()));
        }).bounds(left, y + 120, 230, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Fertig"), b -> onClose())
            .bounds(left, y + 146, 230, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        g.fill(width / 2 - 128, height / 2 - 110, width / 2 + 128, height / 2 + 91, 0xEE0A1521);
        g.text(font, "Chillbyyte - Minikarten-Einstellungen", width / 2 - 114, height / 2 - 102, 0xFFFFFFFF, true);
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() { Minecraft.getInstance().gui.setScreen(parent); }
    @Override
    public boolean isPauseScreen() { return false; }
}
