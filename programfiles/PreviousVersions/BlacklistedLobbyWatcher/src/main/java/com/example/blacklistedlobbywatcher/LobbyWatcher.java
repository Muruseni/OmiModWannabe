package com.example.blacklistedlobbywatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LobbyWatcher {
    private final Minecraft mc;
    private final BlacklistManager blacklistManager;

    /*
     * UUID -> display name.
     *
     * This contains only blacklisted players currently present in the
     * client's player-info list. We do not send anything to the server.
     */
    private final Map<UUID, String> detectedPlayers = new LinkedHashMap<UUID, String>();

    /*
     * Used to prevent repeated pings while a player remains in the lobby.
     * A player is eligible for a new alert after they disappear and later
     * return.
     */
    private final Map<UUID, String> previousPlayers = new LinkedHashMap<UUID, String>();

    private int tickCounter = 0;

    public LobbyWatcher(Minecraft mc, BlacklistManager blacklistManager) {
        this.mc = mc;
        this.blacklistManager = blacklistManager;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (++tickCounter < 5) {
            return;
        }
        tickCounter = 0;

        if (mc.thePlayer == null || mc.getNetHandler() == null) {
            detectedPlayers.clear();
            previousPlayers.clear();
            return;
        }

        Collection<NetworkPlayerInfo> playerInfo = mc.getNetHandler().getPlayerInfoMap();

        Map<UUID, String> currentBlacklisted = new LinkedHashMap<UUID, String>();

        for (NetworkPlayerInfo info : playerInfo) {
            if (info == null || info.getGameProfile() == null) {
                continue;
            }

            UUID uuid = info.getGameProfile().getId();
            String name = info.getGameProfile().getName();

            if (uuid == null || name == null) {
                continue;
            }

            if (blacklistManager.contains(name)) {
                currentBlacklisted.put(uuid, name);

                if (!previousPlayers.containsKey(uuid)) {
                    alert(name);
                }
            }
        }

        detectedPlayers.clear();
        detectedPlayers.putAll(currentBlacklisted);

        previousPlayers.clear();
        previousPlayers.putAll(currentBlacklisted);
    }

    private void alert(String name) {
        mc.getSoundHandler().playSound(
                PositionedSoundRecord.create(
                        new ResourceLocation("gui.button.press"),
                        1.0F
                )
        );

        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(
                    new net.minecraft.util.ChatComponentText(
                            EnumChatFormatting.RED + "[Blacklist] "
                                    + EnumChatFormatting.WHITE + name
                                    + EnumChatFormatting.RED + " is in the lobby."
                    )
            );
        }
    }

    public List<String> getDetectedPlayers() {
        return Collections.unmodifiableList(
                new ArrayList<String>(detectedPlayers.values())
        );
    }
}
