package io.github.hotlava03.chatutils.fileio;

import com.google.gson.*;
import io.github.hotlava03.chatutils.util.IoUtils;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

public class ChatStorage {
    public static final int MAX_ENTRIES = 100;
    private static ChatStorage instance;
    private final Gson gson = new Gson();
    private JsonObject object = new JsonObject();
    private boolean blockingChatEvents = true;

    private ChatStorage() {
    }

    public static ChatStorage getInstance() {
        if (instance == null) instance = new ChatStorage();
        return instance;
    }

    public void pushChat(String chatLine, String server) {
        if (chatLine.startsWith("[CHAT UTILS] ") || this.blockingChatEvents) return;
        this.push("chat.", chatLine, server);
        object.add("timestamp." + server, new JsonPrimitive(System.currentTimeMillis()));
    }

    public void pushCmd(String cmd, String server) {
        this.push("cmd.", cmd, server);
    }

    public List<String> getStoredChatLines(String server) {
        return this.getLines("chat.", server);
    }

    public List<String> getStoredCmdLines(String server) {
        return this.getLines("cmd.", server);
    }

    public void remove(String server, int index) {
        if (!object.has("chat." + server)) return;

        object.getAsJsonArray("chat." + server).remove(index);
    }

    public void load() {
        var configFile = new File(IoUtils.getConfigDirectory(), "history.json");
        try (var fileReader = new FileReader(configFile)) {
            var element = gson.fromJson(fileReader, JsonElement.class);

            this.object = element != null && element.isJsonObject()
                    ? element.getAsJsonObject()
                    : new JsonObject();
        } catch (IOException exception) {
            this.object = new JsonObject();
            if (!(exception instanceof FileNotFoundException)) {
                LogManager.getLogger().error("Failed to read chat storage!", exception);
            }
        }
    }

    public void saveAsync() {
        var root = this.object.deepCopy();
        var dir = IoUtils.getConfigDirectory();
        new Thread(() -> {
            if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
                try (FileWriter fileWriter = new FileWriter(new File(dir, "history.json"))) {
                    gson.toJson(root, fileWriter);
                } catch (IOException e) {
                    LogManager.getLogger().error("[chat-utils] Failed to save chat line!", e);
                }
            }
        }).start();
    }

    public long getTimestamp(String server) {
        return object.get("timestamp." + server).getAsLong();
    }

    public boolean isBlockingChatEvents() {
        return blockingChatEvents;
    }

    public void setBlockingChatEvents(boolean locking) {
        this.blockingChatEvents = locking;
    }

    private void push(String prefix, String toPush, String server) {
        if (!object.has(prefix + server)) object.add(prefix + server, new JsonArray());
        var serverArr = object.getAsJsonArray(prefix + server);
        serverArr.add(toPush);

        if (serverArr.size() > MAX_ENTRIES) serverArr.remove(0);
    }

    private List<String> getLines(String type, String server) {
        if (!object.has(server)) return Collections.emptyList();

        var arr = object.getAsJsonArray(type + server);

        return StreamSupport.stream(arr.spliterator(), true)
                .map(JsonElement::getAsString)
                .toList();
    }
}
