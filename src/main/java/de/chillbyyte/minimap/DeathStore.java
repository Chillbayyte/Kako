package de.chillbyyte.minimap;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** Per-server + per-dimension death coordinates, local to one player's client. */
public final class DeathStore {
    public record Death(String world, int x, int y, int z, long when) {}
    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("chillbyyte-todespunkte.properties");
    private final List<Death> points = new ArrayList<>();

    public static String worldKey(Minecraft mc) {
        if (mc.level == null) return "";
        String server = mc.getCurrentServer() != null ? mc.getCurrentServer().ip : "singleplayer";
        String dimension = mc.level.dimension().toString();
        return (server + "/" + dimension).replace('|', '_').replace('\n', '_');
    }

    public List<Death> points(String world) {
        return points.stream().filter(p -> p.world().equals(world)).toList();
    }

    public void record(Minecraft mc) {
        if (mc.player == null) return;
        points.add(new Death(worldKey(mc), mc.player.getBlockX(), mc.player.getBlockY(),
            mc.player.getBlockZ(), System.currentTimeMillis()));
        while (points.size() > 25) points.removeFirst();
        save();
    }

    public void clear(String world) {
        points.removeIf(p -> p.world().equals(world));
        save();
    }

    public void load() {
        points.clear();
        if (!Files.isRegularFile(file)) return;
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
            int count = Math.min(25, Integer.parseInt(props.getProperty("count", "0")));
            for (int i = 0; i < count; i++) {
                String[] a = props.getProperty("death." + i, "").split("\\|", -1);
                if (a.length == 5) {
                    try { points.add(new Death(a[0], Integer.parseInt(a[1]), Integer.parseInt(a[2]),
                        Integer.parseInt(a[3]), Long.parseLong(a[4]))); }
                    catch (NumberFormatException ignored) { /* skip corrupt entry */ }
                }
            }
        } catch (IOException | NumberFormatException ex) {
            System.err.println("[Chillbyyte-Minimap] Todespunkte konnten nicht gelesen werden: " + ex);
        }
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty("count", Integer.toString(points.size()));
        for (int i = 0; i < points.size(); i++) {
            Death d = points.get(i);
            props.setProperty("death." + i, d.world() + "|" + d.x() + "|" + d.y() + "|" + d.z() + "|" + d.when());
        }
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, "Chillbyyte Todemarkierungen - lokal gespeichert");
            }
        } catch (IOException ex) {
            System.err.println("[Chillbyyte-Minimap] Todespunkte konnten nicht gespeichert werden: " + ex);
        }
    }
}
