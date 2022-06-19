package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.Counter;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(ChatHud.class)
public class ReceiveMessageMixin {
    private final Counter counter = new Counter();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"))
    public void addMessage(Text text, int messageId, CallbackInfo info) {
        antiSpam:
        if (ChatUtilsConfig.ANTI_SPAM.value()) {
            double prejudice = 0;
            if (counter.lastMessage == null) {
                counter.lastMessage = text;
                counter.spamCounter = 1;
                break antiSpam;
            }
            if (getDifference(text.getString(), counter.lastMessage.getString()) <= prejudice) {
                counter.spamCounter++;
                ((MutableText) text).append(" \u00a78[\u00a7c" + counter.spamCounter + "x\u00a78]");
                try {
                    // FIELD field_2064 visibleMessages Ljava/util/List;
                    Field field = ChatHud.class.getDeclaredField("field_2064"); // TODO: sus
                    field.setAccessible(true);
                    List<?> lines = (List<?>) field.get(MinecraftClient.getInstance().inGameHud.getChatHud().getMessageHistory());
                    lines.remove(0);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                ++messageId;
            } else {
                counter.lastMessage = text;
                counter.spamCounter = 1;
            }
        }
        String tooltip;
        String toCopy = text.getString();
        if (!ChatUtilsConfig.COPY_COLORS.value()) {
            toCopy = ChatColor.stripColor(toCopy);
        }

        if (ChatUtilsConfig.PREVIEW_CONTENT.value()) {
            tooltip = ChatColor.translateAlternateColorCodes('&',
                    ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value() + "\n\n&9Preview:\n&f" + toCopy);
        } else {
            tooltip = ChatColor.translateAlternateColorCodes('&',
                    ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value());
        }

        Style style = text.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatmacros " + toCopy));
        if (ChatUtilsConfig.TOOLTIP_ENABLED.value()) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Text.literal(tooltip)));
        }

        if (text.getStyle().getClickEvent() == null && ChatUtilsConfig.ENABLED.value()) {
            ((MutableText) text).setStyle(style);
        }
    }

    /*
     * This method does not belong to me.
     * Original source:
     * https://github.com/killjoy1221/TabbyChat-2/blob/master/src/main/java/mnm/mods/tabbychat/extra/ChatAddonAntiSpam.java
     */
    private double getDifference(String s1, String s2) {
        double avgLen = (s1.length() + s2.length()) / 2D;
        if (avgLen == 0) {
            return 0;
        }
        return StringUtils.getLevenshteinDistance(s1.toLowerCase(), s2.toLowerCase()) / avgLen;
    }
}
