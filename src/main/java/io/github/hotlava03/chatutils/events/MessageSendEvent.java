package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class MessageSendEvent extends Event {
    public static ListenerManager<MessageSendEvent> LISTENERS = new ListenerManager<>();
    private final String line;

    public MessageSendEvent(CallbackInfo callbackInfo, String line) {
        super(callbackInfo);
        this.line = line;
    }

    public String getLine() {
        return this.line;
    }
}
