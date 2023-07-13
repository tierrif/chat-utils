package io.github.hotlava03.chatutils;

import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.listeners.*;
import io.github.hotlava03.chatutils.mixin.ChatHudAccessor;
import io.github.hotlava03.chatutils.util.ChatHudUtils;
import io.github.hotlava03.chatutils.util.StringUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        ReceiveMessageCallback.EVENT.register(new ChatPersistListener());
        CopyToClipboardListener.EVENT.register(new CopyToClipboardListener());
        HudRenderCallback.EVENT.register(new HudRenderListener());

        LogManager.getLogger().info("Started!");
    }
}
