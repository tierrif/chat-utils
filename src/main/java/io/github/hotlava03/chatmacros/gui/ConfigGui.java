package io.github.hotlava03.chatmacros.gui;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import io.github.hotlava03.chatmacros.config.ChatMacrosConfig;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class ConfigGui extends GuiConfigsBase {
    public ConfigGui() {
        super(10, 50, "chatmacros", null, "ChatMacros config");
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(ChatMacrosConfig.OPTIONS);
    }

    @Override
    protected void onSettingsChanged() {
        super.onSettingsChanged();
        ChatMacrosConfig.saveToFile();
        ChatMacrosConfig.loadFromFile();
        LogManager.getLogger().info("Saved settings.");
    }
}
