package io.github.hotlava03.chatutils.events.types;

import io.github.hotlava03.chatutils.events.ChatUtilsEvent;
import io.github.hotlava03.chatutils.events.ChatUtilsEventListener;
import io.github.hotlava03.chatutils.events.EventHandler;

public abstract class SendCommandListener implements ChatUtilsEventListener {
    @Override
    public void onEvent(ChatUtilsEvent event) {
        if (event instanceof SendCommandEvent e) {
            this.onCommandSent(e);
        } else {
            throw new IllegalStateException("Fired event is not a send command event in command sent listener.");
        }
    }

    public abstract void onCommandSent(SendCommandEvent e);

    @Override
    public EventHandler.EventType getType() {
        return EventHandler.EventType.MESSAGE_RECEIVE;
    }
}
