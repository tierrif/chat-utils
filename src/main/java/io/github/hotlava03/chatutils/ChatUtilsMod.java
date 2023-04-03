package io.github.hotlava03.chatutils;

import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.events.SendCommandCallback;
import io.github.hotlava03.chatutils.listeners.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.apache.logging.log4j.LogManager;

public class ChatUtilsMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // To prevent HeadlessExceptions when copying to clipboard.
        System.setProperty("java.awt.headless", "false");
        ChatUtilsConfig.loadFromFile();
        ChatStorage.getInstance().load();

        // Register events.
        ClientPlayConnectionEvents.INIT.register(new RetrieveChatListener());
        var sendMessageListener = new SendMessageListener();
        ClientSendMessageEvents.CHAT.register(sendMessageListener);
        ClientSendMessageEvents.COMMAND.register(sendMessageListener);
        ReceiveMessageCallback.EVENT.register(new AntiSpamListener());
        ReceiveMessageCallback.EVENT.register(new InjectChatCopyListener());
        ReceiveMessageCallback.EVENT.register(new ChatPersistListener());
        SendCommandCallback.EVENT.register(new CopyToClipboardListener());

        LogManager.getLogger().info("Started!");
    }
}
