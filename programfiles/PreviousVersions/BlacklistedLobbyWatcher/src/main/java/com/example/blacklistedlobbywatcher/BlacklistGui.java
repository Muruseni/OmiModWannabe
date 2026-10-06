package com.example.blacklistedlobbywatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlacklistGui extends GuiScreen {
    private GuiTextField nameField;
    private int selectedBlacklistIndex = -1;
    private int selectedDetectedIndex = -1;

    private GuiButton addButton;
    private GuiButton removeButton;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int centerX = width / 2;

        nameField = new GuiTextField(
                0,
                fontRendererObj,
                centerX - 100,
                35,
                200,
                20
        );
        nameField.setMaxStringLength(16);

        buttonList.clear();

        addButton = new GuiButton(
                1,
                centerX - 100,
                60,
                95,
                20,
                "Add"
        );

        removeButton = new GuiButton(
                2,
                centerX + 5,
                60,
                95,
                20,
                "Remove"
        );

        buttonList.add(addButton);
        buttonList.add(removeButton);

        buttonList.add(new GuiButton(
                3,
                centerX - 100,
                height - 30,
                200,
                20,
                "Done"
        ));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                BlacklistedLobbyWatcher.getBlacklistManager().add(name);
                nameField.setText("");
            }
        } else if (button.id == 2) {
            if (selectedBlacklistIndex >= 0) {
                List<String> entries =
                        BlacklistedLobbyWatcher.getBlacklistManager().getEntries();

                if (selectedBlacklistIndex < entries.size()) {
                    BlacklistedLobbyWatcher.getBlacklistManager()
                            .remove(entries.get(selectedBlacklistIndex));
                    selectedBlacklistIndex = -1;
                }
            }
        } else if (button.id == 3) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
            throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);

        int centerX = width / 2;

        // Blacklist column
        int leftX = centerX - 155;
        int topY = 95;

        List<String> entries =
                BlacklistedLobbyWatcher.getBlacklistManager().getEntries();

        for (int i = 0; i < entries.size(); i++) {
            int rowY = topY + i * 14;

            if (mouseX >= leftX && mouseX <= leftX + 145
                    && mouseY >= rowY && mouseY < rowY + 14) {
                selectedBlacklistIndex = i;
                selectedDetectedIndex = -1;
            }
        }

        // Detected column
        int rightX = centerX + 10;
        List<String> detected =
                BlacklistedLobbyWatcher.getLobbyWatcher().getDetectedPlayers();

        for (int i = 0; i < detected.size(); i++) {
            int rowY = topY + i * 14;

            if (mouseX >= rightX && mouseX <= rightX + 145
                    && mouseY >= rowY && mouseY < rowY + 14) {
                selectedDetectedIndex = i;
                selectedBlacklistIndex = -1;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }

        nameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        nameField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int centerX = width / 2;

        drawCenteredString(
                fontRendererObj,
                "Blacklist Lobby Watcher",
                centerX,
                10,
                0xFFFFFF
        );

        nameField.drawTextBox();

        drawCenteredString(
                fontRendererObj,
                "Blacklist",
                centerX - 80,
                82,
                0xFFFF5555
        );

        drawCenteredString(
                fontRendererObj,
                "Currently detected",
                centerX + 80,
                82,
                0xFF55FF55
        );

        List<String> entries =
                BlacklistedLobbyWatcher.getBlacklistManager().getEntries();

        int leftX = centerX - 155;
        int topY = 95;

        for (int i = 0; i < entries.size(); i++) {
            boolean selected = i == selectedBlacklistIndex;

            drawRect(
                    leftX - 2,
                    topY + i * 14 - 1,
                    leftX + 143,
                    topY + i * 14 + 13,
                    selected ? 0xAA555555 : 0x55222222
            );

            fontRendererObj.drawString(
                    entries.get(i),
                    leftX,
                    topY + i * 14,
                    0xFFFFFF
            );
        }

        List<String> detected =
                BlacklistedLobbyWatcher.getLobbyWatcher().getDetectedPlayers();

        int rightX = centerX + 10;

        for (int i = 0; i < detected.size(); i++) {
            boolean selected = i == selectedDetectedIndex;

            drawRect(
                    rightX - 2,
                    topY + i * 14 - 1,
                    rightX + 143,
                    topY + i * 14 + 13,
                    selected ? 0xAA555555 : 0x55222222
            );

            fontRendererObj.drawString(
                    detected.get(i),
                    rightX,
                    topY + i * 14,
                    0xFF55FF55
            );
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
