package io.github.hotlava03.chatutils.events;

import java.util.List;

import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ReceiveMessageCallback {
    Event<ReceiveMessageCallback> EVENT = EventFactory.createArrayBacked(
            ReceiveMessageCallback.class,
            (listeners) -> (text, lines) -> {
                for (ReceiveMessageCallback listener : listeners) {
                    listener.accept(text, lines);
                }
            });

    void accept(Component text, List<GuiMessage.Line> lines);
}
