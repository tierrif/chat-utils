package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class JoinServerEvent extends Event {
    public static ListenerManager<JoinServerEvent> LISTENERS = new ListenerManager<>();

    public JoinServerEvent(CallbackInfo callbackInfo) {
        super(callbackInfo);
    }
}
