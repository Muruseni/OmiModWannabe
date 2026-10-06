package com.example.blacklistedlobbywatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(
        modid = BlacklistedLobbyWatcher.MODID,
        name = BlacklistedLobbyWatcher.NAME,
        version = BlacklistedLobbyWatcher.VERSION,
        clientSideOnly = true
)
public class BlacklistedLobbyWatcher {
    public static final String MODID = "blacklistedlobbywatcher";
    public static final String NAME = "Blacklisted Lobby Watcher";
    public static final String VERSION = "1.0.0";

    public static final Minecraft MC = Minecraft.getMinecraft();

    private static BlacklistManager blacklistManager;
    private static LobbyWatcher lobbyWatcher;
    private static KeyBinding openGuiKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        blacklistManager = new BlacklistManager(MC);
        lobbyWatcher = new LobbyWatcher(MC, blacklistManager);

        openGuiKey = new KeyBinding(
                "Open Blacklist Watcher",
                Keyboard.KEY_B,
                "Blacklisted Lobby Watcher"
        );
        ClientRegistry.registerKeyBinding(openGuiKey);

        MinecraftForge.EVENT_BUS.register(lobbyWatcher);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        blacklistManager.load();
    }

    public static BlacklistManager getBlacklistManager() {
        return blacklistManager;
    }

    public static LobbyWatcher getLobbyWatcher() {
        return lobbyWatcher;
    }

    public static class ClientEvents {
        @SubscribeEvent
        public void onKeyInput(InputEvent.KeyInputEvent event) {
            if (openGuiKey.isPressed() && MC.currentScreen == null) {
                MC.displayGuiScreen(new BlacklistGui());
            }
        }

        @SubscribeEvent
        public void onOverlay(RenderGameOverlayEvent.Post event) {
            if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
                return;
            }

            if (!lobbyWatcher.getDetectedPlayers().isEmpty()) {
                HudRenderer.render(MC, lobbyWatcher.getDetectedPlayers());
            }
        }
    }
}
