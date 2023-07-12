package io.github.hotlava03.chatutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.kyori.adventure.text.Component;

public interface CopyToClipboardCallback {
    Event<CopyToClipboardCallback> EVENT = EventFactory.createArrayBacked(
            CopyToClipboardCallback.class,
            (listeners) -> (component) -> {
                for (CopyToClipboardCallback listener : listeners) {
                    listener.accept(component);
                }
            });

    void accept(Component component);
}
