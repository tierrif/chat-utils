package io.github.hotlava03.chatutils.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.ComponentJson;

public class RetrieveChatListener implements ClientPlayConnectionEvents.Init {

    @Override
    public void onPlayInit(ClientPacketListener handler, Minecraft client) {
        var serverInfo = handler.getServerData();
        var address = serverInfo != null ? serverInfo.ip : null;
        if (address == null) return; // Single-player not yet supported

        var storage = ChatStorage.getInstance();

        // Chat Persist.
        if (ChatUtilsConfig.ENABLE_CHAT_PERSIST.value()) {
            handleChatPersist(storage, address, client);
        }

        // Command Persist.
        if (ChatUtilsConfig.ENABLE_COMMAND_PERSIST.value()) {
            handleCommandPersist(storage, address, client.gui.hud.getChat().getRecentChat());
        }
    }

    private void handleChatPersist(ChatStorage storage, String address, Minecraft client) {
        storage.setBlockingChatEvents(true);
        var chatLines = new ArrayList<>(storage.getStoredChatLines(address));
        if (chatLines.isEmpty()) {
            storage.setBlockingChatEvents(false);
            return;
        }
        var date = new Date(storage.getTimestamp(address));

        var chat = client.gui.hud.getChat();
        chatLines.forEach((line) -> {
            Component component = ComponentJson.fromJson(line);
            if (component != null) chat.addClientSystemMessage(component);
        });
        chat.addClientSystemMessage(Component.translatable("chat-utils.stored_messages", date));
        storage.setBlockingChatEvents(false);
    }

    private void handleCommandPersist(ChatStorage storage, String address, List<String> messageHistory) {
        messageHistory.addAll(new ArrayList<>(storage.getStoredCmdLines(address)));
    }
}
