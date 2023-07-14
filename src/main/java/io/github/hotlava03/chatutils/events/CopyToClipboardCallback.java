package io.github.hotlava03.chatutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.kyori.adventure.text.Component;

public interface CopyToClipboardCallback {
    Event<CopyToClipboardCallback> EVENT = EventFactory.createArrayBacked(
            CopyToClipboardCallback.class,
            (listeners) -> (component, creationTicks) -> {
                for (CopyToClipboardCallback listener : listeners) {
                    listener.accept(component, creationTicks);
                }
            });

    void accept(Component component, int creationTicks);
}
