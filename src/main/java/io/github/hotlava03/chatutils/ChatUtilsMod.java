package io.github.hotlava03.chatutils;

import io.github.hotlava03.chatutils.events.JoinServerEvent;
import io.github.hotlava03.chatutils.events.MessageSendEvent;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.MessageReceiveEvent;
import io.github.hotlava03.chatutils.events.CommandSendEvent;
import io.github.hotlava03.chatutils.listeners.*;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;

public class ChatUtilsMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // To prevent HeadlessExceptions when copying to clipboard.
        System.setProperty("java.awt.headless", "false");
        ChatUtilsConfig.loadFromFile();
        ChatStorage.getInstance().load();

        // Register events.
        MessageReceiveEvent.LISTENERS.register(new AntiSpamListener());
        MessageReceiveEvent.LISTENERS.register(new CopyChatListener());
        MessageReceiveEvent.LISTENERS.register(new ChatPersistListener());
        JoinServerEvent.LISTENERS.register(new RetrieveChatListener());
        CommandSendEvent.LISTENERS.register(new CopyToClipboardListener());
        MessageSendEvent.LISTENERS.register(new MessageSendListener());

        LogManager.getLogger().info("Started!");
    }
}
