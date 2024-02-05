package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.ChatHudUtils;
import io.github.hotlava03.chatutils.util.StringUtils;
import io.github.hotlava03.chatutils.util.TooltipAlert;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HudRenderListener implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        var client = MinecraftClient.getInstance();
        var alert = TooltipAlert.getInstance();

        alert.tick();

        if (client.currentScreen instanceof ChatScreen) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
            double x = getMouseX(client);
            double y = getMouseY(client);

            if (ChatUtilsConfig.ENABLE_COPY_KEY.value()) {
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
                        ChatUtilsConfig.COPY_KEY.value())) {
                    var clipboardString = Text.translatable("chat-utils.hud.keyPressed");
                    int strWidth = client.textRenderer.getWidth(clipboardString);
                    drawContext.drawText(client.textRenderer, clipboardString, width - strWidth - 5,
                            height - 32 - 5, 0x00FF00, true);

                    drawTooltip(drawContext, client, (int) x, (int) y, alert);
                }
            } else {
                drawTooltip(drawContext, client, (int) x, (int) y, alert);
            }

            var version = FabricLoader.getInstance().getModContainer("chat-utils")
                    .orElseThrow().getMetadata().getVersion().getFriendlyString();
            var chatUtilsString = "ChatUtils " + version;
            int strWidth = client.textRenderer.getWidth(chatUtilsString);
            var text = MutableText.of(PlainTextContent.EMPTY);
            text.append(chatUtilsString);
            drawContext.drawText(client.textRenderer, text, width - strWidth - 5,
                    height - 20 - 5, 0xCCCCCC, true);
        }
    }

    private void drawTooltip(DrawContext drawContext, MinecraftClient client, int x, int y, TooltipAlert alert) {
        ChatHudLine line = ChatHudUtils.getMessageAt(x, y);
        if (line == null) return;

        var style = client.inGameHud.getChatHud().getTextStyleAt(x, y);

        if (alert.isRunning() && alert.getCreationTicks() == line.creationTick()
                && ChatUtilsConfig.SHOW_ALERTS.value()) {
            var text = Text.translatable("chat-utils.hud.copiedToClipboard");
            text.setStyle(text.getStyle().withColor(TextColor.fromRgb(0x00FF00)));
            drawContext.drawTooltip(client.textRenderer, text, x, y);
        } else if (ChatUtilsConfig.TOOLTIP_ENABLED.value()
                && (style == null || style.getHoverEvent() == null)) {
            List<Text> tooltip;
            if (ChatUtilsConfig.PREVIEW_CONTENT.value()) {
                tooltip = new ArrayList<>();
                tooltip.add(toText(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value())));
                tooltip.add(Text.of(""));
                tooltip.addAll(Arrays.stream(StringUtils.wrap(line.content().copy()
                        .setStyle(Style.EMPTY).getString(), 25).split("\n")).map(Text::of).toList());
            } else {
                tooltip = Collections.singletonList(toText(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value())));
            }
            drawContext.drawTooltip(client.textRenderer, tooltip, x, y);
        }
    }

    private double getMouseX(MinecraftClient client) {
        return client.mouse.getX() * client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
    }

    private double getMouseY(MinecraftClient client) {
        return client.mouse.getY() * client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
    }

    private Text toText(Component component) {
        return FabricClientAudiences.of().toNative(component);
    }
}
