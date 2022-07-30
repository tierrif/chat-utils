package io.github.hotlava03.chatutils.events;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class MessageReceiveEvent extends Event {
    public static ListenerManager<MessageReceiveEvent> LISTENERS = new ListenerManager<>();

    private final Text text;
    private final List<ChatHudLine.Visible> lines;

    public MessageReceiveEvent(CallbackInfo callbackInfo, Text text, List<ChatHudLine.Visible> lines) {
        super(callbackInfo);
        this.text = text;
        this.lines = lines;
    }

    public Text getText() {
        return this.text;
    }

    public MutableText getTextAsMutable() {
        return (MutableText) this.text;
    }

    public List<ChatHudLine.Visible> getLines() {
        return this.lines;
    }
}
