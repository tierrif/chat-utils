package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.JoinServerEvent;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

public class RetrieveChatListener implements Consumer<JoinServerEvent> {
    @Override
    public void accept(JoinServerEvent e) {
        try {
            var client = MinecraftClient.getInstance();
            var serverInfo = client.getCurrentServerEntry();
            var address = serverInfo != null ? serverInfo.address : ChatStorage.SINGLEPLAYER_ADDRESS;
            var storage = ChatStorage.getInstance();
            storage.setLockingChatEvents(true);

            var lines = new ArrayList<>(storage.getStoredLines(address));
            if (lines.isEmpty()) {
                storage.setLockingChatEvents(false);
                return;
            }
            var date = new Date(storage.getTimestamp(address));

            lines.forEach((line) -> client.inGameHud.getChatHud().addMessage(Text.Serializer.fromJson(line)));
            client.inGameHud.getChatHud().addMessage(Text.literal(
                    StringUtils.translateAlternateColorCodes(Text.translatable("chat-utils.stored_messages").getString() + date)
            ));
            storage.setLockingChatEvents(false);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
