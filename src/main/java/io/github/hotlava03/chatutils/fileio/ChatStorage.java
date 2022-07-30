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
    public static final String SINGLEPLAYER_ADDRESS = "minecraft.singleplayer";
    private static ChatStorage instance;
    private final Gson gson = new Gson();
    private JsonObject object = new JsonObject();
    private boolean lockingChatEvents = true;

    private ChatStorage() {
    }

    public static ChatStorage getInstance() {
        if (instance == null) instance = new ChatStorage();
        return instance;
    }

    public void push(String chatLine, String server) {
        if (chatLine.startsWith("[CHAT UTILS] ") || this.lockingChatEvents) return;

        if (!object.has(server)) object.add(server, new JsonArray());
        var serverArr = object.getAsJsonArray(server);
        serverArr.add(chatLine);

        object.add("timestamp." + server, new JsonPrimitive(System.currentTimeMillis()));

        if (serverArr.size() > MAX_ENTRIES) serverArr.remove(0);
    }

    public List<String> getStoredLines(String server) {
        if (!object.has(server)) return Collections.emptyList();

        var arr = object.getAsJsonArray(server);

        return StreamSupport.stream(arr.spliterator(), true)
                .map(JsonElement::getAsString)
                .toList();
    }

    public void remove(String server, int index) {
        if (!object.has(server)) return;

        object.getAsJsonArray(server).remove(index);
    }

    public void load() {
        var configFile = new File(IoUtils.getConfigDirectory(), "chatutils-chat.json");
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
                try (FileWriter fileWriter = new FileWriter(new File(dir, "chatutils-chat.json"))) {
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

    public boolean isLockingChatEvents() {
        return lockingChatEvents;
    }

    public void setLockingChatEvents(boolean locking) {
        this.lockingChatEvents = locking;
    }
}
