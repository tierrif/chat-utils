package io.github.hotlava03.chatutils.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import net.minecraft.network.chat.Component;

public final class FilteredMessageLog {
    public static final int MAX_ENTRIES = 200;

    private static final FilteredMessageLog INSTANCE = new FilteredMessageLog();

    private final Deque<Entry> entries = new ArrayDeque<>();

    private FilteredMessageLog() {}

    public static FilteredMessageLog getInstance() {
        return INSTANCE;
    }

    public synchronized void record(Component message, String filter) {
        entries.addFirst(new Entry(message, filter, System.currentTimeMillis()));

        while (entries.size() > MAX_ENTRIES) {
            entries.removeLast();
        }
    }

    public synchronized List<Entry> entries() {
        return List.copyOf(entries);
    }

    public synchronized void clear() {
        entries.clear();
    }

    public record Entry(Component message, String filter, long timestamp) {}
}
