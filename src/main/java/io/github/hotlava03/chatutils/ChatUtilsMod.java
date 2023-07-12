package io.github.hotlava03.chatutils;

import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.listeners.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;

public class ChatUtilsMod implements ModInitializer {
    private static ChatUtilsMod instance;

    private KeyBinding holdKey = new KeyBinding(
            "key.chat-utils.hold", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.chat-utils");

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
        CopyToClipboardListener.EVENT.register(new CopyToClipboardListener());

        holdKey = KeyBindingHelper.registerKeyBinding(holdKey);
        instance = this;

        LogManager.getLogger().info("Started!");
    }

    public KeyBinding getHoldKey() {
        return holdKey;
    }

    public static ChatUtilsMod getInstance() {
        return instance;
    }
}
