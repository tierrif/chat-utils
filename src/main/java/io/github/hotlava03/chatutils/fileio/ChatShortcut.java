package io.github.hotlava03.chatutils.fileio;

import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatShortcut {
    private String label;
    private String message;
    private ShortcutColor color;
    private int gridX;
    private int gridY;
    private boolean anchorRight;

    public ChatShortcut() {
        this("", "", ShortcutColor.DEFAULT, 0, 0, false);
    }

    public ChatShortcut(String label, String message, ShortcutColor color,
                        int gridX, int gridY, boolean anchorRight) {
        this.label = label;
        this.message = message;
        this.color = color;
        this.gridX = gridX;
        this.gridY = gridY;
        this.anchorRight = anchorRight;
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

    public String displayLabel() {
        return this.label.isBlank() ? this.message : this.label;
    }

    public ShortcutColor color() {
        return this.color;
    }

    public void setColor(ShortcutColor color) {
        this.color = color;
    }

    public int gridX() {
        return this.gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int gridY() {
        return this.gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public boolean anchorRight() {
        return this.anchorRight;
    }

    public void setAnchorRight(boolean anchorRight) {
        this.anchorRight = anchorRight;
    }

    public ChatShortcut copy() {
        return new ChatShortcut(this.label, this.message, this.color, this.gridX, this.gridY, this.anchorRight);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ChatShortcut shortcut
                && this.gridX == shortcut.gridX
                && this.gridY == shortcut.gridY
                && this.anchorRight == shortcut.anchorRight
                && this.color == shortcut.color
                && this.label.equals(shortcut.label)
                && this.message.equals(shortcut.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.label, this.message, this.color, this.gridX, this.gridY, this.anchorRight);
    }

    public JsonObject toJson() {
        var object = new JsonObject();
        object.addProperty("label", this.label);
        object.addProperty("message", this.message);
        object.addProperty("color", this.color.name());
        object.addProperty("gridX", this.gridX);
        object.addProperty("gridY", this.gridY);
        object.addProperty("anchorRight", this.anchorRight);

        return object;
    }

    public static ChatShortcut fromJson(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }

        var object = element.getAsJsonObject();
        var message = object.get("message");
        if (message == null || !message.isJsonPrimitive()) {
            return null;
        }

        return new ChatShortcut(
                ChatMacro.readString(object, "label"),
                message.getAsString(),
                ShortcutColor.byName(ChatMacro.readString(object, "color")),
                readInt(object, "gridX"),
                readInt(object, "gridY"),
                readFlag(object, "anchorRight"));
    }

    private static int readInt(JsonObject object, String name) {
        var value = object.get(name);

        return value != null && value.isJsonPrimitive() ? Math.max(0, value.getAsInt()) : 0;
    }

    private static boolean readFlag(JsonObject object, String name) {
        var flag = object.get(name);

        return flag != null && flag.isJsonPrimitive() && flag.getAsBoolean();
    }

    public static List<ChatShortcut> dropBlank(List<ChatShortcut> shortcuts) {
        return shortcuts.stream().filter(shortcut -> !shortcut.message().isBlank()).toList();
    }
}
