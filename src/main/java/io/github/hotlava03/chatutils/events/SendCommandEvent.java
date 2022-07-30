package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SendCommandEvent extends Event {
    public static ListenerManager<SendCommandEvent> LISTENERS = new ListenerManager<>();

    private final String command;

    public SendCommandEvent(CallbackInfoReturnable<Boolean> callbackInfo, String command) {
        super(callbackInfo);
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
