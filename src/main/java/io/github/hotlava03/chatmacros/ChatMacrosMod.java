package io.github.hotlava03.chatmacros;

import io.github.hotlava03.chatmacros.config.ChatMacrosConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;

public class ChatMacrosMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // To prevent HeadlessExceptions when copying to clipboard.
        System.setProperty("java.awt.headless", "false");
        ChatMacrosConfig.loadFromFile();
        LogManager.getLogger().info("Started!");
    }
}
