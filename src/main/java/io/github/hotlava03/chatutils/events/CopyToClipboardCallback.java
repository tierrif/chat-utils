package io.github.hotlava03.chatutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface CopyToClipboardCallback {
    Event<CopyToClipboardCallback> EVENT = EventFactory.createArrayBacked(
            CopyToClipboardCallback.class,
            (listeners) -> (text) -> {
                for (CopyToClipboardCallback listener : listeners) {
                    listener.accept(text);
                }
            });

    void accept(Text textToCopy);
}
