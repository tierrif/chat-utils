package io.github.hotlava03.chatutils;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;

public class ChatUtilsMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // To prevent HeadlessExceptions when copying to clipboard.
        System.setProperty("java.awt.headless", "false");
        ChatUtilsConfig.loadFromFile();
        LogManager.getLogger().info("Started!");
    }
}
