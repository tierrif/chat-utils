package io.github.hotlava03.chatutils.events.types;

import io.github.hotlava03.chatutils.events.ChatUtilsEvent;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SendCommandEvent extends ChatUtilsEvent {
    private final ChatMessageSigner signer;
    private final String command;
    private final Text preview;

    public SendCommandEvent(CallbackInfo callbackInfo, ChatMessageSigner signer, String command, Text preview) {
        super(callbackInfo);
        this.signer = signer;
        this.command = command;
        this.preview = preview;
    }

    public ChatMessageSigner getSigner() {
        return this.signer;
    }

    public String getCommand() {
        return this.command;
    }

    public Text getPreview() {
        return this.preview;
    }
}
