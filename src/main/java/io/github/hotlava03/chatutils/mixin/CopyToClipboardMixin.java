package io.github.hotlava03.chatutils.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ClientPlayerEntity.class)
public class CopyToClipboardMixin {
    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessageSDM(String message, CallbackInfo info) {
        if (message.startsWith("/chatmacros")) info.cancel();
        else return;
        /*Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(
                                message.replaceFirst("/chatmacros ", "")
                                        .replace("§", "&")),
                        null);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("\u00a79ChatUtils \u00a78\u00BB \u00a77Copied to clipboard."));*/
        SystemToast.show(MinecraftClient.getInstance().getToastManager(),
                SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER,
                new LiteralText("ChatUtils"),
                new LiteralText("Text copied.")
        );
    }
}
