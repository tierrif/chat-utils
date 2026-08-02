package io.github.hotlava03.chatutils.mixin;

import java.util.List;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;

@Mixin(ChatComponent.class)
public class ReceiveMessageMixin {
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;"
                    + "Lnet/minecraft/network/chat/MessageSignature;"
                    + "Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;"
                    + "Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
            at = @At("HEAD"))
    public void addMessage(Component message, MessageSignature signature, GuiMessageSource source,
                           GuiMessageTag tag, CallbackInfo ci) {
        ReceiveMessageCallback.EVENT.invoker().accept(message, trimmedMessages);
    }
}
