package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.*;

import java.util.List;

import static io.github.hotlava03.chatutils.util.StringUtils.*;

public class InjectChatCopyListener implements ReceiveMessageCallback {
    @Override
    public void accept(Text text, List<ChatHudLine.Visible> lines) {
        String toCopy = ChatUtilsConfig.COPY_COLORS.value()
                ? componentToPlainText(text.asComponent())
                : componentToLegacy(text.asComponent(), ChatUtilsConfig.COPY_HEX_COLORS.value());

        Style style = text.getStyle();
        if (ChatUtilsConfig.ENABLED.value() && ChatUtilsConfig.TOOLTIP_ENABLED.value() &&
                text.getStyle().getHoverEvent() == null) {
            Component tooltip = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value());

            if (ChatUtilsConfig.PREVIEW_CONTENT.value()) {
                tooltip = tooltip.append(Component.text("\n\n"))
                        .append(Component.text("Preview:\n").color(NamedTextColor.BLUE))
                        .append(Component.text(toCopy).color(NamedTextColor.WHITE));
            }

            ((MutableText) text).setStyle(style.withHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    FabricClientAudiences.of().toNative(tooltip)
            )));
        }
    }
}
