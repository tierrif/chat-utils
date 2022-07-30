package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.EventHandler;
import io.github.hotlava03.chatutils.events.types.MessageReceiveEvent;
import io.github.hotlava03.chatutils.events.types.MessageReceiveListener;
import net.minecraft.text.*;

import java.util.regex.Pattern;

import static io.github.hotlava03.chatutils.util.StringUtils.textToLegacy;
import static io.github.hotlava03.chatutils.util.StringUtils.translateAlternateColorCodes;

public class CopyChatListener extends MessageReceiveListener {
    // Taken from https://github.com/SpigotMC/BungeeCord
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§([0-9A-FK-OR]|#[a-f0-9]{6})");

    @Override
    public void onMessageReceive(MessageReceiveEvent e) {
        String tooltip;
        String toCopy = textToLegacy(e.getText());
        if (!ChatUtilsConfig.COPY_COLORS.value()) {
            toCopy = STRIP_COLOR_PATTERN.matcher(toCopy).replaceAll("");
        } else {
            toCopy = STRIP_COLOR_PATTERN.matcher(toCopy).replaceAll(matchResult ->
                    matchResult.group().replace("§", "&"));
        }

        if (ChatUtilsConfig.PREVIEW_CONTENT.value()) {
            tooltip = translateAlternateColorCodes(
                    ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value() + "\n\n&9Preview:\n&f") + toCopy;
        } else {
            tooltip = translateAlternateColorCodes(ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value());
        }

        Style style = e.getText().getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatmacros " + toCopy));
        if (ChatUtilsConfig.TOOLTIP_ENABLED.value()) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.literal(tooltip)));
        }

        if (e.getText().getStyle().getClickEvent() == null && ChatUtilsConfig.ENABLED.value()) {
            e.getTextAsMutable().setStyle(style);
        }
    }

    @Override
    public EventHandler.EventType getType() {
        return EventHandler.EventType.MESSAGE_RECEIVE;
    }
}
