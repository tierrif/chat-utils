package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ChatUtilsEvent {
    private final CallbackInfo callbackInfo;

    public ChatUtilsEvent(CallbackInfo callbackInfo) {
        this.callbackInfo = callbackInfo;
    }

    public CallbackInfo getCallbackInfo() {
        return this.callbackInfo;
    }
}
