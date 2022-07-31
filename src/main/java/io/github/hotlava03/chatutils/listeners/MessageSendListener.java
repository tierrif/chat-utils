package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.MessageSendEvent;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

public class MessageSendListener implements Consumer<MessageSendEvent> {
    @Override
    public void accept(MessageSendEvent e) {
        var client = MinecraftClient.getInstance();
        var serverInfo = client.getCurrentServerEntry();
        var address = serverInfo != null ? serverInfo.address : null;
        if (address == null) return; // Don't store if it's singleplayer.

        if (e.getLine().startsWith("/chatmacros ")) return;

        var storage = ChatStorage.getInstance();
        storage.pushCmd(e.getLine(), address);
        storage.saveAsync();
    }
}
