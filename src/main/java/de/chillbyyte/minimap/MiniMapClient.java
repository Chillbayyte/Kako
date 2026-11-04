package de.chillbyyte.minimap;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/** Client-side only. Does not load or modify Xaero's code/configuration. */
public final class MiniMapClient implements ClientModInitializer {
    public static final String MOD_ID = "chillbyyte_minimap";
    public static final MiniMapSettings SETTINGS = new MiniMapSettings();
    public static final DeathStore DEATHS = new DeathStore();
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(MOD_ID, "controls"));

    private static KeyMapping menuKey;
    private static KeyMapping toggleKey;
    private static boolean sawDeath = false;
    private static int tickCounter = 0;
    private static String currentIdentity = "";

    @Override
    public void onInitializeClient() {
        SETTINGS.load();
        DEATHS.load();
        menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.chillbyyte_minimap.menu", InputConstants.Type.KEYBOARD, InputConstants.KEY_O, CATEGORY));
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.chillbyyte_minimap.toggle", InputConstants.Type.KEYBOARD, InputConstants.KEY_V, CATEGORY));
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "map"), MiniMapHud::render);
        ClientTickEvents.END_CLIENT_TICK.register(MiniMapClient::tick);
    }

    private static void tick(Minecraft client) {
        while (menuKey.consumeClick()) {
            if (client.gui.screen() instanceof MiniMapScreen) {
                client.gui.setScreen(null);
            } else {
                client.gui.setScreen(new MiniMapScreen(client.gui.screen()));
            }
        }
        while (toggleKey.consumeClick()) {
            SETTINGS.visible = !SETTINGS.visible;
            SETTINGS.save();
        }

        if (client.player == null || client.level == null) {
            sawDeath = false;
            currentIdentity = "";
            return;
        }

        String identity = DeathStore.worldKey(client);
        if (!identity.equals(currentIdentity)) {
            currentIdentity = identity;
            sawDeath = false;
            MiniMapHud.clearCache();
        }

        // Track death even when the map is hidden. This is strictly local data.
        boolean dead = client.player.isDeadOrDying();
        if (dead && !sawDeath) {
            DEATHS.record(client);
            sawDeath = true;
        } else if (!dead) {
            sawDeath = false;
        }

        if (++tickCounter % 10 == 0) {
            MiniMapHud.refresh(client);
        }
    }
}
