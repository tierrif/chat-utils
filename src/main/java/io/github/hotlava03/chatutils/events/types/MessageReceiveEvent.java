package io.github.hotlava03.chatutils.events.types;

import io.github.hotlava03.chatutils.events.ChatUtilsEvent;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class MessageReceiveEvent extends ChatUtilsEvent {
    private final Text text;
    private final int messageId;
    private final List<ChatHudLine<OrderedText>> lines;

    public MessageReceiveEvent(CallbackInfo callbackInfo, Text text, int messageId, List<ChatHudLine<OrderedText>> lines) {
        super(callbackInfo);
        this.text = text;
        this.messageId = messageId;
        this.lines = lines;
    }

    public Text getText() {
        return this.text;
    }

    public MutableText getTextAsMutable() {
        return (MutableText) this.text;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public List<ChatHudLine<OrderedText>> getLines() {
        return this.lines;
    }
}
