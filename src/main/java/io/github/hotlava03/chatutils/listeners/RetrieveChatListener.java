package io.github.hotlava03.chatutils.listeners;

import com.ibm.icu.util.Calendar;
import io.github.hotlava03.chatutils.events.JoinServerEvent;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
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
            var date = Calendar.getInstance().getTime();
            lines.add(StringUtils.formatAndTranslate("chat-utils.stored_messages", date.toString()));
            System.out.println("Server join event");

            lines.forEach((line) -> client.inGameHud.getChatHud().addMessage(Text.literal(line)));
            storage.setLockingChatEvents(false);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
