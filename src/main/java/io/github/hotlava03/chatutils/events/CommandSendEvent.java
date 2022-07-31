package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CommandSendEvent extends Event {
    public static ListenerManager<CommandSendEvent> LISTENERS = new ListenerManager<>();

    private final String command;

    public CommandSendEvent(CallbackInfoReturnable<Boolean> callbackInfo, String command) {
        super(callbackInfo);
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
