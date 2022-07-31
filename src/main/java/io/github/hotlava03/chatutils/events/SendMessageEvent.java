package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SendMessageEvent extends Event {
    public static ListenerManager<SendMessageEvent> LISTENERS = new ListenerManager<>();
    private final String line;

    public SendMessageEvent(CallbackInfo callbackInfo, String line) {
        super(callbackInfo);
        this.line = line;
    }

    public String getLine() {
        return this.line;
    }
}
