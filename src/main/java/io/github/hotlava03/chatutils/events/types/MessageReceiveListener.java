package io.github.hotlava03.chatutils.events.types;

import io.github.hotlava03.chatutils.events.ChatUtilsEvent;
import io.github.hotlava03.chatutils.events.ChatUtilsEventListener;
import io.github.hotlava03.chatutils.events.EventHandler;

public abstract class MessageReceiveListener implements ChatUtilsEventListener {
    @Override
    public void onEvent(ChatUtilsEvent event) {
        if (event instanceof MessageReceiveEvent e) {
            this.onMessageReceive(e);
        } else {
            throw new IllegalStateException("Fired event is not a message receive event in message receive listener.");
        }
    }

    public abstract void onMessageReceive(MessageReceiveEvent e);

    @Override
    public EventHandler.EventType getType() {
        return EventHandler.EventType.MESSAGE_RECEIVE;
    }
}
