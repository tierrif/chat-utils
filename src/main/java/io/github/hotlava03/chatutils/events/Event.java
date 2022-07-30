package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class Event {
    private final CallbackInfo callbackInfo;

    public Event(CallbackInfo callbackInfo) {
        this.callbackInfo = callbackInfo;
    }

    public CallbackInfo getCallbackInfo() {
        return this.callbackInfo;
    }
}
