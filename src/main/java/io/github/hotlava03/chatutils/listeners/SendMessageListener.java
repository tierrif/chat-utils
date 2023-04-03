package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.fileio.ChatStorage;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;

public class SendMessageListener implements
        ClientSendMessageEvents.Chat,
        ClientSendMessageEvents.Command {
    @Override
    public void onSendChatMessage(String message) {
        handleMessage(message);
    }

    @Override
    public void onSendCommandMessage(String command) {
        handleMessage("/" + command);
    }

    private void handleMessage(String message) {
        var client = MinecraftClient.getInstance();
        var serverInfo = client.getCurrentServerEntry();
        var address = serverInfo != null ? serverInfo.address : null;
        if (address == null) return; // Don't store if it's singleplayer.

        // Not needed anymore, these events don't get called with run command click events.
        // if (message.startsWith("/chatmacros ")) return;

        var storage = ChatStorage.getInstance();
        var cmds = storage.getStoredCmdLines(address);
        if (!cmds.isEmpty() && cmds.get(cmds.size() - 1).equals(message)) return;
        storage.pushCmd(message, address);
        storage.saveAsync();
    }
}
