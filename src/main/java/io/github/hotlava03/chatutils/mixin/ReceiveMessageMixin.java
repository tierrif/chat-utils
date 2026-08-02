package io.github.hotlava03.chatutils.mixin;

import java.util.List;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;

@Mixin(ChatComponent.class)
public class ReceiveMessageMixin {
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/network/chat/Component;"
                    + "Lnet/minecraft/network/chat/MessageSignature;"
                    + "Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;"
                    + "Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    public Component addMessage(Component message) {
        return ReceiveMessageCallback.EVENT.invoker()
                .accept(message, trimmedMessages);
    }
}
