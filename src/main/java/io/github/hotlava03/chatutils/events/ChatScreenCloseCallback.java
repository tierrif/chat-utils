package io.github.hotlava03.chatutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.ChatScreen;

public interface ChatScreenCloseCallback {
    Event<ChatScreenCloseCallback> EVENT = EventFactory.createArrayBacked(
            ChatScreenCloseCallback.class,
            (listeners) -> (screen) -> {
                for (ChatScreenCloseCallback listener : listeners) {
                    listener.onClose(screen);
                }
            });

    void onClose(ChatScreen screen);
}
