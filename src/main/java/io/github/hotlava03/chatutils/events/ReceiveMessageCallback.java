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
                var result = text;
                for (ReceiveMessageCallback listener : listeners) {
                    result = listener.accept(result, lines);
                }

                return result;
            });

    Component accept(Component text, List<GuiMessage.Line> lines);
}
