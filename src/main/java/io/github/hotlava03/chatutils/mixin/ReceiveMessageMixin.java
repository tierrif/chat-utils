package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.EventHandler;
import io.github.hotlava03.chatutils.events.types.MessageReceiveEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public class ReceiveMessageMixin {
    @Mixin(ChatHud.class)
    public interface ChatHudAccessor {
        @Accessor
        List<ChatHudLine.Visible> getVisibleMessages();
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At("HEAD"))
    public void addMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        var lines = ((ChatHudAccessor) MinecraftClient.getInstance().inGameHud.getChatHud())
                .getVisibleMessages();

        var event = new MessageReceiveEvent(ci, message, lines);
        EventHandler.getInstance().fire(EventHandler.EventType.MESSAGE_RECEIVE, event);
    }
}
