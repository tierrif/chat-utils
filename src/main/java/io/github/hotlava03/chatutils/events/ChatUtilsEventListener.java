package io.github.hotlava03.chatutils.events;

public interface ChatUtilsEventListener {
    void onEvent(ChatUtilsEvent event);

    EventHandler.EventType getType();
}
