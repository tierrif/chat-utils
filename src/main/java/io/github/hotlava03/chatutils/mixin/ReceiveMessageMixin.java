package io.github.hotlava03.chatutils.mixin;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;
import io.github.hotlava03.chatutils.config.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.Counter;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;


@Mixin(ClientPlayNetworkHandler.class)
public class ReceiveMessageMixin {
    private final Counter counter = new Counter();
    private static boolean firstRun = false;

    @Inject(method = "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V", at = @At("HEAD"))
    public void addMessage(GameMessageS2CPacket packet, CallbackInfo info) {
        if (packet.getType() != MessageType.SYSTEM) return;
        firstRun = !firstRun;
        if (!firstRun) return;
        antiSpam:
        if (((ConfigBoolean) ChatUtilsConfig.OPTIONS.get(3)).getBooleanValue()) {
            if (counter.lastMessage == null) {
                counter.lastMessage = packet.getMessage();
                counter.spamCounter = 1;
                break antiSpam;
            }

            List<ChatHudLine<OrderedText>> visibleMessages = getVisibleChatLines();
            System.out.println("First: " + packet.getMessage().getString());
            System.out.println("Second: " + counter.lastMessage.getString());
            if (packet.getMessage().getString().equalsIgnoreCase(counter.lastMessage.getString())) {
                counter.spamCounter++;
                ((MutableText) packet.getMessage()).append(" \u00a78[\u00a7c" + counter.spamCounter + "x\u00a78]");

                ChatHud hud = MinecraftClient.getInstance().inGameHud.getChatHud();
                int width = MathHelper.floor((double) hud.getWidth() / hud.getChatScale());
                int size = ChatMessages.breakRenderedChatMessageLines(
                        packet.getMessage(), width, MinecraftClient.getInstance().textRenderer).size();

                System.out.println(size);
                visibleMessages.subList(0, size).clear();
            } else {
                counter.lastMessage = packet.getMessage();
                counter.spamCounter = 1;
            }
        }

        String tooltip;
        String toCopy = packet.getMessage().getString();
        if (!((ConfigBoolean) ChatUtilsConfig.OPTIONS.get(2)).getBooleanValue()) {
            toCopy = ChatColor.stripColor(toCopy);
        }

        if (((ConfigBoolean) ChatUtilsConfig.OPTIONS.get(1)).getBooleanValue()) {
            tooltip = ChatColor.translateAlternateColorCodes('&',
                    ((ConfigString) ChatUtilsConfig.OPTIONS.get(0)).getStringValue() + "\n\n&9Preview:\n&f" +
                            toCopy);
        } else {
            tooltip = ChatColor.translateAlternateColorCodes('&',
                    ((ConfigString) ChatUtilsConfig.OPTIONS.get(0)).getStringValue());
        }

        Style style = packet.getMessage().getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatmacros " + toCopy));
        if (((ConfigBoolean) ChatUtilsConfig.OPTIONS.get(4)).getBooleanValue()) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new LiteralText(tooltip)));
        }

        if (packet.getMessage().getStyle().getClickEvent() == null && ((ConfigBoolean) ChatUtilsConfig.OPTIONS.get(5)).getBooleanValue()) {
            ((MutableText) packet.getMessage()).setStyle(style);
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

    @SuppressWarnings("unchecked")
    private List<ChatHudLine<Text>> getCurrentChatLines() {
        List<ChatHudLine<Text>> lines = null;
        try {
            // FIELD field_2061 messages Ljava/util/List;
            Field field = ChatHud.class.getDeclaredField("field_2061");
            field.setAccessible(true);
            lines = (List<ChatHudLine<Text>>) field.get(MinecraftClient.getInstance().inGameHud.getChatHud());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return lines;
    }

    @SuppressWarnings("unchecked")
    private List<ChatHudLine<OrderedText>> getVisibleChatLines() {
        List<ChatHudLine<OrderedText>> lines = null;
        try {
            // FIELD field_2064 visibleMessages Ljava/util/List;
            Field field = ChatHud.class.getDeclaredField("field_2064");
            field.setAccessible(true);
            lines = (List<ChatHudLine<OrderedText>>) field.get(MinecraftClient.getInstance().inGameHud.getChatHud());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return lines;
    }
}
