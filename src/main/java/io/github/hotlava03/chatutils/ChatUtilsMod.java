package io.github.hotlava03.chatutils;

import net.minecraft.resources.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import org.apache.logging.log4j.LogManager;

import io.github.hotlava03.chatutils.events.ChatScreenInitCallback;
import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.listeners.*;
import io.github.hotlava03.chatutils.util.TooltipAlert;

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
        var chatFilterListener = new ChatFilterListener();
        ClientReceiveMessageEvents.ALLOW_CHAT.register(chatFilterListener);
        ClientReceiveMessageEvents.ALLOW_GAME.register(chatFilterListener);
        ReceiveMessageCallback.EVENT.register(new AntiSpamListener());
        ReceiveMessageCallback.EVENT.register(new ChatPersistListener());
        CopyToClipboardListener.EVENT.register(new CopyToClipboardListener());
        ChatScreenInitCallback.EVENT.register(new ShortcutOverlayListener());
        ClientTickEvents.END_CLIENT_TICK.register(new MacroKeyListener());

        // The tooltip/alert overlay has to sit on top of the chat it annotates.
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("chat-utils", "chat_tooltip"),
                new HudRenderListener());

        TooltipAlert.init();

        LogManager.getLogger().info("Started!");
    }
}
