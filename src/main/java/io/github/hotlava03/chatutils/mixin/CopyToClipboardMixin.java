package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.EventHandler;
import io.github.hotlava03.chatutils.events.types.SendCommandEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class CopyToClipboardMixin {
    @Inject(method = "sendCommand(Ljava/lang/String;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        var event = new SendCommandEvent(cir, command);
        EventHandler.getInstance().fire(EventHandler.EventType.SEND_COMMAND, event);
    }
}
