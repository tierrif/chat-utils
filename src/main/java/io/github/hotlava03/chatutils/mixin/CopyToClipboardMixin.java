package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.SendCommandCallback;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class CopyToClipboardMixin {
    @Inject(method = "sendCommand(Ljava/lang/String;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        SendCommandCallback.EVENT.invoker().accept(cir, command);
    }
}
