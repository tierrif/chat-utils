package io.github.hotlava03.chatutils;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.MessageReceiveEvent;
import io.github.hotlava03.chatutils.events.SendCommandEvent;
import io.github.hotlava03.chatutils.listeners.AntiSpamListener;
import io.github.hotlava03.chatutils.listeners.CopyChatListener;
import io.github.hotlava03.chatutils.listeners.CopyToClipboardListener;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;

public class ChatUtilsMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // To prevent HeadlessExceptions when copying to clipboard.
        System.setProperty("java.awt.headless", "false");
        ChatUtilsConfig.loadFromFile();

        // Register events.
        MessageReceiveEvent.LISTENERS.register(new AntiSpamListener());
        MessageReceiveEvent.LISTENERS.register(new CopyChatListener());
        SendCommandEvent.LISTENERS.register(new CopyToClipboardListener());

        LogManager.getLogger().info("Started!");
    }
}
