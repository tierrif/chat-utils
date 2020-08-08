package io.github.hotlava03.chatmacros.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@Mixin(ClientPlayerEntity.class)
public class CopyToClipboardMixin {
    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessageSDM(String message, CallbackInfo info) {
        if (message.startsWith("/chatmacros")) info.cancel();
        else return;
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(
                                message.replaceFirst("/chatmacros ", "")
                                        .replace("§", "&")),
                        null);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("\u00a79ChatMacros \u00a78\u00BB \u00a77Copied to clipboard."));
    }
}
