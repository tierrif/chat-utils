package io.github.hotlava03.chatmacros.mixin;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;
import io.github.hotlava03.chatmacros.config.ChatMacrosConfig;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ChatHud.class)
public class ReceiveMessageMixin {
    @Inject(method = "addMessage", at = @At("HEAD"))
    public void addMessage(Text text, CallbackInfo info) {
        String tooltip;
        String toCopy = text.getString();
        if (!((ConfigBoolean) ChatMacrosConfig.OPTIONS.get(2)).getBooleanValue()) {
            toCopy = ChatColor.stripColor(toCopy);
        }

        if (((ConfigBoolean) ChatMacrosConfig.OPTIONS.get(1)).getBooleanValue()) {
            tooltip = ChatColor.translateAlternateColorCodes('&',
                    ((ConfigString) ChatMacrosConfig.OPTIONS.get(0)).getStringValue() + "\n\n&9Preview:\n" +
                            toCopy);
        } else {
            tooltip = ChatColor.translateAlternateColorCodes('&',
                    ((ConfigString) ChatMacrosConfig.OPTIONS.get(0)).getStringValue());
        }

        Style style = text.getStyle()
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new LiteralText(tooltip)))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatmacros " + toCopy));
        if (text.getStyle().getClickEvent() == null && ((ConfigBoolean) ChatMacrosConfig.OPTIONS.get(3)).getBooleanValue()) {
            ((MutableText) text).setStyle(style);
        }
    }
}
