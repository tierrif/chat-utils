package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.MessageSendEvent;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class SendMessageMixin {
    @Shadow public abstract List<String> getMessageHistory();

    @Inject(method = "addToMessageHistory", at = @At("RETURN"))
    public void addToMessageHistory(String message, CallbackInfo info) {
        var size = getMessageHistory().size();
        if (size == 0) return;
        MessageSendEvent.LISTENERS.fire(new MessageSendEvent(info, getMessageHistory().get(size - 1)));
    }
}
