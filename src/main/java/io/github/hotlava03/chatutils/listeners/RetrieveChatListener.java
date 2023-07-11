package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.mixin.ChatHudAccessor;
import io.github.hotlava03.chatutils.util.StringUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RetrieveChatListener implements ClientPlayConnectionEvents.Init {

    @Override
    public void onPlayInit(ClientPlayNetworkHandler handler, MinecraftClient client) {
        var serverInfo = handler.getServerInfo();
        var address = serverInfo != null ? serverInfo.address : null;
        if (address == null) return; // Singleplayer not yet supported

        var storage = ChatStorage.getInstance();

        // Chat Persist.
        if (ChatUtilsConfig.ENABLE_CHAT_PERSIST.value()) {
            handleChatPersist(storage, address, client);
        }

        // Command Persist.
        if (ChatUtilsConfig.ENABLE_COMMAND_PERSIST.value()) {
            handleCommandPersist(storage, address, ((ChatHudAccessor) client.inGameHud.getChatHud()).getMessageHistory());
        }
    }

    private void handleChatPersist(ChatStorage storage, String address, MinecraftClient client) {
        storage.setBlockingChatEvents(true);
        var chatLines = new ArrayList<>(storage.getStoredChatLines(address));
        if (chatLines.isEmpty()) {
            storage.setBlockingChatEvents(false);
            return;
        }
        var date = new Date(storage.getTimestamp(address));

        chatLines.forEach((line) -> client.inGameHud.getChatHud().addMessage(Text.Serializer.fromJson(line)));
        client.inGameHud.getChatHud().addMessage(Text.literal(
                StringUtils.translateAlternateColorCodes(Text.translatable("chat-utils.stored_messages", date).getString())
        ));
        storage.setBlockingChatEvents(false);
    }

    private void handleCommandPersist(ChatStorage storage, String address, List<String> messageHistory) {
        messageHistory.addAll(new ArrayList<>(storage.getStoredCmdLines(address)));
    }
}
