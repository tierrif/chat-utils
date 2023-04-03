package io.github.hotlava03.chatutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

import java.util.List;

public interface ReceiveMessageCallback {
    Event<ReceiveMessageCallback> EVENT = EventFactory.createArrayBacked(
            ReceiveMessageCallback.class,
            (listeners) -> (text, lines) -> {
                for (ReceiveMessageCallback listener : listeners) {
                    listener.accept(text, lines);
                }
            });

    void accept(Text text, List<ChatHudLine.Visible> lines);
}
