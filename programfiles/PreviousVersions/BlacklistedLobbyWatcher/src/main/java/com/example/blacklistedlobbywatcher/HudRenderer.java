package com.example.blacklistedlobbywatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.util.List;

public final class HudRenderer {
    private HudRenderer() {}

    public static void render(Minecraft mc, List<String> players) {
        ScaledResolution resolution = new ScaledResolution(mc);

        int x = 8;
        int y = 8;
        int width = 150;
        int lineHeight = 12;
        int height = 20 + players.size() * lineHeight;

        mc.fontRendererObj.drawRect(
                x - 3,
                y - 3,
                x + width,
                y + height,
                0xAA111111
        );

        FontRenderer font = mc.fontRendererObj;
        font.drawStringWithShadow(
                "Blacklist Watcher",
                x,
                y,
                0xFFFF5555
        );

        int currentY = y + 12;
        for (String player : players) {
            font.drawStringWithShadow(
                    "• " + player,
                    x,
                    currentY,
                    0xFFFFFFFF
            );
            currentY += lineHeight;
        }
    }
}
