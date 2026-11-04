package de.chillbyyte.minimap;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class MiniMapSettings {
    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("chillbyyte-minikarte.properties");
    public boolean visible = true;
    public boolean deathMarkers = true;
    public boolean coords = true;
    public boolean top = false; // false => bottom right, away from Xaero's usual upper left
    public int zoom = 4;       // blocks per map pixel
    public int cellSize = 3;   // rendered pixel size

    public void load() {
        if (!Files.isRegularFile(file)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            p.load(in);
            visible = Boolean.parseBoolean(p.getProperty("visible", "true"));
            deathMarkers = Boolean.parseBoolean(p.getProperty("deathMarkers", "true"));
            coords = Boolean.parseBoolean(p.getProperty("coords", "true"));
            top = Boolean.parseBoolean(p.getProperty("topRight", "false"));
            zoom = clamp(number(p.getProperty("zoom"), 4), 2, 8);
            cellSize = clamp(number(p.getProperty("cellSize"), 3), 2, 4);
        } catch (IOException ex) {
            System.err.println("[Chillbyyte-Minimap] Config konnte nicht gelesen werden: " + ex);
        }
    }

    public void save() {
        Properties p = new Properties();
        p.setProperty("visible", Boolean.toString(visible));
        p.setProperty("deathMarkers", Boolean.toString(deathMarkers));
        p.setProperty("coords", Boolean.toString(coords));
        p.setProperty("topRight", Boolean.toString(top));
        p.setProperty("zoom", Integer.toString(zoom));
        p.setProperty("cellSize", Integer.toString(cellSize));
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                p.store(out, "Chillbyyte Minikarte / Fabric 26.3");
            }
        } catch (IOException ex) {
            System.err.println("[Chillbyyte-Minimap] Speichern fehlgeschlagen: " + ex);
        }
    }

    static int clamp(int v, int min, int max) { return Math.min(max, Math.max(min, v)); }
    private static int number(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (NumberFormatException ex) { return fallback; }
    }
}
