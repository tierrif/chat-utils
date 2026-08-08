package io.github.hotlava03.chatutils.events;

import java.util.function.Consumer;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.ChatScreen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChatScreenInitCallback {
    Event<ChatScreenInitCallback> EVENT = EventFactory.createArrayBacked(
            ChatScreenInitCallback.class,
            (listeners) -> (screen, addWidget) -> {
                for (ChatScreenInitCallback listener : listeners) {
                    listener.onInit(screen, addWidget);
                }
            });

    void onInit(ChatScreen screen, Consumer<AbstractWidget> addWidget);
}
