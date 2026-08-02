package io.github.hotlava03.chatutils.listeners;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;

public class SendMessageListener implements
        ClientSendMessageEvents.Chat,
        ClientSendMessageEvents.Command {
    @Override
    public void onSendChatMessage(@NotNull String message) {
        handleMessage(message);
    }

    @Override
    public void onSendCommandMessage(@NotNull String command) {
        handleMessage("/" + command);
    }

    private void handleMessage(String message) {
        if (!ChatUtilsConfig.ENABLE_COMMAND_PERSIST.value()) {
            return;
        }

        var client = Minecraft.getInstance();
        var serverInfo = client.getCurrentServer();
        var address = serverInfo != null ? serverInfo.ip : null;
        if (address == null) {
            return; // Don't store if it's single-player.
        }

        var storage = ChatStorage.getInstance();
        var commands = storage.getStoredCmdLines(address);
        if (!commands.isEmpty() && commands.getLast().equals(message)) {
            return;
        }

        storage.pushCmd(message, address);
        storage.saveAsync();
    }
}
