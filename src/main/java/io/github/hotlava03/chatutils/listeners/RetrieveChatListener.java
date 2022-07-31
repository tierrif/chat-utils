package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.JoinServerEvent;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

public class RetrieveChatListener implements Consumer<JoinServerEvent> {
    @Override
    public void accept(JoinServerEvent e) {
        var client = MinecraftClient.getInstance();
        var serverInfo = client.getCurrentServerEntry();
        var address = serverInfo != null ? serverInfo.address : null;
        if (address == null) {
            LogManager.getLogger().error("Unexpected missing server info for joined server.");
            client.inGameHud.getChatHud().addMessage(Text.literal(
                    StringUtils.translateAlternateColorCodes("&c[CHAT UTILS] &4Unexpected: Missing server/world info upon server/world join.")
            ));
            return;
        }

        var storage = ChatStorage.getInstance();
        storage.setBlockingChatEvents(true);

        // Chat Persist.

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

        // Command Persist.

        var cmdLines = new ArrayList<>(storage.getStoredCmdLines(address));
        if (cmdLines.isEmpty()) {
            storage.setBlockingChatEvents(false);
            return;
        }

        cmdLines.forEach((line) -> e.getMessageHistory().add(line));

        storage.setBlockingChatEvents(false);
    }
}
