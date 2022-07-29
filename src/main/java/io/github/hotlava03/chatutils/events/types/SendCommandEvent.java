package io.github.hotlava03.chatutils.events.types;

import io.github.hotlava03.chatutils.events.ChatUtilsEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SendCommandEvent extends ChatUtilsEvent {
    private final String command;

    public SendCommandEvent(CallbackInfoReturnable<Boolean> callbackInfo, String command) {
        super(callbackInfo);
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
