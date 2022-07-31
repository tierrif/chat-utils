package io.github.hotlava03.chatutils.events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class JoinServerEvent extends Event {
    public static ListenerManager<JoinServerEvent> LISTENERS = new ListenerManager<>();
    private final List<String> messageHistory;

    public JoinServerEvent(CallbackInfo callbackInfo, List<String> messageHistory) {
        super(callbackInfo);
        this.messageHistory = messageHistory;
    }

    public List<String> getMessageHistory() {
        return messageHistory;
    }
}
