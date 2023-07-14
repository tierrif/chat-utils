package io.github.hotlava03.chatutils.mixin;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.ChatHudUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHud.class)
public abstract class ChatClickMixin {

    @Inject(method = "mouseClicked",
            at = @At("HEAD"))
    private void onChatClick(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof ChatScreen)) return;

        if (ChatUtilsConfig.ENABLE_COPY_KEY.value()) {
            if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
                    ChatUtilsConfig.COPY_KEY.value())) {
                return;
            }
        }

        var message = ChatHudUtils.getMessageAt(mouseX, mouseY);
        if (message != null) {
            CopyToClipboardCallback.EVENT.invoker().accept(message.content().asComponent(), message.creationTick());
        }
    }
}
