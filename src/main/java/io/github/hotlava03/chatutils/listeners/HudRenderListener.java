package io.github.hotlava03.chatutils.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.loader.api.FabricLoader;

import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.ChatHudUtils;
import io.github.hotlava03.chatutils.util.StringUtils;
import io.github.hotlava03.chatutils.util.TooltipAlert;

public class HudRenderListener implements HudElement {
    @Override
    public void extractRenderState(
            @NotNull GuiGraphicsExtractor graphics,
            @NotNull DeltaTracker deltaTracker
    ) {
        var client = Minecraft.getInstance();
        var alert = TooltipAlert.getInstance();

        alert.tick();

        if (client.gui.screen() instanceof ChatScreen) {
            int width = graphics.guiWidth();
            int height = graphics.guiHeight();
            double x = client.mouseHandler.getScaledXPos(client.getWindow());
            double y = client.mouseHandler.getScaledYPos(client.getWindow());

            if (ChatUtilsConfig.ENABLE_COPY_KEY.value()) {
                if (InputConstants.isKeyDown(client.getWindow(), ChatUtilsConfig.COPY_KEY.value())) {
                    var clipboardString = Component.translatable("chat-utils.hud.keyPressed");
                    int strWidth = client.font.width(clipboardString);
                    graphics.text(client.font, clipboardString, width - strWidth - 5,
                            height - 32 - 5, 0xFF00FF00, true);

                    drawTooltip(graphics, client, (int) x, (int) y, alert);
                }
            } else {
                drawTooltip(graphics, client, (int) x, (int) y, alert);
            }

            var version = FabricLoader.getInstance().getModContainer("chat-utils")
                    .orElseThrow().getMetadata().getVersion().getFriendlyString();
            var chatUtilsString = "ChatUtils " + version;
            int strWidth = client.font.width(chatUtilsString);
            graphics.text(client.font, chatUtilsString, width - strWidth - 5,
                    height - 20 - 5, 0xFFCCCCCC, true);
        }
    }

    private void drawTooltip(
            GuiGraphicsExtractor graphics,
            Minecraft client,
            int x,
            int y,
            TooltipAlert alert
    ) {
        GuiMessage line = ChatHudUtils.getMessageAt(x, y);
        if (line == null) return;

        if (alert.isRunning() && alert.getCreationTicks() == line.addedTime()
                && ChatUtilsConfig.SHOW_ALERTS.value()
        ) {
            var text = Component.translatable("chat-utils.hud.copiedToClipboard");
            text.setStyle(text.getStyle().withColor(TextColor.fromRgb(0x00FF00)));

            graphics.setTooltipForNextFrame(client.font, text, x, y);
        } else if (ChatUtilsConfig.TOOLTIP_ENABLED.value()) {
            List<Component> tooltip;
            if (ChatUtilsConfig.PREVIEW_CONTENT.value()) {
                tooltip = new ArrayList<>();
                tooltip.add(toText(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value())));
                tooltip.add(Component.empty());

                var preview = StringUtils.componentToPlainText(StringUtils.unpackLegacyCodes(
                        StringUtils.asAdventure(line.content())));

                tooltip.addAll(Arrays.stream(StringUtils.wrap(preview, 25)
                        .replace("\r", "")
                        .split("\n")).map(Component::literal).toList());
            } else {
                tooltip = Collections.singletonList(toText(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value())));
            }

            graphics.setComponentTooltipForNextFrame(client.font, tooltip, x, y);
        }
    }

    private Component toText(net.kyori.adventure.text.Component component) {
        return MinecraftClientAudiences.of().asNative(component);
    }
}
