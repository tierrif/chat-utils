package io.github.hotlava03.chatutils.fileio;

import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.lwjgl.glfw.GLFW;

import io.github.hotlava03.chatutils.util.KeyUtils;

public class ChatMacro {
    private String label;
    private String message;
    private int key;

    public ChatMacro() {
        this("", "", GLFW.GLFW_KEY_UNKNOWN);
    }

    public ChatMacro(String label, String message, int key) {
        this.label = label;
        this.message = message;
        this.key = key;
    }

    public String label() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String message() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int key() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = KeyUtils.isValidKey(key) ? key : GLFW.GLFW_KEY_UNKNOWN;
    }

    public boolean isBound() {
        return KeyUtils.isValidKey(this.key);
    }

    public ChatMacro copy() {
        return new ChatMacro(this.label, this.message, this.key);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ChatMacro macro
                && this.key == macro.key
                && this.label.equals(macro.label)
                && this.message.equals(macro.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.label, this.message, this.key);
    }

    public JsonObject toJson() {
        var object = new JsonObject();
        object.addProperty("label", this.label);
        object.addProperty("message", this.message);
        object.addProperty("key", this.key);

        return object;
    }

    public static ChatMacro fromJson(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        var object = element.getAsJsonObject();
        var message = object.get("message");
        if (message == null || !message.isJsonPrimitive()) {
            return null;
        }

        var macro = new ChatMacro(readString(object, "label"), message.getAsString(), GLFW.GLFW_KEY_UNKNOWN);
        var key = object.get("key");

        // setKey drops anything GLFW would reject, which would otherwise log an error every tick.
        if (key != null && key.isJsonPrimitive()) {
            macro.setKey(key.getAsInt());
        }

        return macro;
    }

    static String readString(JsonObject object, String name) {
        var value = object.get(name);

        return value != null && value.isJsonPrimitive() ? value.getAsString() : "";
    }

    public static List<ChatMacro> dropBlank(List<ChatMacro> macros) {
        return macros.stream().filter(macro -> !macro.message().isBlank()).toList();
    }
}
