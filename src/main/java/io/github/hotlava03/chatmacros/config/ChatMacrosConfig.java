package io.github.hotlava03.chatmacros.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

public class ChatMacrosConfig implements IConfigHandler {
    public static final ImmutableList<IConfigBase> OPTIONS = getConfig();

    private static ImmutableList<IConfigBase> getConfig() {
        return ImmutableList.of(
                new ConfigString("copyToClipboardMessage", Value.COPY_TO_CLIPBOARD_MESSAGE.getAsString(), "Message in the tooltip."),
                new ConfigBoolean("previewContent", Value.PREVIEW_CONTENT.getAsBoolean(), "Preview content in the tooltip."),
                new ConfigBoolean("copyColorsIfAny", Value.COPY_COLORS.getAsBoolean(), "Copy color codes or not."),
                new ConfigBoolean("enabled", Value.ENABLED.getAsBoolean(), "Click to disable chatmacros for compatibility issues.")
        );
    }

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), "chatmacros.json");

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "ChatMacros", OPTIONS);
            }
        }
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "ChatMacros", OPTIONS);
            JsonUtils.writeJsonToFile(root, new File(dir, "chatmacros.json"));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }

    private static ImmutableList<String> rawPresets() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        return builder
                .add(Value.COPY_TO_CLIPBOARD_MESSAGE.getAsString())
                .add(Value.PREVIEW_CONTENT.getAsString())
                .add(Value.ENABLED.getAsString())
                .build();
    }

    public enum Value {
        COPY_TO_CLIPBOARD_MESSAGE("&9Click to copy to clipboard."),
        PREVIEW_CONTENT("true"),
        COPY_COLORS("true"),
        ENABLED("true");

        private final String value;

        Value(String value) {
            this.value = value;
        }

        public String getAsString() {
            return value;
        }

        public boolean getAsBoolean() {
            return Boolean.parseBoolean(value);
        }
    }
}
