package io.github.hotlava03.chatutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface SendCommandCallback {
    Event<SendCommandCallback> EVENT = EventFactory.createArrayBacked(
            SendCommandCallback.class,
            (listeners) -> (callbackInfo, command) -> {
                for (SendCommandCallback listener : listeners) {
                    listener.accept(callbackInfo, command);
                }
            });

    void accept(CallbackInfo callbackInfo, String command);
}
