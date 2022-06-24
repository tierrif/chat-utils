package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.Counter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.*;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ReceiveMessageMixin {
    private final Counter counter = new Counter();

    @Mixin(ChatHud.class)
    public interface ChatHudAccessor {
        @Accessor List<ChatHudLine<OrderedText>> getVisibleMessages();
    }

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
                var lines = ((ChatHudAccessor) MinecraftClient.getInstance().inGameHud.getChatHud())
                        .getVisibleMessages();
                lines.remove(0);
                ++messageId;
            } else {
                counter.lastMessage = text;
                counter.spamCounter = 1;
            }
        }
        String tooltip;
        String toCopy = text.getString();
        if (!ChatUtilsConfig.COPY_COLORS.value()) {
            toCopy = STRIP_COLOR_PATTERN.matcher(toCopy).replaceAll("");
        }

        if (ChatUtilsConfig.PREVIEW_CONTENT.value()) {
            tooltip = translateAlternateColorCodes(
                    ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value() + "\n\n&9Preview:\n&f" + toCopy);
        } else {
            tooltip = translateAlternateColorCodes(ChatUtilsConfig.COPY_TO_CLIPBOARD_MESSAGE.value());
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

    // Taken from https://github.com/SpigotMC/BungeeCord
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    private static String translateAlternateColorCodes(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
