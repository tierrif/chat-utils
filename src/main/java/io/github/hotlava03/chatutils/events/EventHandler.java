package io.github.hotlava03.chatutils.events;

import io.github.hotlava03.chatutils.listeners.AntiSpamListener;
import io.github.hotlava03.chatutils.listeners.CopyChatListener;
import io.github.hotlava03.chatutils.listeners.CopyToClipboardListener;

import java.util.Arrays;
import java.util.List;

public class EventHandler {
    private static EventHandler instance;
    private final List<ChatUtilsEventListener> listeners;

    private EventHandler() {
        this.listeners = Arrays.asList(
                new AntiSpamListener(),
                new CopyChatListener(),
                new CopyToClipboardListener()
        );
    }

    public static EventHandler getInstance() {
        if (instance == null) instance = new EventHandler();
        return instance;
    }

    public void fire(EventType type, ChatUtilsEvent event) {
        this.listeners.stream()
                .filter((listener) -> listener.getType() == type)
                .forEach((listener) -> listener.onEvent(event));
    }

    public enum EventType {
        MESSAGE_RECEIVE, SEND_COMMAND
    }
}
